package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;
import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusEstatCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment d'estats tipus d'expedient: 
 * - Comprova que el codi no estigui duplicat
 */
public class ExpedientTipusEstatValidator implements ConstraintValidator<ExpedientTipusEstat, ExpedientTipusEstatCommand>{

	private String codiMissatge;
	@Autowired
	ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(ExpedientTipusEstat anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusEstatCommand estat, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (estat.getCodi() != null) {
			EstatDto repetit = expedientTipusService.estatFindAmbCodi(
					estat.getExpedientTipusId(),
					estat.getCodi());
			if(repetit != null && (estat.getId() == null || !estat.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbId(estat.getExpedientTipusId());
		if (ExpedientTipusTipusEnumDto.ESTAT.equals(expedientTipus.getTipus()) && estat.getOrdre() < 0) {
			context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage(this.codiMissatge + ".ordre.buit", null))
					.addNode("ordre")
					.addConstraintViolation();
			valid = false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();

		return valid;
	}

}
