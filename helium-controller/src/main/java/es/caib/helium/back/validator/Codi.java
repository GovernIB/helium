package es.caib.helium.back.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Validador pel camp codi dels commands.
 * - Comprova que el codi:
 * 		- No comenci per majúscula seguida de minúscula
 * 		- No contingui espais
 */
@Documented
@Constraint(validatedBy=CodiValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Codi {
	
	String message() default "codi";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};	
}
