package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Para buscar un usuario por su correo durante el login
    Optional<Usuario> findByCorreo(String correo);
}