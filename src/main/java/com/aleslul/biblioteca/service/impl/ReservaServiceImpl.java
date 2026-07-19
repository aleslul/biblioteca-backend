package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.dto.request.ReservaRequestDTO;
import com.aleslul.biblioteca.dto.response.ReservaResponseDTO;
import com.aleslul.biblioteca.exception.RecursoDuplicadoException;
import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.exception.ReglaNegocioException;
import com.aleslul.biblioteca.model.Libro;
import com.aleslul.biblioteca.model.Reserva;
import com.aleslul.biblioteca.model.Usuario;
import com.aleslul.biblioteca.model.enums.EstadoMulta;
import com.aleslul.biblioteca.model.enums.EstadoReserva;
import com.aleslul.biblioteca.repository.LibroRepository;
import com.aleslul.biblioteca.repository.MultaRepository;
import com.aleslul.biblioteca.repository.ReservaRepository;
import com.aleslul.biblioteca.repository.UsuarioRepository;
import com.aleslul.biblioteca.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MultaRepository multaRepository;

    @Override
    public ReservaResponseDTO registrarReserva(ReservaRequestDTO requestDTO) {
        Libro libro = libroRepository.findById(requestDTO.getIdLibro())
                .orElseThrow(() -> new RecursoNoEncontradoException("El libro con ID " + requestDTO.getIdLibro() + " no existe"));

        Usuario usuario = usuarioRepository.findById(requestDTO.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + requestDTO.getIdUsuario()));

        // Bloquear si el usuario tiene multas pendientes de pago
        if (multaRepository.existsByDevolucion_Prestamo_Usuario_IdAndEstado(usuario.getId(), EstadoMulta.PENDIENTE)) {
            throw new ReglaNegocioException("El usuario tiene multas pendientes de pago; no puede registrar nuevos préstamos/reservas/renovaciones");
        }

        if (libro.isDisponible()) {
            throw new ReglaNegocioException("El libro '" + libro.getTitulo() + "' está disponible; no es necesario reservarlo");
        }

        if (reservaRepository.existsByLibroIdAndUsuarioIdAndEstado(libro.getId(), usuario.getId(), EstadoReserva.ACTIVA)) {
            throw new RecursoDuplicadoException("Ya tienes una reserva activa para este libro");
        }

        Reserva reserva = new Reserva();
        reserva.setLibro(libro);
        reserva.setUsuario(usuario);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setEstado(EstadoReserva.ACTIVA);

        return convertToDTO(reservaRepository.save(reserva));
    }

    @Override
    public void cancelarReserva(int id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con ID: " + id));
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Override
    public List<ReservaResponseDTO> obtenerPorUsuario(int idUsuario) {
        return reservaRepository.findByUsuarioId(idUsuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservaResponseDTO> obtenerTodas() {
        return reservaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ReservaResponseDTO convertToDTO(Reserva reserva) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        dto.setIdLibro(reserva.getLibro().getId());
        dto.setTituloLibro(reserva.getLibro().getTitulo());
        dto.setIdUsuario(reserva.getUsuario().getId());
        dto.setNombreUsuario(reserva.getUsuario().getNombre());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setEstado(reserva.getEstado().name());
        return dto;
    }
}