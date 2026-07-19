package com.aleslul.biblioteca.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

// RF pendiente: la maqueta edita nombre, correo, teléfono, DNI, status y rol
// todos juntos en un solo formulario/submit (PUT /api/usuarios/{id})
@Data
public class UsuarioActualizarRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    private String telefono;

    @Positive(message = "Debe especificar un rol válido")
    private int idRol;

    @NotNull(message = "El estado es obligatorio")
    private String estado; // "ACTIVO" | "INACTIVO"
}
