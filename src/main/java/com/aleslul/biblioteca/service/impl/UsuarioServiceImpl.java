package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.dto.UsuarioDTO;
import com.aleslul.biblioteca.dto.UsuarioRegistroDTO;
import com.aleslul.biblioteca.exception.RecursoDuplicadoException;
import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.model.Rol;
import com.aleslul.biblioteca.model.Usuario;
import com.aleslul.biblioteca.repository.RolRepository;
import com.aleslul.biblioteca.repository.UsuarioRepository;
import com.aleslul.biblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UsuarioDTO registrarUsuario(UsuarioRegistroDTO registroDTO) {
        // DS01 - RF-01: rechazar correos ya registrados (409 Conflict)
        if (usuarioRepository.findByCorreo(registroDTO.getCorreo()).isPresent()) {
            throw new RecursoDuplicadoException("Ya existe un usuario registrado con el correo: " + registroDTO.getCorreo());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setCorreo(registroDTO.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(registroDTO.getContrasena())); // Contraseña hasheada con BCrypt

        Rol rol = rolRepository.findById(registroDTO.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("El rol especificado con ID " + registroDTO.getIdRol() + " no existe")); // Reemplazado
        usuario.setRol(rol);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return convertToDTO(usuarioGuardado);
    }

    @Override
    public UsuarioDTO obtenerPorId(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id)); // Reemplazado
        return convertToDTO(usuario);
    }

    @Override
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarUsuario(int id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No se puede eliminar un usuario inexistente con ID: " + id); // Reemplazado
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public UsuarioDTO actualizarRol(int idUsuario, int idRol) {
        // DS02 - RF-02: verifica que el usuario exista, si no 404
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + idUsuario));

        // DS02 - RF-02: verifica que el rol exista, si no 404
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoException("El rol especificado con ID " + idRol + " no existe"));

        usuario.setRol(rol);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return convertToDTO(usuarioActualizado);
    }

    private UsuarioDTO convertToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setNombreRol(usuario.getRol().getNombre().name());
        return dto;
    }
}