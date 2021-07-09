package es.caib.helium.back.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validador per a la comanda d'alta massiva d'expedients per CSV.
 */
@Constraint(validatedBy = ExpedientAltaMassivaValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpedientAltaMassiva {

	String message() default "expedient.alta.massiva.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
