package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.model.Reserva;
import com.aleslul.biblioteca.model.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // Para RF7: saber si un libro tiene reservas activas (de cualquier usuario)
    List<Reserva> findByLibroIdAndEstado(int idLibro, EstadoReserva estado);

    // Para RF10: atender primero a quien reservó antes (orden de llegada)
    List<Reserva> findByLibroIdAndEstadoOrderByFechaReservaAsc(int idLibro, EstadoReserva estado);

    // Evitar que el mismo usuario reserve el mismo libro dos veces mientras ya tiene una reserva activa
    boolean existsByLibroIdAndUsuarioIdAndEstado(int idLibro, int idUsuario, EstadoReserva estado);

    List<Reserva> findByUsuarioId(int idUsuario);
}