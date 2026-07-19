package com.aleslul.biblioteca.controller;

import com.aleslul.biblioteca.dto.request.LibroRequestDTO;
import com.aleslul.biblioteca.dto.response.LibroResponseDTO;
import com.aleslul.biblioteca.model.Libro;
import com.aleslul.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @PostMapping
    public ResponseEntity<LibroResponseDTO> registrarLibro(@Valid @RequestBody LibroRequestDTO requestDTO) {
        Libro libro = new Libro();
        libro.setTitulo(requestDTO.getTitulo());
        libro.setAutor(requestDTO.getAutor());
        libro.setIsbn(requestDTO.getIsbn());
        libro.setCategoria(requestDTO.getCategoria());
        libro.setPrecio(requestDTO.getPrecio());
        libro.setAnioPublicacion(requestDTO.getAnioPublicacion());
        libro.setUbicacion(requestDTO.getUbicacion());
        libro.setDescripcion(requestDTO.getDescripcion());
        libro.setUrlPortada(requestDTO.getUrlPortada());
        libro.setCopiesTotal(requestDTO.getCopiesTotal());
        libro.setCopiesAvailable(requestDTO.getCopiesTotal());

        Libro libroGuardado = libroService.guardarLibro(libro);
        return new ResponseEntity<>(convertToDTO(libroGuardado), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroResponseDTO> obtenerLibroPorId(@PathVariable int id) {
        Libro libro = libroService.obtenerPorId(id);
        return ResponseEntity.ok(convertToDTO(libro));
    }

    @GetMapping
    public ResponseEntity<List<LibroResponseDTO>> listarLibros(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String categoria) {

        List<Libro> libros;
        if (titulo == null && autor == null && isbn == null && categoria == null) {
            libros = libroService.listarTodos();
        } else {
            libros = libroService.buscarLibros(titulo, autor, isbn, categoria);
        }

        List<LibroResponseDTO> response = libros.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibroResponseDTO> actualizarLibro(@PathVariable int id, @Valid @RequestBody LibroRequestDTO requestDTO) {
        Libro datosActualizados = new Libro();
        datosActualizados.setTitulo(requestDTO.getTitulo());
        datosActualizados.setAutor(requestDTO.getAutor());
        datosActualizados.setIsbn(requestDTO.getIsbn());
        datosActualizados.setCategoria(requestDTO.getCategoria());
        datosActualizados.setPrecio(requestDTO.getPrecio());
        datosActualizados.setAnioPublicacion(requestDTO.getAnioPublicacion());
        datosActualizados.setUbicacion(requestDTO.getUbicacion());
        datosActualizados.setDescripcion(requestDTO.getDescripcion());
        datosActualizados.setUrlPortada(requestDTO.getUrlPortada());
        datosActualizados.setCopiesTotal(requestDTO.getCopiesTotal());

        Libro libroActualizado = libroService.actualizarLibro(id, datosActualizados);
        return ResponseEntity.ok(convertToDTO(libroActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable int id) {
        libroService.eliminarLibro(id);
        return ResponseEntity.noContent().build();
    }

    private LibroResponseDTO convertToDTO(Libro libro) {
        LibroResponseDTO dto = new LibroResponseDTO();
        dto.setId(libro.getId());
        dto.setTitulo(libro.getTitulo());
        dto.setAutor(libro.getAutor());
        dto.setIsbn(libro.getIsbn());
        dto.setCategoria(libro.getCategoria());
        dto.setPrecio(libro.getPrecio());
        dto.setAnioPublicacion(libro.getAnioPublicacion());
        dto.setUbicacion(libro.getUbicacion());
        dto.setDescripcion(libro.getDescripcion());
        dto.setUrlPortada(libro.getUrlPortada());
        dto.setCopiesTotal(libro.getCopiesTotal());
        dto.setCopiesAvailable(libro.getCopiesAvailable());
        dto.setDisponible(libro.isDisponible());
        return dto;
    }
}