package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.model.DetallePrestamo;
import com.aleslul.biblioteca.repository.DetallePrestamoRepository;
import com.aleslul.biblioteca.service.DetallePrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DetallePrestamoServiceImpl implements DetallePrestamoService {

    @Autowired
    private DetallePrestamoRepository detallePrestamoRepository;

    @Override
    public List<DetallePrestamo> obtenerDetallesPorPrestamo(int idPrestamo) {
        return detallePrestamoRepository.findByPrestamoId(idPrestamo);
    }

    @Override
    public List<DetallePrestamo> obtenerPendientesPorPrestamo(int idPrestamo) {
        return detallePrestamoRepository.findByPrestamoIdAndDevueltoFalse(idPrestamo);
    }
}