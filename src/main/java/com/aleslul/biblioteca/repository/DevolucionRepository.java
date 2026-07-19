package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.model.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Integer> {

    // RF9: total de devoluciones en un período
    @Query("SELECT COUNT(d) FROM Devolucion d WHERE " +
            "(:desde IS NULL OR d.fechaDevolucion >= :desde) AND " +
            "(:hasta IS NULL OR d.fechaDevolucion <= :hasta)")
    long contarDevoluciones(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    // RF9: devoluciones que generaron multa en un período
    @Query("SELECT COUNT(d) FROM Devolucion d WHERE d.conMulta = true AND " +
            "(:desde IS NULL OR d.fechaDevolucion >= :desde) AND " +
            "(:hasta IS NULL OR d.fechaDevolucion <= :hasta)")
    long contarDevolucionesConMulta(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
