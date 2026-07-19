package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.request.ReservaRequestDTO;
import com.aleslul.biblioteca.dto.response.ReservaResponseDTO;
import com.aleslul.biblioteca.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> registrarReserva(@Valid @RequestBody ReservaRequestDTO requestDTO) {
        ReservaResponseDTO response = reservaService.registrarReserva(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable int id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerReservasPorUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(reservaService.obtenerPorUsuario(idUsuario));
    }
}