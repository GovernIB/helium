package es.caib.helium.back.validator;

import java.io.IOException;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.back.command.DefinicioProcesDesplegarCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import es.caib.helium.logic.helper.ExpedientTipusHelper;

/**
 * Validador per a la comanda de desplegament d'un procés .bpmn de la definició de procés.
 */
public class DefinicioProcesDesplegarValidator implements ConstraintValidator<DefinicioProcesDesplegar, DefinicioProcesDesplegarCommand>{

	private String codiMissatge;

	@Autowired
	ExpedientTipusService expedientTipusService;
	@Autowired
	DefinicioProcesService definicioProcesService;
	@Autowired
	DissenyService dissenyService;
	@Resource
	ExpedientTipusHelper expedientTipusHelper;

	@Override
	public void initialize(DefinicioProcesDesplegar anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(DefinicioProcesDesplegarCommand command, ConstraintValidatorContext context) {
		boolean valid = true;

		try {
			if (command.getFile().getBytes() == null || command.getFile().getBytes().length == 0) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage( this.codiMissatge + ".arxiu.buit"))
				.addPropertyNode("file")
				.addConstraintViolation();
				valid = false;
			}
			if(command.isActualitzarExpedientsActius()) {
				if (command.getExpedientTipusId() != null) {
					ExpedientTipusDto expedientTipusDto = expedientTipusService.findAmbIdPermisDissenyar(command.getEntornId(), command.getExpedientTipusId());
					if(!expedientTipusDto.isPermisDefprocUpdate() && !expedientTipusDto.isPermisAdministration()) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage( this.codiMissatge + ".permisos"))
						.addNode("actualitzarExpedientsActius")
						.addConstraintViolation();
						valid = false;
					}
				}
			}
		} catch (IOException e) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage( this.codiMissatge + ".error.lectura"))
			.addNode("file")
			.addConstraintViolation();
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();

		return valid;
	}

}
