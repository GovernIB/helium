package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.logic.intf.dto.MapeigSistraDto;
import es.caib.helium.logic.intf.dto.MapeigSistraDto.TipusMapeig;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioTramitsMapeigCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment de mapejos de la integració amb Sistra del tipus d'expedient: 
 * - Comprova que el codi de helium sigui obligatori per variables i documents i que no estigi repetit
 * - Comprova que el codi de sistra sigui obligatori per a tots els casos i que no estigui repetit
 * per a documents adjunts.
 * 
 */
public class ExpedientTipusMapeigValidator implements ConstraintValidator<ExpedientTipusMapeig, ExpedientTipusIntegracioTramitsMapeigCommand>{

	private String codiMissatge;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(ExpedientTipusMapeig anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusIntegracioTramitsMapeigCommand mapeig, ConstraintValidatorContext context) {
		boolean valid = true;
		if (mapeig.getTipus() == TipusMapeig.Adjunt) {
			// documents adjunts
			if (mapeig.getCodiSistra() != null) {
				// codi repetit
				MapeigSistraDto repetit = expedientTipusService.mapeigFindAmbCodiSistraPerValidarRepeticio(
				mapeig.getExpedientTipusId(),
				mapeig.getCodiSistra());
				if(repetit != null && (mapeig.getId() == null || !mapeig.getId().equals(repetit.getId()))) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.sistra.repetit", null))
							.addNode("codiSistra")
							.addConstraintViolation();	
					valid = false;
				}
			} else {
				// codi sistra buit
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("codiSistra")
						.addConstraintViolation();	
				valid = false;								
			}
		} else {
			// variables i documents
			if (mapeig.getCodiHelium() != null) {
				// codi helium repetit
				MapeigSistraDto repetit = expedientTipusService.mapeigFindAmbCodiHeliumPerValidarRepeticio(
						mapeig.getExpedientTipusId(),
						mapeig.getCodiHelium());
				if(repetit != null && (mapeig.getId() == null || !mapeig.getId().equals(repetit.getId()))) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.helium.repetit", null))
							.addNode("codiHelium")
							.addConstraintViolation();	
					valid = false;
				}
			} else {
				// codi helium buit
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("codiHelium")
						.addConstraintViolation();	
				valid = false;				
			}
			if (mapeig.getCodiSistra() == null) {
				// codi sistra buit
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("codiSistra")
						.addConstraintViolation();	
				valid = false;				
			}
		}
		return valid;
	}

}
