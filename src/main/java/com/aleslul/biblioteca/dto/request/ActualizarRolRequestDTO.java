package com.aleslul.biblioteca.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ActualizarRolRequestDTO {

    @Positive(message = "Debe especificar un rol válido")
    private int idRol;
}