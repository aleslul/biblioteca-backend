package com.aleslul.biblioteca.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "libros")
@Data
@NoArgsConstructor
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private Integer id;

    @Column(length = 150, nullable = false)
    private String titulo;

    @Column(length = 100, nullable = false)
    private String autor;

    @Column(length = 20, nullable = false, unique = true)
    private String isbn;

    @Column(length = 50, nullable = false)
    private String categoria;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    // --- Campos requeridos por la maqueta (RF pendiente: alineación de modelo Libro) ---
    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;

    @Column(name = "ubicacion", length = 50)
    private String ubicacion;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "url_portada", length = 500)
    private String urlPortada;

    // --- Modelo de stock (decisión de diseño: copies en vez de "1 fila = 1 ejemplar") ---
    @Column(name = "copias_totales", nullable = false)
    private int copiesTotal = 1;

    @Column(name = "copias_disponibles", nullable = false)
    private int copiesAvailable = 1;

    /**
     * Compatibilidad con el código existente que preguntaba por disponibilidad de "el" ejemplar.
     * Ahora un libro está disponible si le queda al menos una copia libre.
     * No es un campo persistido: se deriva de copiesAvailable.
     */
    public boolean isDisponible() {
        return copiesAvailable > 0;
    }
}