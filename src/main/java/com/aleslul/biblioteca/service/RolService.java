package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.model.Rol;
import java.util.List;

public interface RolService {
    List<Rol> obtenerTodos();
    Rol obtenerPorId(int id);
}