package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.dto.response.DetallePrestamoResponseDTO;
import com.aleslul.biblioteca.dto.request.PrestamoRequestDTO;
import com.aleslul.biblioteca.dto.response.PrestamoResponseDTO;
import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.exception.ReglaNegocioException;
import com.aleslul.biblioteca.model.DetallePrestamo;
import com.aleslul.biblioteca.model.Libro;
import com.aleslul.biblioteca.model.Prestamo;
import com.aleslul.biblioteca.model.Reserva;
import com.aleslul.biblioteca.model.Usuario;
import com.aleslul.biblioteca.model.enums.EstadoMulta;
import com.aleslul.biblioteca.model.enums.EstadoPrestamo;
import com.aleslul.biblioteca.model.enums.EstadoReserva;
import com.aleslul.biblioteca.model.enums.TipoAccion;
import com.aleslul.biblioteca.repository.DetallePrestamoRepository;
import com.aleslul.biblioteca.repository.LibroRepository;
import com.aleslul.biblioteca.repository.MultaRepository;
import com.aleslul.biblioteca.repository.PrestamoRepository;
import com.aleslul.biblioteca.repository.ReservaRepository;
import com.aleslul.biblioteca.repository.UsuarioRepository;
import com.aleslul.biblioteca.service.LogSistemaService;
import com.aleslul.biblioteca.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.aleslul.biblioteca.exception.AccesoDenegadoException;
import com.aleslul.biblioteca.model.enums.NombreRol;
import com.aleslul.biblioteca.security.UsuarioAutenticadoHelper;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private DetallePrestamoRepository detallePrestamoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private MultaRepository multaRepository;

    @Autowired
    private LogSistemaService logSistemaService;

    @Autowired
    private UsuarioAutenticadoHelper usuarioAutenticadoHelper;

    // DS07 - RF-07: ventana de renovación (horas antes del vencimiento) y días que se extiende
    @Value("${app.prestamos.horas-ventana-renovacion}")
    private long horasVentanaRenovacion;

    @Value("${app.prestamos.dias-extension-renovacion}")
    private long diasExtensionRenovacion;

    @Override
    @Transactional
    public PrestamoResponseDTO registrarPrestamo(PrestamoRequestDTO requestDTO) {
        // 1. Validar existencia del usuario
        Usuario usuario = usuarioRepository.findById(requestDTO.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no registrado en el sistema con ID: " + requestDTO.getIdUsuario()));

        // 1.0 Un usuario final solo puede solicitar préstamos para sí mismo (autoservicio).
        // ADMINISTRADOR/BIBLIOTECARIO conservan la posibilidad de registrar a nombre de cualquier usuario (ej. en el mostrador).
        Usuario usuarioAutenticado = usuarioAutenticadoHelper.obtenerUsuarioActual();
        boolean esUsuarioFinal = usuarioAutenticado.getRol().getNombre() == NombreRol.USUARIO_FINAL;
        if (esUsuarioFinal && !usuarioAutenticado.getId().equals(requestDTO.getIdUsuario())) {
            throw new AccesoDenegadoException("Un usuario no puede solicitar préstamos a nombre de otro usuario");
        }

        // 1.1. Bloquear si el usuario tiene multas pendientes de pago
        if (multaRepository.existsByDevolucion_Prestamo_Usuario_IdAndEstado(usuario.getId(), EstadoMulta.PENDIENTE)) {
            throw new ReglaNegocioException("El usuario tiene multas pendientes de pago; no puede registrar nuevos préstamos/reservas/renovaciones");
        }

        // 2. Instanciar la cabecera del préstamo
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(LocalDateTime.now());

        LocalDateTime fechaVencimiento = requestDTO.getFechaVencimiento() != null
                ? requestDTO.getFechaVencimiento().atStartOfDay()
                : LocalDateTime.now().plusDays(7);
        prestamo.setFechaVencimiento(fechaVencimiento);
        prestamo.setEstado(EstadoPrestamo.ACTIVO);

        Prestamo prestamoGuardado = prestamoRepository.save(prestamo);

        // 3. Registrar los detalles físicos del libro y cambiar su disponibilidad
        List<DetallePrestamo> detallesGuardados = new ArrayList<>();
        for (Integer idLibro : requestDTO.getIdLibros()) {
            Libro libro = libroRepository.findById(idLibro)
                    .orElseThrow(() -> new RecursoNoEncontradoException("El libro con ID " + idLibro + " no existe"));

            if (!libro.isDisponible()) {
                throw new ReglaNegocioException("El libro '" + libro.getTitulo() + "' no tiene copias disponibles");
            }

            // --- INICIO LÓGICA RF9: Gestión y validación de reservas ---
            // Buscar si este libro tiene reservas pendientes (por orden de llegada)
            List<Reserva> reservasActivas = reservaRepository.findByLibroIdAndEstadoOrderByFechaReservaAsc(libro.getId(), EstadoReserva.ACTIVA);

            if (!reservasActivas.isEmpty()) {
                Reserva primeraReserva = reservasActivas.get(0);

                // Si la primera reserva en fila le pertenece a OTRA persona, bloqueamos el préstamo
                if (!primeraReserva.getUsuario().getId().equals(usuario.getId())) {
                    throw new ReglaNegocioException("El libro '" + libro.getTitulo() +
                            "' está reservado por el usuario: " + primeraReserva.getUsuario().getNombre());
                } else {
                    // Si la reserva le pertenece a ESTE mismo usuario, cerramos el ciclo marcándola como ATENDIDA
                    primeraReserva.setEstado(EstadoReserva.ATENDIDA);
                    reservaRepository.save(primeraReserva);
                }
            }
            // --- FIN LÓGICA RF9 ---

            // Descontar una copia disponible del stock (ya no se marca el "único" ejemplar como ocupado)
            libro.setCopiesAvailable(libro.getCopiesAvailable() - 1);
            libroRepository.save(libro);

            DetallePrestamo detalle = new DetallePrestamo();
            detalle.setPrestamo(prestamoGuardado);
            detalle.setLibro(libro);
            detalle.setDevuelto(false);

            detallesGuardados.add(detallePrestamoRepository.save(detalle));
        }

        // 4. Escribir huella digital de auditoría en Logs
        logSistemaService.registrarAccion(
                usuario.getId(),
                TipoAccion.REGISTRO_PRESTAMO,
                "Préstamo ID " + prestamoGuardado.getId() + " registrado. Libros prestados: " + requestDTO.getIdLibros().size()
        );

        return convertToDTO(prestamoGuardado, detallesGuardados);
    }

    @Override
    public PrestamoResponseDTO obtenerPorId(int id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con ID: " + id)); // Reemplazado[cite: 2]
        List<DetallePrestamo> detalles = detallePrestamoRepository.findByPrestamoId(id);
        return convertToDTO(prestamo, detalles);
    }

    @Override
    public List<PrestamoResponseDTO> obtenerTodos() {
        return prestamoRepository.findAll().stream()
                .map(prestamo -> {
                    List<DetallePrestamo> detalles = detallePrestamoRepository.findByPrestamoId(prestamo.getId());
                    return convertToDTO(prestamo, detalles);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PrestamoResponseDTO> obtenerPorUsuario(int idUsuario) {
        return prestamoRepository.findByUsuarioId(idUsuario).stream()
                .map(prestamo -> {
                    List<DetallePrestamo> detalles = detallePrestamoRepository.findByPrestamoId(prestamo.getId());
                    return convertToDTO(prestamo, detalles);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PrestamoResponseDTO renovarPrestamo(int idPrestamo) {
        // 1. Validar existencia del préstamo
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Préstamo no encontrado con ID: " + idPrestamo));

        // 1.1. Bloquear si el usuario tiene multas pendientes de pago
        if (multaRepository.existsByDevolucion_Prestamo_Usuario_IdAndEstado(prestamo.getUsuario().getId(), EstadoMulta.PENDIENTE)) {
            throw new ReglaNegocioException("El usuario tiene multas pendientes de pago; no puede registrar nuevos préstamos/reservas/renovaciones");
        }

        // 2. Solo se puede renovar un préstamo vigente (ACTIVO o ya RENOVADO antes)
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new ReglaNegocioException("No se puede renovar un préstamo que ya fue devuelto");
        }
        if (prestamo.getEstado() == EstadoPrestamo.VENCIDO) {
            throw new ReglaNegocioException("No se puede renovar un préstamo vencido");
        }

        // 3. Validar la ventana de renovación: solo dentro de las N horas previas al vencimiento
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioVentana = prestamo.getFechaVencimiento().minusHours(horasVentanaRenovacion);
        if (ahora.isBefore(inicioVentana)) {
            throw new ReglaNegocioException(
                    "La renovación solo está disponible dentro de las " + horasVentanaRenovacion +
                            " horas previas al vencimiento (" + prestamo.getFechaVencimiento() + ")");
        }
        if (ahora.isAfter(prestamo.getFechaVencimiento())) {
            throw new ReglaNegocioException("El préstamo ya venció, no se puede renovar (debe regularizarse primero)");
        }

        // 4. Validar que ninguno de los libros del préstamo tenga una reserva activa de otro usuario
        List<DetallePrestamo> detalles = detallePrestamoRepository.findByPrestamoId(idPrestamo);
        for (DetallePrestamo detalle : detalles) {
            Libro libro = detalle.getLibro();
            List<Reserva> reservasActivas = reservaRepository.findByLibroIdAndEstado(libro.getId(), EstadoReserva.ACTIVA);
            if (!reservasActivas.isEmpty()) {
                throw new ReglaNegocioException(
                        "No se puede renovar: el libro '" + libro.getTitulo() + "' tiene una reserva activa de otro usuario");
            }
        }

        // 5. Extender la fecha de vencimiento y marcar como RENOVADO
        prestamo.setFechaVencimiento(prestamo.getFechaVencimiento().plusDays(diasExtensionRenovacion));
        prestamo.setEstado(EstadoPrestamo.RENOVADO);
        Prestamo prestamoRenovado = prestamoRepository.save(prestamo);

        logSistemaService.registrarAccion(
                prestamo.getUsuario().getId(),
                TipoAccion.RENOVACION_PRESTAMO,
                "Préstamo ID " + idPrestamo + " renovado. Nueva fecha de vencimiento: " + prestamoRenovado.getFechaVencimiento()
        );

        return convertToDTO(prestamoRenovado, detalles);
    }

    private PrestamoResponseDTO convertToDTO(Prestamo prestamo, List<DetallePrestamo> detalles) {
        PrestamoResponseDTO dto = new PrestamoResponseDTO();
        dto.setId(prestamo.getId());
        dto.setIdUsuario(prestamo.getUsuario().getId());
        dto.setNombreUsuario(prestamo.getUsuario().getNombre());
        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
        dto.setFechaVencimiento(prestamo.getFechaVencimiento());
        dto.setEstado(prestamo.getEstado().name());

        List<DetallePrestamoResponseDTO> detalleDTOs = detalles.stream().map(d -> {
            DetallePrestamoResponseDTO detDto = new DetallePrestamoResponseDTO();
            detDto.setIdDetalle(d.getId());
            detDto.setIdLibro(d.getLibro().getId());
            detDto.setTituloLibro(d.getLibro().getTitulo());
            detDto.setDevuelto(d.getDevuelto());
            return detDto;
        }).collect(Collectors.toList());

        dto.setDetalles(detalleDTOs);
        return dto;
    }
}