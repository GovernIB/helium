package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAgrupacioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment d'agrupació de variables del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 */
public class ExpedientTipusAgrupacioValidator implements ConstraintValidator<ExpedientTipusAgrupacio, ExpedientTipusAgrupacioCommand>{

	private String codiMissatge;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(ExpedientTipusAgrupacio anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusAgrupacioCommand agrupacio, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (agrupacio.getCodi() != null) {
			CampAgrupacioDto repetit = expedientTipusService.agrupacioFindAmbCodiPerValidarRepeticio(
					agrupacio.getExpedientTipusId(),
					agrupacio.getCodi());
			if(repetit != null && (agrupacio.getId() == null || !agrupacio.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		return valid;
	}

}
