package es.caib.helium.back.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validador per a la comanda d'exportació de dades de la definició de procés.
 */
@Constraint(validatedBy = DefinicioProcesExportarValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DefinicioProcesExportar {

	String message() default "exportar.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
