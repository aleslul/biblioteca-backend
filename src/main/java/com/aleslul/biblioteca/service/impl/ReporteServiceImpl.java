package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.dto.response.LibroMasSolicitadoResponseDTO;
import com.aleslul.biblioteca.dto.response.ReporteCirculacionResponseDTO;
import com.aleslul.biblioteca.model.enums.EstadoPrestamo;
import com.aleslul.biblioteca.repository.DetallePrestamoRepository;
import com.aleslul.biblioteca.repository.DevolucionRepository;

import com.aleslul.biblioteca.model.enums.TipoAccion;
import com.aleslul.biblioteca.repository.PrestamoRepository;
import com.aleslul.biblioteca.security.UsuarioAutenticadoHelper;
import com.aleslul.biblioteca.service.LogSistemaService;
import com.aleslul.biblioteca.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {
    @Autowired
    private DetallePrestamoRepository detallePrestamoRepository;

    @Autowired
    private LogSistemaService logSistemaService;

    @Autowired
    private UsuarioAutenticadoHelper usuarioAutenticadoHelper;

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private DevolucionRepository devolucionRepository;

    @Override
    public ReporteCirculacionResponseDTO generarReporteCirculacion(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime desde = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime hasta = fechaFin != null ? fechaFin.atTime(LocalTime.MAX) : null;

        ReporteCirculacionResponseDTO reporte = new ReporteCirculacionResponseDTO();
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);

        reporte.setTotalPrestamos(prestamoRepository.contarPrestamos(desde, hasta));
        reporte.setPrestamosActivos(prestamoRepository.contarPrestamosPorEstado(EstadoPrestamo.ACTIVO, desde, hasta));
        reporte.setPrestamosRenovados(prestamoRepository.contarPrestamosPorEstado(EstadoPrestamo.RENOVADO, desde, hasta));
        reporte.setPrestamosDevueltos(prestamoRepository.contarPrestamosPorEstado(EstadoPrestamo.DEVUELTO, desde, hasta));
        reporte.setPrestamosVencidos(prestamoRepository.contarPrestamosPorEstado(EstadoPrestamo.VENCIDO, desde, hasta));

        reporte.setTotalDevoluciones(devolucionRepository.contarDevoluciones(desde, hasta));
        reporte.setDevolucionesConMulta(devolucionRepository.contarDevolucionesConMulta(desde, hasta));

        logSistemaService.registrarAccion(
                usuarioAutenticadoHelper.obtenerIdUsuarioActual(),
                TipoAccion.REPORTE_CIRCULACION,
                "Reporte de circulación generado" + (fechaInicio != null || fechaFin != null
                        ? " (" + fechaInicio + " a " + fechaFin + ")" : " (histórico completo)"));

        return reporte;
    }

    @Override
    public List<LibroMasSolicitadoResponseDTO> obtenerLibrosMasSolicitados(LocalDate fechaInicio, LocalDate fechaFin, int limite) {
        LocalDateTime desde = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        LocalDateTime hasta = fechaFin != null ? fechaFin.atTime(LocalTime.MAX) : null;

        int limiteEfectivo = limite > 0 ? limite : 10;
        Pageable pageable = PageRequest.of(0, limiteEfectivo);

        List<LibroMasSolicitadoResponseDTO> resultado =
                detallePrestamoRepository.obtenerLibrosMasSolicitados(desde, hasta, pageable);

        logSistemaService.registrarAccion(
                usuarioAutenticadoHelper.obtenerIdUsuarioActual(),
                TipoAccion.REPORTE_LIBROS_SOLICITADOS,
                "Reporte de libros más solicitados generado (top " + limiteEfectivo + ")");

        return resultado;
    }
}
