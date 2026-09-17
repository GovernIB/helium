package es.caib.helium.back.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DocumentNotificacioValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentNotificacio {
	String message() default "Document de notificació no vàlid";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
