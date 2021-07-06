package es.caib.helium.dada.validacions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import org.springframework.messaging.handler.annotation.Payload;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Constraint(validatedBy = DadaCodiValidator.class)
public @interface DadaValidacio {
	
	String message() default "Format de la dada incorrecte";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
