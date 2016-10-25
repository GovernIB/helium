package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.webapp.v3.command.AgrupacioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment d'agrupació de variables del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 */
public class AgrupacioValidator implements ConstraintValidator<Agrupacio, AgrupacioCommand>{

	private String codiMissatge;
	@Autowired
	private CampService campService;

	@Override
	public void initialize(Agrupacio anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(AgrupacioCommand agrupacio, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (agrupacio.getCodi() != null) {
			CampAgrupacioDto repetit = campService.agrupacioFindAmbCodiPerValidarRepeticio(
						agrupacio.getExpedientTipusId(),
						agrupacio.getDefinicioProcesId(),
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
