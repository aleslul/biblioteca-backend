package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.response.LibroMasSolicitadoResponseDTO;
import com.aleslul.biblioteca.dto.response.ReporteCirculacionResponseDTO;
import com.aleslul.biblioteca.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

// RF9: reportes de circulación y libros más solicitados (prioridad Baja/Won't del backlog)
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // GET /api/reportes/circulacion?fechaInicio=2026-01-01&fechaFin=2026-06-30
    // Sin parámetros = histórico completo
    @GetMapping("/circulacion")
    public ResponseEntity<ReporteCirculacionResponseDTO> obtenerReporteCirculacion(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reporteService.generarReporteCirculacion(fechaInicio, fechaFin));
    }

    // GET /api/reportes/libros-mas-solicitados?limite=10
    @GetMapping("/libros-mas-solicitados")
    public ResponseEntity<List<LibroMasSolicitadoResponseDTO>> obtenerLibrosMasSolicitados(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(reporteService.obtenerLibrosMasSolicitados(fechaInicio, fechaFin, limite));
    }
}
