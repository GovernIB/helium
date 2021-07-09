package es.caib.helium.back.validator;

import es.caib.helium.back.command.EntornAreaCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornAreaDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.service.EntornAreaService;
import es.caib.helium.logic.intf.service.EntornTipusAreaService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EntornAreaValidator implements ConstraintValidator<EntornArea, EntornAreaCommand> {

	private EntornArea anotacio;
	@Autowired
	private EntornAreaService entornAreaService;
	@Autowired
	private EntornTipusAreaService entornTipusAreaService;
	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(EntornArea anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(EntornAreaCommand command, ConstraintValidatorContext context) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		boolean valid = true;

		EntornAreaDto repetit = entornAreaService.findAmbCodiByEntorn(command.getCodi(), entornActual.getId());
		if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.codiRepetit()))
					.addNode("codi").addConstraintViolation();
			valid = false;
		}
		
		if(entornTipusAreaService.findAmbId(entornActual.getId(), command.getTipusId()) == null) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.tipusAreaBuida()))
			.addNode("tipusId").addConstraintViolation();
			valid = false;
		}
		
		if (command.getPareId() != null && entornAreaService.findAmbId(entornActual.getId(), command.getPareId()) == null) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.areaInexistent()))
			.addNode("pareId").addConstraintViolation();
			valid = false;
		}
		
		if (command.getPareId() != null && command.getId() != null && command.getPareId().longValue() == command.getId().longValue()) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.pareEllMateix()))
			.addNode("pareId").addConstraintViolation();
			valid = false;
		}

		return valid;
	}
}
