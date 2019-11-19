/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
public class InteressatValidator implements ConstraintValidator<Interessat, InteressatCommand> {

	@Autowired
	private ExpedientInteressatService expedientInteressatService;


	@Override
	public void initialize(Interessat anotacio) {
	}

	@Override
	public boolean isValid(InteressatCommand command, ConstraintValidatorContext context) {
		boolean valid = true;

		// Comprova si ja hi ha un interessat amb el mateix codi
		if (command.getCodi() != null) {
			
			InteressatDto repetit = expedientInteressatService.findAmbCodiAndExpedientId(command.getCodi(), command.getExpedientId());
			if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("interessat.validacio.codi.repetit")).
				addNode("codi").
				addConstraintViolation();	
				valid = false;
			}
		}
		
		if (command.getTipus() != null) {
			switch (command.getTipus()) {
			case ADMINISTRACIO:
				// Codi DIR3 per administracions
				if (command.getDir3Codi() == null || command.getDir3Codi().isEmpty()) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("interessat.validacio.dir3Codi.obligatori")).
					addNode("dir3Codi").
					addConstraintViolation();
					valid = false;
				}
				break;
			case FISICA:
			case JURIDICA:
				break;
			}
		}

		// Llinatge1 per persones físiques
		if (command.getTipus() == InteressatTipusEnumDto.FISICA && (command.getLlinatge1() == null || command.getLlinatge1().isEmpty())) {
			
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage("interessat.validacio.llinatge.obligatori")).
			addNode("llinatge1").
			addConstraintViolation();
			valid = false;
		}
		
		// Línies entrega postal
		try {
			if (command.getEntregaPostal() && (command.getLinia1() == null || command.getLinia1().isEmpty())) {
				context
					.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("interessat.form.valid.linia"))
					.addNode("linia1")
					.addConstraintViolation();
				valid = false;
			}
			if (command.getEntregaPostal() && (command.getLinia2() == null || command.getLinia2().isEmpty())) {
				context
					.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("interessat.form.valid.linia"))
					.addNode("linia2")
					.addConstraintViolation();
				valid = false;
			}
			if (command.getEntregaPostal() && (command.getCodiPostal() == null || command.getCodiPostal().isEmpty())) {
				context
					.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("interessat.form.valid.linia"))
					.addNode("codiPostal")
					.addConstraintViolation();
				valid = false;
			}
		} catch (final Exception ex) {
			logger.error("Ha d'informar els camps codi postal, línia 1 i línia 2 quan hi ha entrega postal", ex);
        }
		
		// email per entregues DEH
		try {
			
			
			if (command.getEntregaDeh() && (command.getEmail() == null || command.getEmail().isEmpty())) {
				context
					.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("interessat.form.valid.email"))
					.addNode("email")
					.addConstraintViolation();
				valid = false;
			}
		} catch (final Exception ex) {
        	logger.error("Ha d'informar el email quan hi ha entrega DEH", ex);
        }

		if (!valid)
			context.disableDefaultConstraintViolation();

		return valid;
	}

	private static final Logger logger = LoggerFactory.getLogger(InteressatValidator.class);

}
