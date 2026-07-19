package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.response.MultaResponseDTO;
import com.aleslul.biblioteca.model.Multa;
import com.aleslul.biblioteca.service.MultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/multas")
public class MultaController {

    @Autowired
    private MultaService multaService;

    @PutMapping("/{id}/pagar")
    public ResponseEntity<MultaResponseDTO> pagarMulta(@PathVariable int id) {
        Multa multa = multaService.pagarMulta(id);
        return ResponseEntity.ok(convertToDTO(multa));
    }

    @PutMapping("/{id}/condonar")
    public ResponseEntity<MultaResponseDTO> condonarMulta(@PathVariable int id) {
        Multa multa = multaService.condonarMulta(id);
        return ResponseEntity.ok(convertToDTO(multa));
    }

    private MultaResponseDTO convertToDTO(Multa multa) {
        MultaResponseDTO dto = new MultaResponseDTO();
        dto.setId(multa.getId());
        dto.setIdDevolucion(multa.getDevolucion().getId());
        dto.setMontoTotal(multa.getMontoTotal());
        dto.setPrecioLibro(multa.getPrecioLibro());
        dto.setEstado(multa.getEstado().name());
        dto.setFechaCalculo(multa.getFechaCalculo());
        return dto;
    }
}