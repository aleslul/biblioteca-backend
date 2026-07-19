package com.aleslul.biblioteca.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DevolucionResponseDTO {
    private int id;
    private int idPrestamo;
    private LocalDateTime fechaDevolucion;
    private int diasRetraso;
    private boolean conMulta;
}