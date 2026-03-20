package es.caib.helium.back.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validador per a la comanda de desplegament d'un .par de la definició de correu.
 */
@Constraint(validatedBy = AccioDesplegarValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AccioDesplegar {

	String message() default "expedient.tipus.accio.desplegar";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
