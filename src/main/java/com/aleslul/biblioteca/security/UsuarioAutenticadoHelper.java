package com.aleslul.biblioteca.security;

import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.model.Usuario;
import com.aleslul.biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Helper reusable: obtiene el Usuario autenticado actual a partir del correo
// que Spring Security guarda como "username" del JWT (ver CustomUserDetailsService).
// Pensado para cualquier service que necesite saber "quién" hizo la petición,
// no solo "sobre quién" es la acción (por ejemplo, para auditoría en LogSistema).

@Component
public class UsuarioAutenticadoHelper {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // No debería pasar en un endpoint protegido por SecurityConfig, pero se valida por seguridad.
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay un usuario autenticado en el contexto de seguridad actual");
        }

        String correo = authentication.getName();
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario autenticado no encontrado con correo: " + correo));
    }

    public int obtenerIdUsuarioActual() {
        return obtenerUsuarioActual().getId();
    }
}