/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.validator;

import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.EstatReglaCommand;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Comprova que el codi d'entorn no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstatReglaValidator implements ConstraintValidator<EstatRegla, EstatReglaCommand> {

	private EstatRegla anotacio;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(EstatRegla anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(
			EstatReglaCommand command,
			ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha un tipus d'expedient amb el mateix codi
//		if (command.getCodi() != null) {
//			EntornDto repetit = entornService.findAmbCodi(command.getCodi());
//			if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
//				context.buildConstraintViolationWithTemplate(
//						MessageHelper.getInstance().getMessage(anotacio.message())).
//				addNode("codi").
//				addConstraintViolation();
//				valid = false;
//			}
//		}
		return valid;
	}

}
