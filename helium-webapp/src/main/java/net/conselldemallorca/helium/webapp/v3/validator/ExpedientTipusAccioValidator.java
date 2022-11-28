package net.conselldemallorca.helium.webapp.v3.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.AccioService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAccioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment d'accios del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 */
public class ExpedientTipusAccioValidator implements ConstraintValidator<ExpedientTipusAccio, ExpedientTipusAccioCommand>{

	private String codiMissatge;
	@Autowired
	private AccioService accioService;
	@Autowired
	private DissenyService dissenyService;

	@Override
	public void initialize(ExpedientTipusAccio anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusAccioCommand accio, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (accio.getCodi() != null) {
			AccioDto repetit = accioService.findAmbCodi(
					accio.getExpedientTipusId(),
					accio.getDefinicioProcesId(),
					accio.getCodi());
			if(repetit != null && (accio.getId() == null || !accio.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		// Si hi ha expedient tipus informat comprova que la acció pertany a la darrera
		// versió
		if (accio.getExpedientTipusId() != null && accio.getDefprocJbpmKey() != null && accio.getJbpmAction() != null ) {
			// Darrera versió de la definició de procés
			DefinicioProcesDto definicioProces = dissenyService.findDarreraVersioForExpedientTipusIDefProcCodi(
																accio.getExpedientTipusId(),
																accio.getDefprocJbpmKey());
			List<String> accions = dissenyService.findAccionsJbpmOrdenades(definicioProces.getId());
			if (!accions.contains(accio.getJbpmAction())) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".accio.no.existeix", null))
						.addNode("jbpmAction")
						.addConstraintViolation();	
				valid = false;
			}
		}
		if (accio.getTipus() == null) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage(this.codiMissatge + ".tipus.null", null))
					.addNode("tipus")
					.addConstraintViolation();	
			valid = false;
		} else {
			if (AccioTipusEnumDto.HANDLER.equals(accio.getTipus())) {
				if (accio.getDefprocJbpmKey() == null || accio.getDefprocJbpmKey().trim().isEmpty()) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty", null))
							.addNode("defprocJbpmKey")
							.addConstraintViolation();	
					valid = false;
				}
				if (accio.getJbpmAction() == null || accio.getJbpmAction().trim().isEmpty()) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty", null))
							.addNode("jbpmAction")
							.addConstraintViolation();	
					valid = false;
				}
			} else if (AccioTipusEnumDto.SCRIPT.equals(accio.getTipus()) 
					&& (accio.getScript() == null || accio.getScript().trim().isEmpty())) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("script")
						.addConstraintViolation();	
				valid = false;
			} 
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		return valid;
	}

}
