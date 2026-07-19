package com.aleslul.biblioteca.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LibroResponseDTO {
    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private String categoria;
    private BigDecimal precio;
    private boolean disponible;
}