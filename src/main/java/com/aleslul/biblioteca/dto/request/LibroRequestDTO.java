package com.aleslul.biblioteca.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LibroRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    private String autor;

    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Positive(message = "El año de publicación debe ser válido")
    private Integer anioPublicacion;

    private String ubicacion;

    private String descripcion;

    private String urlPortada;

    @NotNull(message = "El número de copias totales es obligatorio")
    @Positive(message = "Debe haber al menos una copia")
    private Integer copiesTotal;
}