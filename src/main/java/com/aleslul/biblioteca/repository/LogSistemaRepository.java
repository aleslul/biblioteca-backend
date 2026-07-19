package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.model.LogSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogSistemaRepository extends JpaRepository<LogSistema, Integer> {

    // Para auditar las acciones realizadas por un usuario específico
    List<LogSistema> findByUsuarioId(int idUsuario);

    // Para listar los logs del sistema ordenados del más reciente al más antiguo
    List<LogSistema> findAllByOrderByFechaRegistroDesc();
}