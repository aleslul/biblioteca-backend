package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.model.LogSistema;
import com.aleslul.biblioteca.model.Usuario;
import com.aleslul.biblioteca.model.enums.TipoAccion;
import com.aleslul.biblioteca.repository.LogSistemaRepository;
import com.aleslul.biblioteca.repository.UsuarioRepository;
import com.aleslul.biblioteca.service.LogSistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogSistemaServiceImpl implements LogSistemaService {

    @Autowired
    private LogSistemaRepository logSistemaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void registrarAccion(int idUsuario, TipoAccion tipoAccion, String descripcion) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado para registrar auditoría con ID: " + idUsuario)); // Reemplazado[cite: 2]

        LogSistema log = new LogSistema();
        log.setUsuario(usuario);
        log.setTipoAccion(tipoAccion);
        log.setDescripccion(descripcion); // Mapeado exactamente al atributo 'descripccion' de tu entidad[cite: 2]
        log.setFechaRegistro(LocalDateTime.now());

        logSistemaRepository.save(log);
    }

    @Override
    public List<LogSistema> obtenerTodosLosLogs() {
        return logSistemaRepository.findAllByOrderByFechaRegistroDesc();
    }

    @Override
    public List<LogSistema> obtenerLogsPorUsuario(int idUsuario) {
        return logSistemaRepository.findByUsuarioId(idUsuario);
    }
}