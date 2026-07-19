package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.dto.UsuarioDTO;
import com.aleslul.biblioteca.dto.UsuarioRegistroDTO;
import java.util.List;

public interface UsuarioService {
    UsuarioDTO registrarUsuario(UsuarioRegistroDTO registroDTO);
    UsuarioDTO obtenerPorId(int id);
    List<UsuarioDTO> obtenerTodos();
    void eliminarUsuario(int id);
    // DS02 - RF-02: Gestión dinámica de roles
    UsuarioDTO actualizarRol(int idUsuario, int idRol);
}