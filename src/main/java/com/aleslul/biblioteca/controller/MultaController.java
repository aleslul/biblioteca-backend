package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.response.MultaResponseDTO;
import com.aleslul.biblioteca.model.Devolucion;
import com.aleslul.biblioteca.model.Multa;
import com.aleslul.biblioteca.model.Prestamo;
import com.aleslul.biblioteca.repository.DetallePrestamoRepository;
import com.aleslul.biblioteca.service.MultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/multas")
public class MultaController {

    @Autowired
    private MultaService multaService;

    @Autowired
    private DetallePrestamoRepository detallePrestamoRepository;

    // RF pendiente: el frontend necesita listar todas las multas (antes no existía este endpoint)
    @GetMapping
    public ResponseEntity<List<MultaResponseDTO>> obtenerTodasLasMultas() {
        List<MultaResponseDTO> response = multaService.obtenerTodas().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // RF pendiente: para que un estudiante consulte solo sus propias multas
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<MultaResponseDTO>> obtenerMultasPorUsuario(@PathVariable int idUsuario) {
        List<MultaResponseDTO> response = multaService.obtenerPorUsuario(idUsuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<MultaResponseDTO> pagarMulta(@PathVariable int id) {
        Multa multa = multaService.pagarMulta(id);
        return ResponseEntity.ok(convertToDTO(multa));
    }

    @PutMapping("/{id}/condonar")
    public ResponseEntity<MultaResponseDTO> condonarMulta(@PathVariable int id) {
        Multa multa = multaService.condonarMulta(id);
        return ResponseEntity.ok(convertToDTO(multa));
    }

    private MultaResponseDTO convertToDTO(Multa multa) {
        MultaResponseDTO dto = new MultaResponseDTO();
        dto.setId(multa.getId());
        dto.setIdDevolucion(multa.getDevolucion().getId());
        dto.setMontoTotal(multa.getMontoTotal());
        dto.setPrecioLibro(multa.getPrecioLibro());
        dto.setEstado(multa.getEstado().name());
        dto.setFechaCalculo(multa.getFechaCalculo());

        // Campos enriquecidos: Multa -> Devolucion -> Prestamo -> Usuario / libros prestados
        Devolucion devolucion = multa.getDevolucion();
        Prestamo prestamo = devolucion.getPrestamo();
        dto.setIdPrestamo(prestamo.getId());
        dto.setIdUsuario(prestamo.getUsuario().getId());
        dto.setMemberName(prestamo.getUsuario().getNombre());
        dto.setDaysOverdue(devolucion.getDiasRetraso());

        String titulos = detallePrestamoRepository.findByPrestamoId(prestamo.getId()).stream()
                .map(d -> d.getLibro().getTitulo())
                .collect(Collectors.joining(", "));
        dto.setBookTitle(titulos);

        return dto;
    }
}