package net.conselldemallorca.helium.webapp.v3.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Command per validar el formulari de EntornCarrec
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Documented
@Constraint(validatedBy = EntornCarrecValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EntornCarrec {
	
	String message() default "error.carrec.validacio";
	
	String codiBuit() default "error.carrec.codi.buit";
	
	String codiRepetit() default "error.carrec.codi.repetit";
	
	String areaInexistent() default "error.carrec.area.inexistent";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
