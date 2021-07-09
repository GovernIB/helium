package es.caib.helium.back.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validador per a la comanda de processament d'anotacions pendents. Comprova:
 * - Si l'acció és crear:
 * 	- Ha d'informar el tipus d'expedient
 * - Si l'acció és incorporar:
 * 	- Ha d'informar el tipus d'expedient
 * 	- Ha d'informar l'expedient
 */
@Documented
@Constraint(validatedBy = AnotacioAcceptarValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AnotacioAcceptar {

	String message() default "expedient.tipus.validacio";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
