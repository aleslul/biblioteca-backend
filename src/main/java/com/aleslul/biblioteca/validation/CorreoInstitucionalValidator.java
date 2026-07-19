package com.aleslul.biblioteca.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CorreoInstitucionalValidator implements ConstraintValidator<CorreoInstitucional, String> {

    // Dominio institucional configurable sin tocar código (ver application.properties).
    // NOTA: se asumió "@uss.edu.pe" como dominio de la USS; ajustar si el informe RUP
    // especifica otro (ej. correo de alumno vs. correo institucional docente/admin).
    @Value("${app.correo.dominio-institucional}")
    private String dominioInstitucional;

    @Override
    public boolean isValid(String correo, ConstraintValidatorContext context) {
        // Null/blank lo maneja @NotBlank; aquí solo validamos el dominio si hay valor.
        if (correo == null || correo.isBlank()) {
            return true;
        }
        return correo.trim().toLowerCase().endsWith(dominioInstitucional.toLowerCase());
    }
}