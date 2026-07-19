package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.model.Libro;
import com.aleslul.biblioteca.repository.LibroRepository;
import com.aleslul.biblioteca.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public Libro guardarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public Libro obtenerPorId(int id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Libro no encontrado con ID: " + id)); // Reemplazado[cite: 2]
    }

    @Override
    public List<Libro> listarTodos() {
        return libroRepository.findAll();
    }

    @Override
    public List<Libro> buscarLibros(String titulo, String autor, String isbn, String categoria) {
        return libroRepository.buscarConFiltros(titulo, autor, isbn, categoria);
    }
}