package com.aleslul.biblioteca.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ReservaRequestDTO {

    @Positive(message = "Debe especificar un libro válido")
    private int idLibro;

    @Positive(message = "Debe especificar un usuario válido")
    private int idUsuario;
}