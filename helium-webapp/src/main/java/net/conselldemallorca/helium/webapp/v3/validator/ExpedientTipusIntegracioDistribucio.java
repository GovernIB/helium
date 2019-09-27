package net.conselldemallorca.helium.webapp.v3.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validador per a la comanda de modificar les dades d'integració amb Distribució. Valida:
 * - Que no hi hagi cap altre tipus d'expedient amb el mateix codi de procediment en cap altre entorn.
 */
@Constraint(validatedBy = ExpedientTipusIntegracioDistribucioValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpedientTipusIntegracioDistribucio {

	String message() default "expedient.tipus.integracio.distribucio.validacio.codiProcediment.repetit";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
