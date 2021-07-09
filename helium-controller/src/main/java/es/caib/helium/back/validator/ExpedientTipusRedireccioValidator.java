package es.caib.helium.back.validator;

import es.caib.helium.back.command.ExpedientTipusRedireccioCommand;
import es.caib.helium.back.helper.MessageHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validador per al manteniment de redireccions del tipus d'expedient: 
 * - Comprova que la data final sigui major o igual a la data inicial
 */
public class ExpedientTipusRedireccioValidator implements ConstraintValidator<ExpedientTipusRedireccio, ExpedientTipusRedireccioCommand>{

	private String codiMissatge;

	@Override
	public void initialize(ExpedientTipusRedireccio anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusRedireccioCommand redireccio, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova que la data final sigui major o igual a la data inicial
		if (redireccio.getDataInici() != null && redireccio.getDataFi() != null && redireccio.getDataFi().before(redireccio.getDataInici())) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(this.codiMissatge + ".dataFi.anterior", null))
					.addNode("dataFi")
					.addConstraintViolation();	
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}

}
