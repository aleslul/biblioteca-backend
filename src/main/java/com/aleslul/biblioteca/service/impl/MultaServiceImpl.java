package com.aleslul.biblioteca.service.impl;

import com.aleslul.biblioteca.exception.RecursoNoEncontradoException;
import com.aleslul.biblioteca.model.Multa;
import com.aleslul.biblioteca.model.enums.EstadoMulta;
import com.aleslul.biblioteca.repository.MultaRepository;
import com.aleslul.biblioteca.service.MultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MultaServiceImpl implements MultaService {

    @Autowired
    private MultaRepository multaRepository;

    @Override
    public Multa pagarMulta(int idMulta) {
        Multa multa = multaRepository.findById(idMulta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Multa no encontrada con ID: " + idMulta)); // Reemplazado[cite: 2]
        multa.setEstado(EstadoMulta.PAGADA);
        return multaRepository.save(multa);
    }

    @Override
    public Multa condonarMulta(int idMulta) {
        Multa multa = multaRepository.findById(idMulta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Multa no encontrada con ID: " + idMulta)); // Reemplazado[cite: 2]
        multa.setEstado(EstadoMulta.CONDONADA);
        return multaRepository.save(multa);
    }

    // RF pendiente: el frontend necesita listar multas (GET /api/multas no existía)
    @Override
    public List<Multa> obtenerTodas() {
        return multaRepository.findAll();
    }

    @Override
    public List<Multa> obtenerPorUsuario(int idUsuario) {
        return multaRepository.findByDevolucion_Prestamo_Usuario_Id(idUsuario);
    }

    @Override
    public BigDecimal calcularAlgoritmoMulta(int diasRetraso, BigDecimal precioLibro) {
        BigDecimal montoCalculado;

        // Tramo 1: Primeros 7 días -> S/. 2 por día[cite: 2]
        if (diasRetraso <= 7) {
            montoCalculado = BigDecimal.valueOf(diasRetraso * 2L);
        }
        // Tramo 2: Del día 8 al 14 -> S/. 14 base más S/. 4 por cada día extra[cite: 2]
        else if (diasRetraso <= 14) {
            int diasExtraTramo2 = diasRetraso - 7;
            montoCalculado = BigDecimal.valueOf(14 + (diasExtraTramo2 * 4L));
        }
        // Tramo 3: A partir del día 15 -> 50% del valor del libro más la mora fija acumulada de 42 soles[cite: 2]
        else {
            BigDecimal moraAcumuladaSemanasPrevias = BigDecimal.valueOf(42); // 14 soles (S1) + 28 soles (S2)[cite: 2]
            BigDecimal cincuentaPorCientoLibro = precioLibro.multiply(BigDecimal.valueOf(0.5));
            montoCalculado = cincuentaPorCientoLibro.add(moraAcumuladaSemanasPrevias);
        }

        // Regla de Protección del Activo (Tope máximo): Valor completo del Libro + 20% de gastos operativos[cite: 2]
        BigDecimal topeMaximoPermitido = precioLibro.multiply(BigDecimal.valueOf(1.2));

        if (montoCalculado.compareTo(topeMaximoPermitido) > 0) {
            montoCalculado = topeMaximoPermitido;
        }

        // Los montos son en soles; se normaliza a 2 decimales (céntimos) para evitar
        // escalas inconsistentes al persistir/comparar BigDecimal.
        return montoCalculado.setScale(2, RoundingMode.HALF_UP);
    }
}