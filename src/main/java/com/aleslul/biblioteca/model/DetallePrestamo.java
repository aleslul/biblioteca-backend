package com.aleslul.biblioteca.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detalle_prestamo")
@Data
public class DetallePrestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prestamo", nullable = false)
    private Prestamo prestamo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;
    @Column(nullable = false)
    private Boolean devuelto;
}