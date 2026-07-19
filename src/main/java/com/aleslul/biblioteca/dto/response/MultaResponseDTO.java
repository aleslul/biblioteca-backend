package com.aleslul.biblioteca.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MultaResponseDTO {
    private int id;
    private int idDevolucion;
    private BigDecimal montoTotal;
    private BigDecimal precioLibro;
    private String estado;
    private LocalDate fechaCalculo;

    // Campos enriquecidos que el frontend necesita para pintar la tabla de multas
    // sin tener que hacer joins manuales (Multa -> Devolucion -> Prestamo -> Usuario/Libro)
    private int idPrestamo;
    private int idUsuario;
    private String memberName;
    private String bookTitle;
    private int daysOverdue;
}