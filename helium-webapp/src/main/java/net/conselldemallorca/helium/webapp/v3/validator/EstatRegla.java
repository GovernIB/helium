/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command per a validar les dades d'una regla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Documented
@Constraint(validatedBy = EstatReglaValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EstatRegla {

	String message() default "error.estat.regla";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
