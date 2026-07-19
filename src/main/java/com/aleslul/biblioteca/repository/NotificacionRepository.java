package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.model.Notificacion;
import com.aleslul.biblioteca.model.enums.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(int idUsuario);

    // Evita que el scheduler duplique la misma notificación en corridas sucesivas
    boolean existsByUsuarioIdAndTipoAndIdReferencia(int idUsuario, TipoNotificacion tipo, int idReferencia);
}