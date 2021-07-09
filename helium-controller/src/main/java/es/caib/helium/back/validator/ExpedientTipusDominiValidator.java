package es.caib.helium.back.validator;

import es.caib.helium.back.command.ExpedientTipusDominiCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.DominiDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.service.DominiService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador per al manteniment de dominis tipus d'expedient: 
 * - Comprova que el codi no estigui duplicat
 */
public class ExpedientTipusDominiValidator implements ConstraintValidator<ExpedientTipusDomini, ExpedientTipusDominiCommand>{

	private String codiMissatge;
	@Autowired
	DominiService dominiService;
	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(ExpedientTipusDomini anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusDominiCommand domini, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (domini.getCodi() != null) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();

			DominiDto repetit = dominiService.findAmbCodi(
					entornActual.getId(),
					domini.getExpedientTipusId(),
					domini.getCodi());
			if(repetit != null && (domini.getId() == null || !domini.getId().equals(repetit.getId()))) {
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
