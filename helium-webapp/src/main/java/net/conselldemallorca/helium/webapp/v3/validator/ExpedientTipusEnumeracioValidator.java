package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEnumeracioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment de enumeracios tipus d'expedient: 
 * - Comprova que el codi no estigui duplicat
 */
public class ExpedientTipusEnumeracioValidator implements ConstraintValidator<ExpedientTipusEnumeracio, ExpedientTipusEnumeracioCommand>{

	private String codiMissatge;
	@Autowired
	ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(ExpedientTipusEnumeracio anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusEnumeracioCommand enumeracio, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (enumeracio.getCodi() != null) {
			EnumeracioDto repetit = expedientTipusService.enumeracioFindAmbCodi(
					enumeracio.getExpedientTipusId(),
					enumeracio.getCodi());
			if(repetit != null && (enumeracio.getId() == null || !enumeracio.getId().equals(repetit.getId()))) {
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
