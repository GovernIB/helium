package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/** Validador pel camp codi dels commands.
 * - Comprova que el codi:
 * 		- No comenci per majúscula seguida de minúscula
 * 		- No contingui espais
 */
public class CodiValidator implements ConstraintValidator<Codi, String>{

	@Override
	public void initialize(Codi constraintAnnotation) {
	}

	@Override
	public boolean isValid(String codi, ConstraintValidatorContext context) {

		boolean valid = true;
		
		if (codi != null) {
			//  Els codis de variables no poden començar per majúscula seguida de minúscula, guió baix o número.
			if (codi.matches("^[A-Z]{1}[a-z_$.0-9a-z]{1}.*")) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("error.camp.codi.char.mayguionum", null))
						.addConstraintViolation();	
				valid = false;
			}
			// Que no contingui punts
			if (codi.contains(".")) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("error.camp.codi.char.nok", null))
						.addConstraintViolation();	
				valid = false;
			}
			if (codi.contains(" ")) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("error.camp.codi.char.espai", null))
						.addConstraintViolation();	
				valid = false;
			}
			// Els codis de variables només poden contenir caràcters sense accentuació que siguin majúscula, minúscula, número o guió baix.
			if (!codi.matches("[a-z0-9A-Z_]*?")) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("error.camp.codi.char.caracters.nomespodencontenir", null))
						.addConstraintViolation();	
				valid = false;
			}
			if (!valid)
				context.disableDefaultConstraintViolation();
		}
		return valid;
	}
	
	public static boolean isValid(String codi) {
		boolean valid = true;		
		if (codi.matches("^[A-Z]{1}[a-z]{1}.*")	// Que no comenci amb una majúscula seguida de minúscula 
				|| codi.contains(".")			// Que no contingui punts
				|| codi.contains(" ")) {
			valid = false;
		}
		return valid;
	}

}
