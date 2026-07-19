package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.RolDTO;
import com.aleslul.biblioteca.model.Rol;
import com.aleslul.biblioteca.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<List<RolDTO>> listarRoles() {
        List<RolDTO> rolesDto = rolService.obtenerTodos().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rolesDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtenerRolPorId(@PathVariable int id) {
        Rol rol = rolService.obtenerPorId(id);
        return ResponseEntity.ok(convertToDTO(rol));
    }

    private RolDTO convertToDTO(Rol rol) {
        RolDTO dto = new RolDTO();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre().name());
        return dto;
    }
}