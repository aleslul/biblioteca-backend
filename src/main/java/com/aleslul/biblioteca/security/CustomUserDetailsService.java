package com.aleslul.biblioteca.security;

import com.aleslul.biblioteca.model.Usuario;
import com.aleslul.biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No existe un usuario registrado con el correo: " + correo));

        // El rol se expone como authority con el prefijo ROLE_ (convencion de Spring Security),
        // por ejemplo NombreRol.ADMINISTRADOR -> "ROLE_ADMINISTRADOR"
        String authority = "ROLE_" + usuario.getRol().getNombre().name();

        // RF pendiente: un usuario con estado INACTIVO no puede autenticarse
        boolean habilitado = usuario.getEstado() == com.aleslul.biblioteca.model.enums.EstadoUsuario.ACTIVO;

        return new User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                habilitado,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(authority))
        );
    }
}