package net.conselldemallorca.helium.webapp.v3.validator;

import java.io.IOException;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.helper.ExpedientTipusHelper;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.DefinicioProcesDesplegarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per a la comanda de desplegament d'un .par de la definició de correu.
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
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
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
				.addNode("file")
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
