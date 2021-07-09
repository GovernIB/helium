package es.caib.helium.back.validator;

import es.caib.helium.back.command.ExpedientTipusConsultaCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.dto.ConsultaDto;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador per al manteniment de consultes del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 * 		- No comenci per majúscula seguida de minúscula
 * 		- No contingui espais
 */
public class ExpedientTipusConsultaValidator implements ConstraintValidator<ExpedientTipusConsulta, ExpedientTipusConsultaCommand>{

	private String codiMissatge;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(ExpedientTipusConsulta anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusConsultaCommand consulta, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (consulta.getCodi() != null) {
			ConsultaDto repetit = expedientTipusService.consultaFindAmbCodiPerValidarRepeticio(
					consulta.getExpedientTipusId(),
					consulta.getCodi());
			if(repetit != null && (consulta.getId() == null || !consulta.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		return valid;
	}

}
