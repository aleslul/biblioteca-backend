// src/main/java/com/aleslul/biblioteca/repository/ReservaRepository.java

package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.model.Reserva;
import com.aleslul.biblioteca.model.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findByLibroIdAndEstado(int idLibro, EstadoReserva estado);

    List<Reserva> findByLibroIdAndEstadoOrderByFechaReservaAsc(int idLibro, EstadoReserva estado);

    // CORREGIDO: Se agregaron guiones bajos para resolver la ambigüedad de travesía en JPA
    boolean existsByLibro_IdAndUsuario_IdAndEstado(int idLibro, int idUsuario, EstadoReserva estado);

    List<Reserva> findByUsuarioId(int idUsuario);
}