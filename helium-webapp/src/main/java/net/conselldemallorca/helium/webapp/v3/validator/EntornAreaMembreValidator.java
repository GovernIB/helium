package net.conselldemallorca.helium.webapp.v3.validator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.v3.core.api.dto.CarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.service.EntornAreaMembreService;
import net.conselldemallorca.helium.v3.core.api.service.EntornCarrecService;
import net.conselldemallorca.helium.webapp.v3.command.EntornAreaMembreCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

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
