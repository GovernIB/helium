package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAccioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment d'accios del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 * 		- No comenci per majúscula seguida de minúscula
 * 		- No contingui espais
 * 
 */
public class ExpedientTipusAccioValidator implements ConstraintValidator<ExpedientTipusAccio, ExpedientTipusAccioCommand>{

	private String codiMissatge;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(ExpedientTipusAccio anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusAccioCommand accio, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (accio.getCodi() != null) {
			AccioDto repetit = expedientTipusService.accioFindAmbCodiPerValidarRepeticio(
					accio.getExpedientTipusId(),
					accio.getCodi());
			if(repetit != null && (accio.getId() == null || !accio.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
			// Que no comenci amb una majúscula seguida de minúscula
			if (accio.getCodi().matches("^[A-Z]{1}[a-z]{1}.*")) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("error.camp.codi.maymin", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
			// Que no contingui punts
			if (accio.getCodi().contains(".")) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("error.camp.codi.char.nok", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
			if (accio.getCodi().contains(" ")) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("error.camp.codi.char.espai", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		return valid;
	}

}
