package es.caib.helium.back.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command per a validar que el codi del tipus d'àrea no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Documented
@Constraint(validatedBy = EntornTipusAreaCodiNoRepetitValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EntornTipusArea {

	String message() default "error.areatipus.validacio";
	
	String codiRepetit() default "error.areatipus.codi.repetit";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
