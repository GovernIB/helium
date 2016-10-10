package net.conselldemallorca.helium.webapp.v3.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validador per a la càrrega d'un fitxer d'exportació
 * durant la importació de dades del tipus d'expedient.
 */
@Constraint(validatedBy = DefinicioProcesUploadValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DefinicioProcesUpload {

	String message() default "definicio.proces.upload.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
