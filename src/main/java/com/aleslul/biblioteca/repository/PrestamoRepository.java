package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.model.Prestamo;
import com.aleslul.biblioteca.model.enums.EstadoPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

    // Método útil para buscar todos los préstamos de un usuario específico
    List<Prestamo> findByUsuarioId(int idUsuario);

    // Método útil para buscar préstamos por su estado (por ejemplo, VENCIDO o ACTIVO)
    List<Prestamo> findByEstado(EstadoPrestamo estado);

    // RF10: préstamos vigentes cuyo vencimiento cae dentro de una ventana (para notificar)
    List<Prestamo> findByEstadoInAndFechaVencimientoBetween(
            List<EstadoPrestamo> estados, LocalDateTime desde, LocalDateTime hasta);

    // RF9: total de préstamos en un período (o histórico completo si desde/hasta son null)
    @Query("SELECT COUNT(p) FROM Prestamo p WHERE " +
            "(:desde IS NULL OR p.fechaPrestamo >= :desde) AND " +
            "(:hasta IS NULL OR p.fechaPrestamo <= :hasta)")
    long contarPrestamos(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    // RF9: total de préstamos por estado en un período
    @Query("SELECT COUNT(p) FROM Prestamo p WHERE p.estado = :estado AND " +
            "(:desde IS NULL OR p.fechaPrestamo >= :desde) AND " +
            "(:hasta IS NULL OR p.fechaPrestamo <= :hasta)")
    long contarPrestamosPorEstado(@Param("estado") EstadoPrestamo estado,
                                  @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
