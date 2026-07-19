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
}