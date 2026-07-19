package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.UsuarioDTO;
import com.aleslul.biblioteca.dto.UsuarioRegistroDTO;
import com.aleslul.biblioteca.dto.request.ActualizarRolRequestDTO;
import com.aleslul.biblioteca.dto.request.UsuarioActualizarRequestDTO;
import com.aleslul.biblioteca.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDTO> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO registroDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.registrarUsuario(registroDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    // DS02 - RF-02: Gestión dinámica de roles
    @PutMapping("/{id}/rol")
    public ResponseEntity<UsuarioDTO> actualizarRol(@PathVariable int id,
                                                    @Valid @RequestBody ActualizarRolRequestDTO requestDTO) {
        UsuarioDTO usuarioActualizado = usuarioService.actualizarRol(id, requestDTO.getIdRol());
        return ResponseEntity.ok(usuarioActualizado);
    }

    // RF pendiente: edición general (nombre, correo, teléfono, DNI, status, rol) en un solo submit
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(@PathVariable int id,
                                                        @Valid @RequestBody UsuarioActualizarRequestDTO requestDTO) {
        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, requestDTO);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable int id) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodosLosUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable int id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}