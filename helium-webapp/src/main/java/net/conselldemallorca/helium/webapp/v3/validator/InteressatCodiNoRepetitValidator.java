/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Comprova que el codi d'interessat no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatCodiNoRepetitValidator implements ConstraintValidator<InteressatCodiNoRepetit, InteressatCommand> {

	private InteressatCodiNoRepetit anotacio;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;

	@Override
	public void initialize(InteressatCodiNoRepetit anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(
			InteressatCommand command,
			ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha un interessat amb el mateix codi
		if (command.getCodi() != null) {
			
			InteressatDto repetit = expedientInteressatService.findAmbCodiAndExpedientId(command.getCodi(), command.getExpedientId());
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
