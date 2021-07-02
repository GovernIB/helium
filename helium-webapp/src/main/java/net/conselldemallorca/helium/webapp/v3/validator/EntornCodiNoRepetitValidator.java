/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.service.EntornService;
import net.conselldemallorca.helium.webapp.v3.command.EntornCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Comprova que el codi d'entorn no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntornCodiNoRepetitValidator implements ConstraintValidator<EntornCodiNoRepetit, EntornCommand> {

	private EntornCodiNoRepetit anotacio;
	@Autowired
	private EntornService entornService;

	@Override
	public void initialize(EntornCodiNoRepetit anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(
			EntornCommand command,
			ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha un tipus d'expedient amb el mateix codi
		if (command.getCodi() != null) {
			EntornDto repetit = entornService.findAmbCodi(command.getCodi());
			if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(anotacio.message())).
				addNode("codi").
				addConstraintViolation();	
				valid = false;
			}
		}
		return valid;
	}

}
