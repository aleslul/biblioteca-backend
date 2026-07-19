package com.aleslul.biblioteca.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogSistemaResponseDTO {
    private int id;
    private int idUsuario;
    private String nombreUsuario;
    private String tipoAccion;
    private String descripccion; // Respeta exactamente el nombre de atributo 'descripccion' del modelo original
    private LocalDateTime fechaRegistro;
}