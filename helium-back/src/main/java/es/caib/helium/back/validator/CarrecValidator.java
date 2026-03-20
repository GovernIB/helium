package es.caib.helium.back.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.back.command.CarrecCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.commons.dto.CarrecJbpmIdDto;
import es.caib.helium.logic.intf.service.CarrecService;

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
