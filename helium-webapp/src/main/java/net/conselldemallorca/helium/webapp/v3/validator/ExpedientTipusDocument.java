package net.conselldemallorca.helium.webapp.v3.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validador per al manteniment de variales del tipus d'expedient: 
 * - Comprova que el codi no estigui duplicat
 * - Comprova les opcions del portasignatures el tipus flux o simple ha d'estar informat
 * 	- Simple: el tipus paral·lel o sèrie i els responsables han d'estar informats. la llargada dels responsables no pot ser major a 1024 comptant el separador
 *	- Flux: el flux id ha d'estar informat
 */
@Documented
@Constraint(validatedBy = ExpedientTipusDocumentValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpedientTipusDocument {

	String message() default "expedient.tipus.camp.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
