package es.caib.helium.back.validator;

import es.caib.helium.back.command.ExpedientTipusIntegracioNotibCommand;
import es.caib.helium.back.helper.MessageHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validació de la integració amb el Notib. Valida:
 * - Si s'activa:
 * 	- El codi de l'emisor no pot estar buit
 * 	- El codi del procediment no pot estar buit
 */
public class ExpedientTipusIntegracioNotibValidator implements ConstraintValidator<ExpedientTipusIntegracioNotib, ExpedientTipusIntegracioNotibCommand>{

	@SuppressWarnings("unused")
	private String codiMissatge;

	@Override
	public void initialize(ExpedientTipusIntegracioNotib anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusIntegracioNotibCommand command, ConstraintValidatorContext context) {
		boolean valid = true;

		if (command.getNotibActiu()) {
			
			if (command.getNotibEmisor() == null || command.getNotibEmisor().trim().isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("notibEmisor")
						.addConstraintViolation();
				valid = false;
			}
			
			if (command.getNotibCodiProcediment() == null || command.getNotibCodiProcediment().trim().isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("notibCodiProcediment")
						.addConstraintViolation();
				valid = false;
			}
		}
		return valid;
	}

}
