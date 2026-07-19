package com.aleslul.biblioteca.dto.response;

import lombok.Data;

@Data
public class DetallePrestamoResponseDTO {
    private int idDetalle;
    private int idLibro;
    private String tituloLibro;
    private boolean devuelto;
}