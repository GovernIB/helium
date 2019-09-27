package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioDistribucioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per a la comanda de modificar les dades d'integració amb Distribució. Valida:
 * - Que no hi hagi cap altre tipus d'expedient amb el mateix codi de procediment en cap altre entorn.
 */
public class ExpedientTipusIntegracioDistribucioValidator implements ConstraintValidator<ExpedientTipusIntegracioDistribucio, ExpedientTipusIntegracioDistribucioCommand>{

	private String codiMissatge;
	@Autowired
	private ExpedientTipusService expedientTipusService;
	
	@Override
	public void initialize(ExpedientTipusIntegracioDistribucio anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusIntegracioDistribucioCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		
		if (command.isActiu()) {
			if (command.getCodiProcediment() == null || "".equals(command.getCodiProcediment().trim())) {
				// Comprova que el codi de procediment no sigui null
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("codiProcediment")
						.addConstraintViolation();
				valid = false;
			} else {
				// Comprova que no hi hagi cap altre tipus d'expedient a l'entorn actiu pel mateix codi de procediment
				ExpedientTipusDto expedientTipus = expedientTipusService.findPerDistribucio(command.getCodiProcediment()); 
				if (expedientTipus != null 
						&& expedientTipus.isDistribucioActiu() 
						&& !expedientTipus.getId().equals(command.getId())) {
					context.buildConstraintViolationWithTemplate(
							// expedient.tipus.integracio.distribucio.validacio.codiProcediment.repetit=Ja existeix el tipus d''expedient "{0} - {1}" a l''entorn "{2} - {3}" actiu pel codi de procediment "{4}"
							MessageHelper.getInstance().getMessage(codiMissatge, new Object[] {
									expedientTipus.getCodi(),
									expedientTipus.getNom(),
									expedientTipus.getEntorn().getCodi(),
									expedientTipus.getEntorn().getNom(),
									command.getCodiProcediment()
							}))
							.addNode("codiProcediment")
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
