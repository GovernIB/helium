package net.conselldemallorca.helium.webapp.v3.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = EntornAreaMembreValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EntornAreaMembre {

	String message() default "error.area.nou.membre.validacio";
	
	String codiPersonaInexistent() default "error.area.nou.membre.codi.persona.inexistent";

	String codiPersonaRepetit() default "error.area.nou.membre.codi.persona.repetit";
	
	String carrecInexistent() default "error.area.nou.membre.carrec.inexistent";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
