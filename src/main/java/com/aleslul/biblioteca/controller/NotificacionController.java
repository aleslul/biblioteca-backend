package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.response.NotificacionResponseDTO;
import com.aleslul.biblioteca.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    // RF10: GET /api/notificaciones?idUsuario={id}
    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerNotificaciones(@RequestParam int idUsuario) {
        return ResponseEntity.ok(notificacionService.obtenerPorUsuario(idUsuario));
    }
}