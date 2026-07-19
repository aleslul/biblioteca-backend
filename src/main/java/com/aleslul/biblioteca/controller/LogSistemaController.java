package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.response.LogSistemaResponseDTO;
import com.aleslul.biblioteca.model.LogSistema;
import com.aleslul.biblioteca.service.LogSistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class LogSistemaController {

    @Autowired
    private LogSistemaService logSistemaService;

    @GetMapping
    public ResponseEntity<List<LogSistemaResponseDTO>> obtenerTodosLosLogs() {
        List<LogSistemaResponseDTO> logsDto = logSistemaService.obtenerTodosLosLogs().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logsDto);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<LogSistemaResponseDTO>> obtenerLogsPorUsuario(@PathVariable int idUsuario) {
        List<LogSistemaResponseDTO> logsDto = logSistemaService.obtenerLogsPorUsuario(idUsuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logsDto);
    }

    private LogSistemaResponseDTO convertToDTO(LogSistema log) {
        LogSistemaResponseDTO dto = new LogSistemaResponseDTO();
        dto.setId(log.getId());
        dto.setIdUsuario(log.getUsuario().getId());
        dto.setNombreUsuario(log.getUsuario().getNombre());
        dto.setTipoAccion(log.getTipoAccion().name());
        dto.setSeveridad(log.getSeveridad() != null ? log.getSeveridad().name() : null);
        dto.setDescripccion(log.getDescripccion()); // Mapeado exactamente al atributo de tu modelo
        dto.setFechaRegistro(log.getFechaRegistro());
        return dto;
    }
}