package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.request.DevolucionRequestDTO;
import com.aleslul.biblioteca.dto.response.DevolucionResponseDTO;
import com.aleslul.biblioteca.model.Devolucion;
import com.aleslul.biblioteca.service.DevolucionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionController {

    @Autowired
    private DevolucionService devolucionService;

    @PostMapping
    public ResponseEntity<DevolucionResponseDTO> registrarDevolucion(@Valid @RequestBody DevolucionRequestDTO requestDTO) {
        Devolucion devolucion = devolucionService.registrarDevolucion(requestDTO.getIdPrestamo());
        return new ResponseEntity<>(convertToDTO(devolucion), HttpStatus.CREATED);
    }

    private DevolucionResponseDTO convertToDTO(Devolucion dev) {
        DevolucionResponseDTO dto = new DevolucionResponseDTO();
        dto.setId(dev.getId());
        dto.setIdPrestamo(dev.getPrestamo().getId());
        dto.setFechaDevolucion(dev.getFechaDevolucion());
        dto.setDiasRetraso(dev.getDiasRetraso());
        dto.setConMulta(dev.isConMulta());
        return dto;
    }
}