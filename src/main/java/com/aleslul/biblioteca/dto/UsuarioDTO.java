package com.aleslul.biblioteca.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private int id;
    private String nombre;
    private String correo;
    private String nombreRol;
}