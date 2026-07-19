package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.response.DetallePrestamoResponseDTO;
import com.aleslul.biblioteca.model.DetallePrestamo;
import com.aleslul.biblioteca.service.DetallePrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//TODO: Esta clase podría no ser necesaria

@RestController
@RequestMapping("/api/prestamos/{idPrestamo}/detalles")
public class DetallePrestamoController {

    @Autowired
    private DetallePrestamoService detallePrestamoService;

    // GET /api/prestamos/{idPrestamo}/detalles
    // Obtiene todos los libros asociados a un préstamo específico
    @GetMapping
    public ResponseEntity<List<DetallePrestamoResponseDTO>> obtenerDetallesPorPrestamo(@PathVariable int idPrestamo) {
        List<DetallePrestamoResponseDTO> response = detallePrestamoService.obtenerDetallesPorPrestamo(idPrestamo)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /api/prestamos/{idPrestamo}/detalles/pendientes
    // Obtiene únicamente los libros que el usuario aún no ha devuelto de ese préstamo
    @GetMapping("/pendientes")
    public ResponseEntity<List<DetallePrestamoResponseDTO>> obtenerPendientesPorPrestamo(@PathVariable int idPrestamo) {
        List<DetallePrestamoResponseDTO> response = detallePrestamoService.obtenerPendientesPorPrestamo(idPrestamo)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Método helper para mapear la Entidad al Response DTO
    private DetallePrestamoResponseDTO convertToDTO(DetallePrestamo detalle) {
        DetallePrestamoResponseDTO dto = new DetallePrestamoResponseDTO();
        dto.setIdDetalle(detalle.getId());
        dto.setIdLibro(detalle.getLibro().getId());
        dto.setTituloLibro(detalle.getLibro().getTitulo());
        dto.setDevuelto(detalle.getDevuelto());
        return dto;
    }
}