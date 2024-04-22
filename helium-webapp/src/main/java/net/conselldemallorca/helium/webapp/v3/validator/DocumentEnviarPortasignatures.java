package net.conselldemallorca.helium.webapp.v3.validator;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;



/**
 * Command per validar el formulari de documents per enviar al portasignatures
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Documented
@Constraint(validatedBy = DocumentEnviarPortasignaturesValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentEnviarPortasignatures {

	String message() default "error.enviar.portasignatures.validacio";
	
	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
