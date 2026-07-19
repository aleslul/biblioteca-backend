package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.exception.ReglaNegocioException;
import com.aleslul.biblioteca.model.*;
import com.aleslul.biblioteca.model.enums.*;
import com.aleslul.biblioteca.repository.*;
import com.aleslul.biblioteca.service.DevolucionService;
import com.aleslul.biblioteca.service.LogSistemaService;
import com.aleslul.biblioteca.service.MultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DevolucionServiceImpl implements DevolucionService {

    @Autowired
    private MultaService multaService;

    @Autowired
    private LogSistemaService logSistemaService;

    @Autowired
    private DevolucionRepository devolucionRepository;

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private DetallePrestamoRepository detallePrestamoRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private MultaRepository multaRepository;

    @Override
    @Transactional
    public Devolucion registrarDevolucion(int idPrestamo) {
        // 1. Obtener préstamo activo
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con ID: " + idPrestamo)); // Reemplazado[cite: 2]

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new ReglaNegocioException("Este préstamo ya fue devuelto previamente"); // Reemplazado[cite: 2]
        }

        // 2. Obtener los libros pendientes de este préstamo (puede haber varios)
        List<DetallePrestamo> detallesPendientes = detallePrestamoRepository.findByPrestamoIdAndDevueltoFalse(idPrestamo);
        if (detallesPendientes.isEmpty()) {
            throw new ReglaNegocioException("Este préstamo no tiene libros pendientes de devolución");
        }

        LocalDateTime ahora = LocalDateTime.now();

        // 3. Calcular días de retraso exactos[cite: 2]
        long dias = ChronoUnit.DAYS.between(prestamo.getFechaVencimiento(), ahora);
        int diasRetraso = dias > 0 ? (int) dias : 0;
        boolean requiereMulta = diasRetraso > 0;

        // 4. Crear y persistir la devolución[cite: 2]
        Devolucion devolucion = new Devolucion();
        devolucion.setPrestamo(prestamo);
        devolucion.setFechaDevolucion(ahora);
        devolucion.setDiasRetraso(diasRetraso);
        devolucion.setConMulta(requiereMulta);
        Devolucion devolucionGuardada = devolucionRepository.save(devolucion);

        // 5. Actualizar estados del dominio circular[cite: 2]
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamoRepository.save(prestamo);

        // 6. Marcar cada libro del préstamo como devuelto y disponible de nuevo
        BigDecimal valorTotalLibros = BigDecimal.ZERO;
        for (DetallePrestamo detalle : detallesPendientes) {
            detalle.setDevuelto(true);
            detallePrestamoRepository.save(detalle);

            Libro libro = detalle.getLibro();
            libro.setCopiesAvailable(libro.getCopiesAvailable() + 1);
            libroRepository.save(libro);

            valorTotalLibros = valorTotalLibros.add(libro.getPrecio());
        }

        // 7. Si hay mora, disparar de forma automática la creación de la Multa
        // El cálculo se basa en el valor total de los libros del préstamo (uno o varios)
        if (requiereMulta) {
            Multa multa = new Multa();
            multa.setDevolucion(devolucionGuardada);
            multa.setPrecioLibro(valorTotalLibros);
            multa.setEstado(EstadoMulta.PENDIENTE);
            multa.setFechaCalculo(ahora.toLocalDate());

            // Invoca la regla de negocio matemática
            multa.setMontoTotal(multaService.calcularAlgoritmoMulta(diasRetraso, valorTotalLibros));
            multaRepository.save(multa);

            logSistemaService.registrarAccion(
                    prestamo.getUsuario().getId(),
                    TipoAccion.NOTIFICACION_MULTA_APLICADA,
                    "Multa de S/. " + multa.getMontoTotal() + " aplicada por " + diasRetraso + " día(s) de retraso (préstamo ID " + idPrestamo + ")");
        }
        return devolucionGuardada;
    }
}