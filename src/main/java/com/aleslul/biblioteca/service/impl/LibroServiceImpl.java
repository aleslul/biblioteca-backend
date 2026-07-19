package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.exception.ReglaNegocioException;
import com.aleslul.biblioteca.model.Libro;
import com.aleslul.biblioteca.model.enums.EstadoReserva;
import com.aleslul.biblioteca.repository.DetallePrestamoRepository;
import com.aleslul.biblioteca.repository.LibroRepository;
import com.aleslul.biblioteca.repository.ReservaRepository;
import com.aleslul.biblioteca.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private DetallePrestamoRepository detallePrestamoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

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

    @Override
    public Libro actualizarLibro(int id, Libro datosActualizados) {
        Libro libro = obtenerPorId(id);

        int copiasPrestadasActualmente = libro.getCopiesTotal() - libro.getCopiesAvailable();
        if (datosActualizados.getCopiesTotal() < copiasPrestadasActualmente) {
            throw new ReglaNegocioException("No se puede reducir las copias totales a " +
                    datosActualizados.getCopiesTotal() + ": hay " + copiasPrestadasActualmente +
                    " copia(s) actualmente prestada(s)");
        }

        libro.setTitulo(datosActualizados.getTitulo());
        libro.setAutor(datosActualizados.getAutor());
        libro.setIsbn(datosActualizados.getIsbn());
        libro.setCategoria(datosActualizados.getCategoria());
        libro.setPrecio(datosActualizados.getPrecio());
        libro.setAnioPublicacion(datosActualizados.getAnioPublicacion());
        libro.setUbicacion(datosActualizados.getUbicacion());
        libro.setDescripcion(datosActualizados.getDescripcion());
        libro.setUrlPortada(datosActualizados.getUrlPortada());

        // Ajustar copiesAvailable manteniendo la cantidad de copias ya prestadas
        libro.setCopiesTotal(datosActualizados.getCopiesTotal());
        libro.setCopiesAvailable(datosActualizados.getCopiesTotal() - copiasPrestadasActualmente);

        return libroRepository.save(libro);
    }

    @Override
    public void eliminarLibro(int id) {
        Libro libro = obtenerPorId(id);

        if (libro.getCopiesAvailable() < libro.getCopiesTotal()) {
            throw new ReglaNegocioException("No se puede eliminar el libro '" + libro.getTitulo() +
                    "': tiene copias actualmente prestadas");
        }
        if (detallePrestamoRepository.existsByLibro_IdAndDevueltoFalse(id)) {
            throw new ReglaNegocioException("No se puede eliminar el libro '" + libro.getTitulo() +
                    "': tiene préstamos históricos asociados sin devolver");
        }
        if (!reservaRepository.findByLibroIdAndEstado(id, EstadoReserva.ACTIVA).isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar el libro '" + libro.getTitulo() +
                    "': tiene reservas activas");
        }

        libroRepository.delete(libro);
    }
}