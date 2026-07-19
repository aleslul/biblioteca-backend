package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.dto.response.NotificacionResponseDTO;
import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.model.Notificacion;
import com.aleslul.biblioteca.model.Usuario;
import com.aleslul.biblioteca.model.enums.TipoNotificacion;
import com.aleslul.biblioteca.repository.NotificacionRepository;
import com.aleslul.biblioteca.repository.UsuarioRepository;
import com.aleslul.biblioteca.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<NotificacionResponseDTO> obtenerPorUsuario(int idUsuario) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(idUsuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void crearSiNoExiste(int idUsuario, TipoNotificacion tipo, String mensaje, int idReferencia) {
        if (notificacionRepository.existsByUsuarioIdAndTipoAndIdReferencia(idUsuario, tipo, idReferencia)) {
            return; // Ya se notificó antes; no duplicar (RF10).
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + idUsuario));

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(tipo);
        notificacion.setMensaje(mensaje);
        notificacion.setIdReferencia(idReferencia);
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setLeida(false);

        notificacionRepository.save(notificacion);
    }

    private NotificacionResponseDTO convertToDTO(Notificacion n) {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setId(n.getId());
        dto.setTipo(n.getTipo().name());
        dto.setMensaje(n.getMensaje());
        dto.setFechaCreacion(n.getFechaCreacion());
        dto.setLeida(n.isLeida());
        return dto;
    }
}