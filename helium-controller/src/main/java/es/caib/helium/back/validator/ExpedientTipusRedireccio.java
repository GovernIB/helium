package es.caib.helium.back.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validador per al manteniment de redireccions del tipus d'expedient: 
 * - Comprova que la data final sigui major o igual a la data inicial
 */
@Documented
@Constraint(validatedBy = ExpedientTipusRedireccioValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpedientTipusRedireccio {

	String message() default "expedient.tipus.redireccio.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
