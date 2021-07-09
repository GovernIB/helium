package es.caib.helium.back.validator;

import es.caib.helium.back.command.AnotacioAcceptarCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.dto.AnotacioAccioEnumDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador per a la comanda de processament d'anotacions pendents. Comprova:
 * - Si l'acció és crear:
 * 	- Ha d'informar el tipus d'expedient
 * - Si l'acció és incorporar:
 * 	- Ha d'informar el tipus d'expedient
 * 	- Ha d'informar l'expedient
 */
public class AnotacioAcceptarValidator implements ConstraintValidator<AnotacioAcceptar, AnotacioAcceptarCommand> {

	@Autowired 
	private ExpedientTipusService expedientTipusService;
	@Override
	public void initialize(AnotacioAcceptar anotacio) {
	}

	@Override
	public boolean isValid(AnotacioAcceptarCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		if (command.getAccio() != null) {
			if (AnotacioAccioEnumDto.CREAR.equals(command.getAccio())
					|| AnotacioAccioEnumDto.INCORPORAR.equals(command.getAccio())) {
				// Comprova que el tipus d'expedient estigui informat per a la creació o incorporació.
				if (command.getExpedientTipusId() == null) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty", null))
							.addNode("expedientTipusId")
							.addConstraintViolation();	
					valid = false;
				}
				// Si l'acció és crear 
				if (AnotacioAccioEnumDto.CREAR.equals(command.getAccio())) {
					// Comprova que estigui informat el tipus d'expedient
					if (command.getExpedientTipusId() == null) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("NotEmpty", null))
								.addNode("expedientTipusId")
								.addConstraintViolation();	
						valid = false;
					} else {
						ExpedientTipusDto expedientTipus = expedientTipusService.findAmbId(command.getExpedientTipusId());
						// Si demana títol comprova que el títol estigi informat
						if (expedientTipus.isDemanaTitol() && (command.getTitol() == null || command.getTitol().isEmpty())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("anotacio.acceptar.validacio.demana.titol", null))
									.addNode("titol")
									.addConstraintViolation();	
							valid = false;
						}
						// Si demana número comprova que el número estigi informat
						if (expedientTipus.isDemanaNumero() && (command.getNumero() == null || command.getNumero().isEmpty())) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage("anotacio.acceptar.validacio.demana.numero", null))
									.addNode("numero")
									.addConstraintViolation();	
							valid = false;
						}
					}
				}
				
				// Si l'acció és incorporar a un expedient comprova que s'hagi seleccionat l'expedient
				if (AnotacioAccioEnumDto.INCORPORAR.equals(command.getAccio())
						&& command.getExpedientId() == null) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty", null))
							.addNode("expedientId")
							.addConstraintViolation();	
					valid = false;
				}
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
