package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.AreaJbpmIdDto;
import net.conselldemallorca.helium.v3.core.api.service.AreaService;
import net.conselldemallorca.helium.webapp.v3.command.AreaCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

public class AreaValidator implements ConstraintValidator<Area, AreaCommand>{

	private Area anotacio;
	@Autowired
	private AreaService areaService;
	
	@Override
	public void initialize(Area anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(AreaCommand command, ConstraintValidatorContext context) {
		
		boolean valid = true;
		AreaJbpmIdDto repetit = areaService.findAmbCodi(command.getCodi());
		if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.codiRepetit()))
					.addNode("codi").addConstraintViolation();
			valid = false;
		}
		return valid;
	}

}
