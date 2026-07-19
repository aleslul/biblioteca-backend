package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.model.DetallePrestamo;
import java.util.List;

public interface DetallePrestamoService {
    List<DetallePrestamo> obtenerDetallesPorPrestamo(int idPrestamo);
    List<DetallePrestamo> obtenerPendientesPorPrestamo(int idPrestamo);
}