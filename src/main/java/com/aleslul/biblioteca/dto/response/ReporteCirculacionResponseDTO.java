package com.aleslul.biblioteca.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ReporteCirculacionResponseDTO {
    private LocalDate fechaInicio;   // null = sin límite inferior
    private LocalDate fechaFin;      // null = sin límite superior
    private long totalPrestamos;
    private long prestamosActivos;
    private long prestamosRenovados;
    private long prestamosDevueltos;
    private long prestamosVencidos;
    private long totalDevoluciones;
    private long devolucionesConMulta;
}
