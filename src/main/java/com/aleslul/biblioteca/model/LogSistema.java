package com.aleslul.biblioteca.model;

import com.aleslul.biblioteca.model.enums.TipoAccion;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs_sistema")
@Data

public class LogSistema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_accion", nullable = false)
    private TipoAccion tipoAccion;

    @Column(length = 500)
    private String descripccion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}