package com.aleslul.biblioteca.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DevolucionRequestDTO {

    @Positive(message = "Debe especificar un préstamo válido")
    private int idPrestamo;
}