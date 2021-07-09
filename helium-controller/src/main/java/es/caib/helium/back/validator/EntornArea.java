package es.caib.helium.back.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command per validar el formulari de EntornArea
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Documented
@Constraint(validatedBy = EntornAreaValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EntornArea {
	
	String message() default "error.area.validacio";

	String codiRepetit() default "error.area.codi.repetit";
	
	String codiBuit() default "error.area.codi.buit";

	String nomBuit() default "error.area.nom.buit";
	
	String tipusAreaBuida() default "error.area.tipus.area.buida";
	
	String areaInexistent() default "error.carrec.area.inexistent";
	
	String pareEllMateix() default "error.area.pare.no.ella.mateixa";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
