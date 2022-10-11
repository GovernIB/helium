package net.conselldemallorca.helium.webapp.v3.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/** Validador pel camp codi dels commands.
 * - Comprova que el codi:
 * 		- No comenci per majúscula seguida de minúscula, guió baix o número
 * 		- No contingui espais ni punts
 */
@Documented
@Constraint(validatedBy=CodiVariableValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CodiVariable {
	
	String message() default "codi";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};	
}
