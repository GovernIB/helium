package es.caib.helium.back.validator;

import es.caib.helium.back.command.EntornTipusAreaCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.dto.EntornTipusAreaDto;
import es.caib.helium.logic.intf.service.EntornTipusAreaService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
