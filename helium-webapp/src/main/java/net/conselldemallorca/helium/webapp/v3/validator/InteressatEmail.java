package net.conselldemallorca.helium.webapp.v3.validator;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint de validació que controla que camp email és obligatori si està habilitada l'entrega a la Direcció Electrònica Hablitada (DEH)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=InteressatEmailValidator.class)
public @interface InteressatEmail {

	String message() default "És obligatori informar el camp email si la opció Entrega a la Direcció Electrònica Hablitada (DEH) està habilitada";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
