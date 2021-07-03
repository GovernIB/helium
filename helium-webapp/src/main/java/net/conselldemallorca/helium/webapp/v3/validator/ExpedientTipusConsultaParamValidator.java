package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.dto.ConsultaCampDto;
import es.caib.helium.logic.intf.dto.ConsultaCampDto.TipusConsultaCamp;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusConsultaParamCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment de consultes del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 */
public class ExpedientTipusConsultaParamValidator implements ConstraintValidator<ExpedientTipusConsultaParam, ExpedientTipusConsultaParamCommand>{

	private String codiMissatge;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(ExpedientTipusConsultaParam anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusConsultaParamCommand parametre, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha un camp de la conaulta per tipus amb el mateix codi
		if (parametre.getCampCodi() != null) {
			ConsultaCampDto repetit = expedientTipusService.consultaCampFindAmbTipusICodiPerValidarRepeticio(
					parametre.getConsultaId(), 
					TipusConsultaCamp.PARAM, 
					parametre.getCampCodi());
			if(repetit != null && (parametre.getId() == null || !parametre.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("campCodi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		return valid;
	}

}
