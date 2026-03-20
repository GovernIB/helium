package es.caib.helium.back.validator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.back.command.EntornAreaMembreCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.CarrecDto;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.plugins.persones.PersonesPlugin;
import es.caib.helium.logic.intf.service.EntornAreaMembreService;
import es.caib.helium.logic.intf.service.EntornCarrecService;
import es.caib.helium.service.utils.GlobalProperties;

public class EntornAreaMembreValidator
		implements ConstraintValidator<EntornAreaMembre, EntornAreaMembreCommand> {

	private EntornAreaMembre anotacio;
	private PersonesPlugin personesPlugin;
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
			
			String pluginClass = GlobalProperties.getInstance().getProperty("app.persones.plugin.class");
			if (pluginClass == null) {
				return false;
			}

			personesPlugin = (PersonesPlugin) (Class.forName(pluginClass).newInstance());
			if (personesPlugin.findAmbCodi(command.getCodi()) == null) {
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
