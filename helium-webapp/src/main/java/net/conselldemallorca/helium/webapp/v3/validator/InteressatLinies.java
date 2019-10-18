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
@Constraint(validatedBy=InteressatLiniesValidator.class)
public @interface InteressatLinies {

	String message() default "És obligatori informar els camps codi postal, línia1 i línia2 si la opció Entrega postal està habilitada";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
