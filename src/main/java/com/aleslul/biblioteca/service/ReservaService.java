package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.dto.request.ReservaRequestDTO;
import com.aleslul.biblioteca.dto.response.ReservaResponseDTO;

import java.util.List;

public interface ReservaService {
    ReservaResponseDTO registrarReserva(ReservaRequestDTO requestDTO);
    void cancelarReserva(int id);
    List<ReservaResponseDTO> obtenerPorUsuario(int idUsuario);
    List<ReservaResponseDTO> obtenerTodas();
}