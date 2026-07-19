package com.aleslul.biblioteca.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibroMasSolicitadoResponseDTO {
    private int idLibro;
    private String titulo;
    private String autor;
    private long cantidadPrestamos;
}
