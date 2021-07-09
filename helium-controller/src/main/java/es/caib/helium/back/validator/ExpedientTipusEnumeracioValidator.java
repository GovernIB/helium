package es.caib.helium.back.validator;

import es.caib.helium.back.command.ExpedientTipusEnumeracioCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EnumeracioDto;
import es.caib.helium.logic.intf.service.EnumeracioService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador per al manteniment de enumeracios tipus d'expedient: 
 * - Comprova que el codi no estigui duplicat
 */
public class ExpedientTipusEnumeracioValidator implements ConstraintValidator<ExpedientTipusEnumeracio, ExpedientTipusEnumeracioCommand>{

	private String codiMissatge;
	@Autowired
	EnumeracioService enumeracioService;
	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(ExpedientTipusEnumeracio anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusEnumeracioCommand enumeracio, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (enumeracio.getCodi() != null) {
    		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			EnumeracioDto repetit = enumeracioService.findAmbCodi(
					entornActual.getId(),
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
