package com.aleslul.biblioteca.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrestamoResponseDTO {
    private int id;
    private String nombreUsuario;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaVencimiento;
    private String estado;
    private List<DetallePrestamoResponseDTO> detalles;
}