package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.dto.CarrecJbpmIdDto;
import es.caib.helium.logic.intf.service.CarrecService;
import net.conselldemallorca.helium.webapp.v3.command.CarrecCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

public class CarrecValidator implements ConstraintValidator<Carrec, CarrecCommand> {

	private Carrec anotacio;
	@Autowired
	private CarrecService carrecService;
	
	@Override
	public void initialize(Carrec anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(CarrecCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		CarrecJbpmIdDto repetit = carrecService.findAmbCodi(command.getCodi());
		if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.codiRepetit()))
					.addNode("codi").addConstraintViolation();
			valid = false;
		}
		return valid;
	}

}
