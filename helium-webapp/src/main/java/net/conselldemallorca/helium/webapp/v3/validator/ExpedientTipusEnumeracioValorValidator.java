package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusEnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.service.EnumeracioService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEnumeracioValorCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment de valors d'enumeracions: 
 * - Comprova que el codi no estigui duplicat dins del llistat de valors
 */
public class ExpedientTipusEnumeracioValorValidator implements ConstraintValidator<ExpedientTipusEnumeracioValor, ExpedientTipusEnumeracioValorCommand>{

	private String codiMissatge;
	@Autowired
	EnumeracioService enumeracioService;
	
	@Override
	public void initialize(ExpedientTipusEnumeracioValor anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusEnumeracioValorCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha un valor de la enumeració amb el mateix codi
		if (command.getCodi() != null) {
			ExpedientTipusEnumeracioValorDto repetit = enumeracioService.valorFindAmbCodi(
					command.getExpedientTipusId(),
					command.getEnumeracioId(),
					command.getCodi());
			if(repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();

		return valid;
	}

}
