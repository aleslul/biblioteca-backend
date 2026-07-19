package com.aleslul.biblioteca.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionResponseDTO {
    private int id;
    private String tipo;
    private String mensaje;
    private LocalDateTime fechaCreacion;
    private boolean leida;
}