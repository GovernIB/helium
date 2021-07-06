package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.service.TerminiService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusTerminiCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment de terminis tipus d'expedient: 
 * - Comprova que el codi no estigui duplicat
 */
public class ExpedientTipusTerminiValidator implements ConstraintValidator<ExpedientTipusTermini, ExpedientTipusTerminiCommand>{

	private String codiMissatge;
	@Autowired
	TerminiService terminiService;

	@Override
	public void initialize(ExpedientTipusTermini anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusTerminiCommand termini, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (termini.getCodi() != null) {
			TerminiDto repetit = terminiService.findAmbCodi(
					termini.getExpedientTipusId(),
					termini.getDefinicioProcesId(),
					termini.getCodi());
			if(repetit != null && (termini.getId() == null || !termini.getId().equals(repetit.getId()))) {
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
