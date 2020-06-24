/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Command per a validar els interessats. Valida:
 * - Que no hi hagi un interessat amb el mateix codi
 * - Que el llinatge1 sigui obligatori per persones físiques.
 * - Que el nif sigui obligatori per persones físiques i jurídiques.
 * - Que el codi dir3 sigui obligatori per administracions
 * - Que les línies estiguin informades si està la entrega postal activa
 * - Que l'email estigui informat per entreges deh
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Documented
@Constraint(validatedBy = InteressatValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Interessat {

	String message() default "interessat.validacio.llinatge.obligatori";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
