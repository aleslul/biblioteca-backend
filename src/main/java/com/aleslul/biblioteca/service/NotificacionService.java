package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.dto.response.NotificacionResponseDTO;
import com.aleslul.biblioteca.model.enums.TipoNotificacion;

import java.util.List;

public interface NotificacionService {
    List<NotificacionResponseDTO> obtenerPorUsuario(int idUsuario);

    // Usado por el scheduler (RF10): crea la notificación solo si no existe ya una igual
    void crearSiNoExiste(int idUsuario, TipoNotificacion tipo, String mensaje, int idReferencia);
}