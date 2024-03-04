package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.CarrecJbpmIdDto;
import net.conselldemallorca.helium.v3.core.api.service.CarrecService;
import net.conselldemallorca.helium.webapp.v3.command.CarrecCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

public class CarrecValidator implements ConstraintValidator<Carrec, CarrecCommand> {

	private Carrec carrec;
	@Autowired
	private CarrecService carrecService;
	
	@Override
	public void initialize(Carrec carrec) {
		this.carrec = carrec;
	}

	@Override
	public boolean isValid(CarrecCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		CarrecJbpmIdDto repetit = carrecService.findAmbCodiAndGrup(command.getCodi(), command.getGrup());
		if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(carrec.codiGrupRepetit()))
					.addNode("codi").addConstraintViolation();
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}

}
