package com.aleslul.biblioteca.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservaResponseDTO {
    private int id;
    private int idLibro;
    private String tituloLibro;
    private int idUsuario;
    private String nombreUsuario;
    private LocalDateTime fechaReserva;
    private String estado;
}