package es.caib.helium.back.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validador per al manteniment de integració amb NOTIB del tipus d'expedient: 
 */
@Documented
@Constraint(validatedBy = ExpedientTipusIntegracioNotibValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpedientTipusIntegracioNotib {

	String message() default "expedient.tipus.integracio.notib.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
