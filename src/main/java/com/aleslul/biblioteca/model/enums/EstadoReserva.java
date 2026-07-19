package com.aleslul.biblioteca.model.enums;

// Equivalencia con la maqueta (RF pendiente, sin renombrar el backend):
// ACTIVA (backend)   <-> 'PENDIENTE' (maqueta)
// ATENDIDA (backend) <-> 'COMPLETADA' (maqueta)
// CANCELADA (backend) <-> 'CANCELADA' (maqueta, coincide)
// Si se expone directamente al frontend maqueta, mapear en el DTO de salida.
public enum EstadoReserva {
    ACTIVA,
    ATENDIDA,
    CANCELADA
}