package com.aleslul.biblioteca.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Valida que un correo pertenezca al dominio institucional configurado
 * en application.properties (app.correo.dominio-institucional).
 * Requerido explícitamente por RF1 / HU01 en el informe RUP.
 */
@Documented
@Constraint(validatedBy = CorreoInstitucionalValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorreoInstitucional {
    String message() default "El correo debe pertenecer al dominio institucional";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}