package com.aleslul.biblioteca.service;

import com.aleslul.biblioteca.model.Multa;
import java.math.BigDecimal;

public interface MultaService {
    Multa pagarMulta(int idMulta);
    Multa condonarMulta(int idMulta);
    BigDecimal calcularAlgoritmoMulta(int diasRetraso, BigDecimal precioLibro);
}