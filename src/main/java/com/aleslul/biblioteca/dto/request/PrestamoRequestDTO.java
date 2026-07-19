package com.aleslul.biblioteca.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PrestamoRequestDTO {

    @Positive(message = "Debe especificar un usuario válido")
    private int idUsuario;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser posterior a hoy")
    private LocalDate fechaVencimiento;

    @NotEmpty(message = "Debe incluir al menos un libro en el préstamo")
    private List<Integer> idLibros;
}