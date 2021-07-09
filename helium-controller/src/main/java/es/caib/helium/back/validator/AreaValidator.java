package es.caib.helium.back.validator;

import es.caib.helium.back.command.AreaCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.dto.AreaJbpmIdDto;
import es.caib.helium.logic.intf.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AreaValidator implements ConstraintValidator<Area, AreaCommand> {

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
