package net.conselldemallorca.helium.webapp.v3.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.AvisDto;
import net.conselldemallorca.helium.v3.core.api.service.AvisService;
import net.conselldemallorca.helium.webapp.v3.command.AvisCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

public class AvisValidator implements ConstraintValidator<Avis, AvisCommand>{

	private Avis avis;
	@Autowired
	private AvisService avisService;
	final String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

	
	@Override
	public void initialize(Avis anotacio) {
		this.avis = anotacio;
	}

	@Override
	public boolean isValid(AvisCommand command, ConstraintValidatorContext context) {
		
		boolean valid = true;
		AvisDto repetit = avisService.findById(command.getId());
		Pattern p = Pattern.compile(regex);
		if(command.getHoraInici()!=null) {	
			Matcher m = p.matcher(command.getHoraInici());
			valid=m.matches();
			if(!valid) {
				context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage("error.avis.validacio", null))
					.addConstraintViolation();	
			}
		}
		if(command.getHoraFi()!=null) {
		   Matcher m = p.matcher(command.getHoraInici());
	       valid = m.matches();
	       if(!valid) {
				context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage("error.avis.validacio", null))
					.addConstraintViolation();	
			}
		}
		if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(avis.codiRepetit()))
					.addNode("codi").addConstraintViolation();
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}

}
