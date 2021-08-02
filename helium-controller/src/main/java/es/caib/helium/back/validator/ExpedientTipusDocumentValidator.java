package es.caib.helium.back.validator;

import es.caib.helium.back.command.ExpedientTipusDocumentCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.service.DefinicioProcesService;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador per al manteniment de variables del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 * 		- No comenci per majúscula seguida de minúscula
 * 		- No contingui espais
 * - Comprova que el tipus:
 * 
 */
public class ExpedientTipusDocumentValidator implements ConstraintValidator<ExpedientTipusDocument, ExpedientTipusDocumentCommand>{

	private String codiMissatge;

	@Autowired
	DocumentService documentService;
	@Autowired
	ExpedientTipusService expedientTipusService;
	@Autowired
	DefinicioProcesService definicioProcesService;

	@Override
	public void initialize(ExpedientTipusDocument anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusDocumentCommand document, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (document.getCodi() != null) {
			DocumentDto repetit = documentService.findAmbCodi(
					document.getExpedientTipusId(),
					document.getDefinicioProcesId(),
					document.getCodi(),
					false);
			if(repetit != null && (document.getId() == null || !document.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		if (!valid) {
			context.disableDefaultConstraintViolation();
		}
		return valid;
	}

}