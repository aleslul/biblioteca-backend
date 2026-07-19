package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.request.PrestamoRequestDTO;
import com.aleslul.biblioteca.dto.response.PrestamoResponseDTO;
import com.aleslul.biblioteca.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoControlller {

    @Autowired
    private PrestamoService prestamoService;

    @PostMapping
    public ResponseEntity<PrestamoResponseDTO> registrarPrestamo(@Valid @RequestBody PrestamoRequestDTO requestDTO) {
        PrestamoResponseDTO response = prestamoService.registrarPrestamo(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoResponseDTO> obtenerPrestamoPorId(@PathVariable int id) {
        PrestamoResponseDTO response = prestamoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerTodosLosPrestamos() {
        List<PrestamoResponseDTO> response = prestamoService.obtenerTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PrestamoResponseDTO>> obtenerPrestamosPorUsuario(@PathVariable int idUsuario) {
        List<PrestamoResponseDTO> response = prestamoService.obtenerPorUsuario(idUsuario);
        return ResponseEntity.ok(response);
    }

    // DS07 - RF-07: Renovación autónoma de préstamos
    @PutMapping("/{id}/renovar")
    public ResponseEntity<PrestamoResponseDTO> renovarPrestamo(@PathVariable int id) {
        PrestamoResponseDTO response = prestamoService.renovarPrestamo(id);
        return ResponseEntity.ok(response);
    }
}