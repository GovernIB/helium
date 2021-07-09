package es.caib.helium.back.validator;

import es.caib.helium.back.command.CarrecCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.dto.CarrecJbpmIdDto;
import es.caib.helium.logic.intf.service.CarrecService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
