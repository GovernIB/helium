package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusRedireccioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

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
		return valid;
	}

}
