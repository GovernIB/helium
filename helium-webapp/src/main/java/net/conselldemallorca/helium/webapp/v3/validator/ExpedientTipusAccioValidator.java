package net.conselldemallorca.helium.webapp.v3.validator;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AccioTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.handlers.HandlerDto;
import net.conselldemallorca.helium.v3.core.api.dto.handlers.HandlerParametreDto;
import net.conselldemallorca.helium.v3.core.api.service.AccioService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAccioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment d'accios del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 * - Segons el tipus
 * 	- Pels handlers predefinits:
 * 		- Comprova que la classe no estigui buida
 * 		- Comprova que tots els paràmetres obligatoris estiguin informats
 * 		- Comprova que el handler predefinit existeixi a la llista.
 */
public class ExpedientTipusAccioValidator implements ConstraintValidator<ExpedientTipusAccio, ExpedientTipusAccioCommand>{

	private String codiMissatge;
	@Autowired
	private AccioService accioService;
	@Autowired
	private DissenyService dissenyService;
	@Autowired
	private ExpedientTipusService expedientTipusService;

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
				if ((accio.getDefprocJbpmKey() == null || accio.getDefprocJbpmKey().trim().isEmpty()) && !accio.isPerEstats()) {
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
			} else if (AccioTipusEnumDto.HANDLER_PREDEFINIT.equals(accio.getTipus())) {
				if (accio.getPredefinitClasse() == null || accio.getPredefinitClasse().isEmpty()) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("NotEmpty", null))
							.addNode("predefinitClasse")
							.addConstraintViolation();	
					valid = false;					
				} else {
					HandlerDto handler = null;
					for (HandlerDto h : dissenyService.getHandlersPredefinits()) {
						if (accio.getPredefinitClasse().equals(h.getClasse())) {
							handler = h;
							break;
						}
					}
					if (handler == null) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(this.codiMissatge + ".predefinit.classe.no.existeix", new Object[] { accio.getPredefinitClasse()}))
								.addNode("predefinitClasse")
								.addConstraintViolation();	
						valid = false;
					} else {
						// Comprova que tots els paràmetres obligatoris estiguin informats
						List<String> parametresObligatoris = new ArrayList<String>();
						for (HandlerParametreDto parametre : handler.getParametres()) {
							if (parametre.isObligatori() 
								&& (!accio.getPredefinitDades().containsKey(parametre.getParam()) 
										|| accio.getPredefinitDades().get(parametre.getParam()) == null)  
								&& (!accio.getPredefinitDades().containsKey(parametre.getVarParam()) 
										|| accio.getPredefinitDades().get(parametre.getVarParam()) == null)) 
							{
								parametresObligatoris.add(parametre.getNom());
							}
						}
						if (!parametresObligatoris.isEmpty()) {
							context.buildConstraintViolationWithTemplate(
									MessageHelper.getInstance().getMessage(this.codiMissatge + ".predefinit.parametres.obligatoris", new Object[] { parametresObligatoris}))
									.addNode("predefinitClasse")
									.addConstraintViolation();	
							valid = false;
						}
					}
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
