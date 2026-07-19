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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // @Transactional es lo que soluciona el LazyInitializationException: abre su propia
    // sesión de Hibernate para este método, sin depender de si el filtro OpenEntityManagerInView
    // ya corrió o no. El filtro JWT (parte de la cadena de Spring Security) se ejecuta ANTES
    // de que se abra la sesión "de la vista", así que no podíamos confiar en eso.
    @Override
    @Transactional(readOnly = true)
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