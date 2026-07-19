package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.request.LoginRequestDTO;
import com.aleslul.biblioteca.dto.response.LoginResponseDTO;
import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.model.Usuario;
import com.aleslul.biblioteca.repository.UsuarioRepository;
import com.aleslul.biblioteca.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    // DS03 - RF-03: Autenticacion segura
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        // Si las credenciales son invalidas, esto lanza BadCredentialsException,
        // que el GlobalExceptionHandler convierte en 401.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getCorreo(), requestDTO.getContrasena())
        );

        Usuario usuario = usuarioRepository.findByCorreo(requestDTO.getCorreo())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con correo: " + requestDTO.getCorreo()));

        String token = jwtService.generarToken(usuario);

        LoginResponseDTO response = new LoginResponseDTO(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombre().name()
        );

        return ResponseEntity.ok(response);
    }
}