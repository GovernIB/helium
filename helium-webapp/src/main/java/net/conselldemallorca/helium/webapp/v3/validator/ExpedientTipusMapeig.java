package net.conselldemallorca.helium.webapp.v3.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validador per al manteniment de mapejos de la integració amb Sistra del tipus d'expedient: 
 * - Comprova que el codi de helium sigui obligatori per variables i documents i que no estigi repetit
 * - Comprova que el codi de sistra sigui obligatori per a tots els casos i que no estigui repetit
 * per a documents adjunts.
 */
@Documented
@Constraint(validatedBy = ExpedientTipusMapeigValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpedientTipusMapeig {

	String message() default "expedient.tipus.integracio.tramits.mapeig.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
