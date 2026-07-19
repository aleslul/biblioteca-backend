package com.aleslul.biblioteca.model;

import com.aleslul.biblioteca.model.enums.TipoNotificacion;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// RF10: notificación dirigida a un usuario. A diferencia de LogSistema (auditoría interna),
// esta entidad representa mensajes que el usuario final debe poder consultar.
@Entity
@Table(name = "notificaciones")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private TipoNotificacion tipo;

    @Column(length = 500, nullable = false)
    private String mensaje;

    // id del préstamo o libro relacionado, según el tipo — permite evitar notificaciones duplicadas
    @Column(name = "id_referencia", nullable = false)
    private Integer idReferencia;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean leida = false;
}