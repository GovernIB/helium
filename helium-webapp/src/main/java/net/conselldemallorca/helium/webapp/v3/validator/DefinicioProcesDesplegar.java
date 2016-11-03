package net.conselldemallorca.helium.webapp.v3.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validador per a la comanda de desplegament d'un .par de la definició de correu.
 */
@Constraint(validatedBy = DefinicioProcesDesplegarValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DefinicioProcesDesplegar {

	String message() default "definicio.proces.desplegar.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
