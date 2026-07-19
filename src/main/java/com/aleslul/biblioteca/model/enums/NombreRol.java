package com.aleslul.biblioteca.model.enums;

// Equivalencia con la maqueta (RF pendiente, sin renombrar el backend):
// USUARIO_FINAL (backend) <-> 'ESTUDIANTE' (maqueta)
// Si se expone directamente al frontend maqueta, mapear en el DTO de salida.
public enum NombreRol {
    ADMINISTRADOR,
    BIBLIOTECARIO,
    USUARIO_FINAL
}