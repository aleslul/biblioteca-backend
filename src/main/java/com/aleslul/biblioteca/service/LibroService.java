package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.model.Libro;
import java.util.List;

public interface LibroService {
    Libro guardarLibro(Libro libro);
    Libro obtenerPorId(int id);
    List<Libro> listarTodos();
    List<Libro> buscarLibros(String titulo, String autor, String isbn, String categoria);
    Libro actualizarLibro(int id, Libro datosActualizados);
    void eliminarLibro(int id);
}