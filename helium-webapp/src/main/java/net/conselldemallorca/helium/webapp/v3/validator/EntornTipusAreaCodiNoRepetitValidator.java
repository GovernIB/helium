package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.EntornTipusAreaDto;
import net.conselldemallorca.helium.v3.core.api.service.EntornTipusAreaService;
import net.conselldemallorca.helium.webapp.v3.command.EntornTipusAreaCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Comprova que el codi del tipus d'àrea no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntornTipusAreaCodiNoRepetitValidator implements ConstraintValidator<EntornTipusArea, EntornTipusAreaCommand>{

	private EntornTipusArea anotacio;
	@Autowired
	private EntornTipusAreaService entornTipusAreaService;

	@Override
	public void initialize(EntornTipusArea anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(EntornTipusAreaCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha un tipus d'àrea amb el mateix codi
		if (command.getCodi() != null) {
			EntornTipusAreaDto repetit = entornTipusAreaService.findAmbCodi(command.getCodi());
			if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.codiRepetit())).
				addNode("codi").
				addConstraintViolation();	
				valid = false;
			}
		}

		return valid;
	}
}
