// src/main/java/com/aleslul/biblioteca/controller/ReservaController.java

package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.request.ReservaRequestDTO;
import com.aleslul.biblioteca.dto.response.ReservaResponseDTO;
import com.aleslul.biblioteca.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> registrarReserva(@RequestBody ReservaRequestDTO requestDTO) {
        return new ResponseEntity<>(reservaService.registrarReserva(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(reservaService.obtenerPorUsuario(idUsuario));
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(reservaService.obtenerTodas());
    }

    // MANDATORIO: Asegurar que exista este mapeo exacto para soportar el DELETE del frontend
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable int id) {
        reservaService.cancelarReserva(id);
        // Devolvemos un JSON estructurado para evitar que el JSON parser del frontend falle por cuerpo vacío
        return ResponseEntity.ok(Map.of("mensaje", "Reserva cancelada exitosamente"));
    }
}