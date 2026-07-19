package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.model.Rol;
import com.aleslul.biblioteca.model.enums.NombreRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    // Por si necesitas buscar un rol directamente por su enum
    Optional<Rol> findByNombre(NombreRol nombre);
}