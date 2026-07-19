package com.aleslul.biblioteca.model;

import com.aleslul.biblioteca.model.enums.EstadoMulta;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "multas")
@Data
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_multa")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devolucion", nullable = false, unique = true)
    private Devolucion devolucion;

    @Column(name = "monto_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "precio_libro", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioLibro;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EstadoMulta estado;

    @Column(name = "fecha_calculo", nullable = false)
    private LocalDate fechaCalculo;
}