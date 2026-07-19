package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.dto.response.LibroMasSolicitadoResponseDTO;
import com.aleslul.biblioteca.dto.response.ReporteCirculacionResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    // RF9: fechaInicio/fechaFin nulos = histórico completo, sin acotar
    ReporteCirculacionResponseDTO generarReporteCirculacion(LocalDate fechaInicio, LocalDate fechaFin);

    List<LibroMasSolicitadoResponseDTO> obtenerLibrosMasSolicitados(LocalDate fechaInicio, LocalDate fechaFin, int limite);
}
