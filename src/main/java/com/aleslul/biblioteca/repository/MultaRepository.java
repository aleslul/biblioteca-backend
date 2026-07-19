package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.model.Multa;
import com.aleslul.biblioteca.model.enums.EstadoMulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultaRepository extends JpaRepository<Multa, Integer> {

    // RF pendiente: verifica si un usuario tiene multas en un estado dado,
    // navegando Multa -> Devolucion -> Prestamo -> Usuario
    boolean existsByDevolucion_Prestamo_Usuario_IdAndEstado(int idUsuario, EstadoMulta estado);
}