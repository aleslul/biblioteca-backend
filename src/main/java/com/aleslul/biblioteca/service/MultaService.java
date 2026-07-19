package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.model.Multa;
import java.math.BigDecimal;
import java.util.List;

public interface MultaService {
    Multa pagarMulta(int idMulta);
    Multa condonarMulta(int idMulta);
    BigDecimal calcularAlgoritmoMulta(int diasRetraso, BigDecimal precioLibro);

    // RF pendiente: el frontend necesita listar multas (GET /api/multas no existía)
    List<Multa> obtenerTodas();
    List<Multa> obtenerPorUsuario(int idUsuario);
}