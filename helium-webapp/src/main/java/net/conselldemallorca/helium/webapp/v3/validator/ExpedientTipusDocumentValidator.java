package net.conselldemallorca.helium.webapp.v3.validator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDocumentCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

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
	@Autowired
	private HttpServletRequest request;

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
					document.getCodi());
			if(repetit != null && (document.getId() == null || !document.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		// Comprova les dades NTI si el tipus d'expedient està configurat com a tal
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		Long expedientTipusId = null;
		if (document.getExpedientTipusId() != null) {
			expedientTipusId = document.getExpedientTipusId();
		} else {
			// Prova si la definició de procés no és null per obtenir l'expedient tipus id
			if (document.getDefinicioProcesId() != null){
				DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdAndEntorn(
						entornActual.getId(), 
						document.getDefinicioProcesId());
				if (definicioProces != null && definicioProces.getExpedientTipus() != null)
					expedientTipusId = definicioProces.getExpedientTipus().getId();
			}
		}
		if (expedientTipusId != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisConsultar(
					entornActual.getId(), 
					expedientTipusId);
			if (expedientTipus.isNtiActiu() 
					&& (document.getNtiTipusDocumental() == null || "".equals(document.getNtiTipusDocumental().trim()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("NotEmpty", null))
						.addNode("ntiTipusDocumental")
						.addConstraintViolation();	
				valid = false;
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();

		return valid;
	}

}
