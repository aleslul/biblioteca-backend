package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.model.LogSistema;
import com.aleslul.biblioteca.model.enums.TipoAccion;
import java.util.List;

public interface LogSistemaService {
    // Método principal para registrar cualquier evento en el sistema
    void registrarAccion(int idUsuario, TipoAccion tipoAccion, String descripcion);

    List<LogSistema> obtenerTodosLosLogs();
    List<LogSistema> obtenerLogsPorUsuario(int idUsuario);
}