package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.dto.request.PrestamoRequestDTO;
import com.aleslul.biblioteca.dto.response.PrestamoResponseDTO;
import java.util.List;

public interface PrestamoService {
    PrestamoResponseDTO registrarPrestamo(PrestamoRequestDTO requestDTO);
    PrestamoResponseDTO obtenerPorId(int id);
    List<PrestamoResponseDTO> obtenerTodos();
    List<PrestamoResponseDTO> obtenerPorUsuario(int idUsuario);
    // DS07 - RF-07: Renovación autónoma de préstamos
    PrestamoResponseDTO renovarPrestamo(int idPrestamo);
}