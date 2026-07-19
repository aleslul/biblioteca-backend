package com.aleslul.biblioteca.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioDTO {
    private int id;
    private String nombre;
    private String correo;
    private String nombreRol;
    private String telefono;
    private String dni;
    private LocalDate fechaMembresia;
    private String estado;
}