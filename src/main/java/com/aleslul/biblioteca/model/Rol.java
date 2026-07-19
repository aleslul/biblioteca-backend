package com.aleslul.biblioteca.model;

import com.aleslul.biblioteca.model.enums.NombreRol;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255, nullable = false)
    private NombreRol nombre;
}