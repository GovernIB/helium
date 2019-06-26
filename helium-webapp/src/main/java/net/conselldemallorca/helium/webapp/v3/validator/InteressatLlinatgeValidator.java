/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.core.model.hibernate.Interessat;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;
import net.conselldemallorca.helium.webapp.v3.command.InteressatCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Comprova que quan persona es fisica llinatge no es buit
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatLlinatgeValidator implements ConstraintValidator<InteressatLlinatge, InteressatCommand> {

	private InteressatLlinatge anotacio;

	@Override
	public void initialize(InteressatLlinatge anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(InteressatCommand command, ConstraintValidatorContext context) {
		boolean valid = true;

		if (command.getTipus() == InteressatTipusEnumDto.FISICA && (command.getLlinatge1() == null || command.getLlinatge1().isEmpty())) {
			
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(anotacio.message())).
			addNode("llinatge1").
			addConstraintViolation();
			valid = false;
		}
			
			

		return valid;
	}

}
