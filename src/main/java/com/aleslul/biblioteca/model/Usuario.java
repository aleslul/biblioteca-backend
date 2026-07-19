package com.aleslul.biblioteca.model;

import com.aleslul.biblioteca.model.enums.EstadoUsuario;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 150, nullable = false, unique = true)
    private String correo;

    @Column(length = 255,nullable = false)
    private String contrasena;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(length = 20)
    private String telefono;

    @Column(length = 20, unique = true)
    private String dni;

    // Fecha de alta: se autogenera al crear el usuario, el cliente no la envía
    @Column(name = "fecha_membresia")
    private LocalDate fechaMembresia;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;
}