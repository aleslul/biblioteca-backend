package com.aleslul.biblioteca.repository;

import com.aleslul.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Integer> {

    @Query("SELECT l FROM Libro l WHERE " +
            "(:titulo IS NULL OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND " +
            "(:autor IS NULL OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :autor, '%'))) AND " +
            "(:isbn IS NULL OR l.isbn = :isbn) AND " +
            "(:categoria IS NULL OR LOWER(l.categoria) LIKE LOWER(CONCAT('%', :categoria, '%')))")
    List<Libro> buscarConFiltros(@Param("titulo") String titulo,
                                 @Param("autor") String autor,
                                 @Param("isbn") String isbn,
                                 @Param("categoria") String category);

    // RF10: libros con al menos una copia disponible, para cruzar contra reservas pendientes
    List<Libro> findByCopiesAvailableGreaterThan(int copias);
}