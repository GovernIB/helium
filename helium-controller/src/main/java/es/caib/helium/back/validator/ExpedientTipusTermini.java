package es.caib.helium.back.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validador per al manteniment de dominis del tipus d'expedient: 
 * - Comprova que el codi no estigui duplicat
 */
@Documented
@Constraint(validatedBy = ExpedientTipusTerminiValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpedientTipusTermini {

	String message() default "expedient.tipus.termini.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
