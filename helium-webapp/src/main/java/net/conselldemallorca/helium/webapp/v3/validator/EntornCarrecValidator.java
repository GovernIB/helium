package net.conselldemallorca.helium.webapp.v3.validator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.CarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.service.EntornAreaService;
import net.conselldemallorca.helium.v3.core.api.service.EntornCarrecService;
import net.conselldemallorca.helium.webapp.v3.command.EntornCarrecCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

public class EntornCarrecValidator implements ConstraintValidator<EntornCarrec, EntornCarrecCommand> {
	
	private EntornCarrec anotacio;
	@Autowired
	private EntornCarrecService entornCarrecService;
	@Autowired 
	EntornAreaService entornAreaService;
	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(EntornCarrec anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(EntornCarrecCommand command, ConstraintValidatorContext context) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		boolean valid = true;

		CarrecDto carrec = entornCarrecService.findByEntornAndCodi(entornActual.getId(), command.getCodi());
		if (carrec != null && !carrec.getId().equals(command.getId())) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.codiRepetit()))
			.addNode("codi").addConstraintViolation();
			valid = false;
		}
		
		if (entornAreaService.findAmbId(entornActual.getId(), command.getAreaId()) == null) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.areaInexistent()))
			.addNode("areaId").addConstraintViolation();
			valid = false;
		}
		
		return valid;
	}

}
