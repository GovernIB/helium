package es.caib.helium.back.validator;

import java.io.IOException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.helium.back.command.ExpedientTipusAccioDesplegarCommand;
import es.caib.helium.back.helper.MessageHelper;

/**
 * Validador per a la comanda de desplegament d'un .par de la definició de correu.
 */
public class AccioDesplegarValidator implements ConstraintValidator<AccioDesplegar, ExpedientTipusAccioDesplegarCommand>{

	private String codiMissatge;
	
	@Override
	public void initialize(AccioDesplegar anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusAccioDesplegarCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		
		try {
			if (command.getFile().getBytes() == null || command.getFile().getBytes().length == 0) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage( this.codiMissatge + ".arxiu.buit"))
				.addNode("file")
				.addConstraintViolation();
				valid = false;
			} 
		} catch (IOException e) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage( this.codiMissatge + ".error.lectura"))
			.addNode("file")
			.addConstraintViolation();
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
