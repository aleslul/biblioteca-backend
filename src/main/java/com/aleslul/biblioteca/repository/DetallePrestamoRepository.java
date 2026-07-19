// src/main/java/com/aleslul/biblioteca/repository/DetallePrestamoRepository.java

package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.dto.response.LibroMasSolicitadoResponseDTO;
import com.aleslul.biblioteca.model.DetallePrestamo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetallePrestamoRepository extends JpaRepository<DetallePrestamo, Integer> {

    List<DetallePrestamo> findByPrestamoId(int idPrestamo);

    List<DetallePrestamo> findByPrestamoIdAndDevueltoFalse(int idPrestamo);

    boolean existsByLibro_IdAndDevueltoFalse(int idLibro);

    // NUEVO: Valida si el usuario ya cuenta con un ejemplar activo sin devolver
    boolean existsByLibro_IdAndPrestamo_Usuario_IdAndDevueltoFalse(int idLibro, int idUsuario);

    @Query("SELECT new com.aleslul.biblioteca.dto.response.LibroMasSolicitadoResponseDTO(" +
            "d.libro.id, d.libro.titulo, d.libro.autor, COUNT(d)) " +
            "FROM DetallePrestamo d WHERE " +
            "(:desde IS NULL OR d.prestamo.fechaPrestamo >= :desde) AND " +
            "(:hasta IS NULL OR d.prestamo.fechaPrestamo <= :hasta) " +
            "GROUP BY d.libro.id, d.libro.titulo, d.libro.autor " +
            "ORDER BY COUNT(d) DESC")
    List<LibroMasSolicitadoResponseDTO> obtenerLibrosMasSolicitados(
            @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, Pageable pageable);
}