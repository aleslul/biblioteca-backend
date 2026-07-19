package com.aleslul.biblioteca.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String tipo; // "Bearer"
    private int idUsuario;
    private String nombre;
    private String correo;
    private String rol;
}