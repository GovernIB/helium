package es.caib.helium.back.validator;

import es.caib.helium.back.command.EntornAreaMembreCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.CarrecDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.EntornAreaMembreService;
import es.caib.helium.logic.intf.service.EntornCarrecService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EntornAreaMembreValidator
		implements ConstraintValidator<EntornAreaMembre, EntornAreaMembreCommand> {

	private EntornAreaMembre anotacio;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private EntornCarrecService entornCarrecService;
	@Autowired
	private EntornAreaMembreService entornAreaMembreService;
	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(EntornAreaMembre anotacio) {
		this.anotacio = anotacio;
		;
	}

	@Override
	public boolean isValid(EntornAreaMembreCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		try {

			String[] path = request.getRequestURI().split("/");
			if (path.length != 7) {
				valid = false;
			}
			Long entornAreaId;
			try {				
				entornAreaId = Long.parseLong(path[path.length-3]);
			} catch (Exception e) {
				return false;
			}
			if (entornAreaMembreService.findAmbCodiAndAreaId(command.getCodi(), entornAreaId) != null){
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(anotacio.codiPersonaRepetit())).addNode("codi")
						.addConstraintViolation();
				valid = false;
			}

			if (aplicacioService.findPersonaAmbCodi(command.getCodi()) == null) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(anotacio.codiPersonaInexistent())).addNode("codi")
						.addConstraintViolation();
				valid = false;
			}

			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			CarrecDto carrec = entornCarrecService.findAmbId(entornActual.getId(), command.getCarrecId());
			if (command.getCarrecId() != null
					&& carrec == null) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(anotacio.carrecInexistent())).addNode("carrecId")
						.addConstraintViolation();
				valid = false;
			}
			
			if (command.getCarrecId() != null && carrec.getArea().getId().longValue() != entornAreaId.longValue()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(anotacio.carrecInexistent())).addNode("carrecId")
						.addConstraintViolation();
				valid = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			valid = false;
		}

		return valid;
	}

}
