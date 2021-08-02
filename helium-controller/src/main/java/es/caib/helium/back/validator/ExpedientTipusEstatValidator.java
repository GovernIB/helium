package es.caib.helium.back.validator;

import es.caib.helium.back.command.ExpedientTipusEstatCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador per al manteniment d'estats tipus d'expedient: 
 * - Comprova que el codi no estigui duplicat
 */
public class ExpedientTipusEstatValidator implements ConstraintValidator<ExpedientTipusEstat, ExpedientTipusEstatCommand>{

	private String codiMissatge;
	@Autowired
	ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(ExpedientTipusEstat anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusEstatCommand estat, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (estat.getCodi() != null) {
			EstatDto repetit = expedientTipusService.estatFindAmbCodi(
					estat.getExpedientTipusId(),
					estat.getCodi());
			if(repetit != null && (estat.getId() == null || !estat.getId().equals(repetit.getId()))) {
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