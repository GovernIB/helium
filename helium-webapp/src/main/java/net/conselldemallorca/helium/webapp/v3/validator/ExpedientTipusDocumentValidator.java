package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDocumentCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment de variables del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 * 		- No comenci per majúscula seguida de minúscula
 * 		- No contingui espais
 * - Comprova les opcions del portasignatures el tipus flux o simple ha d'estar informat
 * 	- Simple: el tipus paral·lel o sèrie i els responsables han d'estar informats. la llargada dels responsables no pot ser major a 1024 comptant el separador
 *	- Flux: el flux id ha d'estar informat
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
		// Comprova les opcions del portasignatures
		// el tipus flux o simple ha d'estar informat
		// simple: el tipus paral·lel o sèrie i els responsables han d'estar informats. la llargada dels responsables no pot ser major a 1024 comptant el separador
		// flux: el flux id ha d'estar informat
		if (document.isPortafirmesActiu()) {
			if(document.getPortafirmesFluxTipus()!=null) {
				if(document.getPortafirmesFluxTipus().equals(PortafirmesTipusEnumDto.FLUX) && document.getPortafirmesFluxId()==null) {
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage("expedient.tipus.document.form.camp.portafirmes.flux.id.buit"))
							.addNode("portafirmesFluxId")
							.addConstraintViolation();	
					valid = false;
				}
				if((document.getPortafirmesFluxTipus().equals(PortafirmesTipusEnumDto.SIMPLE))&&
						(document.getPortafirmesResponsables()==null || document.getPortafirmesResponsables().length()==0 || document.getPortafirmesResponsables().length()>1024))
				{
					context.buildConstraintViolationWithTemplate(
							MessageHelper.getInstance().getMessage( "expedient.tipus.document.form.camp.portafirmes.responsables.buit"))
							.addNode("portafirmesResponsables")
							.addConstraintViolation();
					valid = false;
				}
			} else {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("expedient.tipus.document.form.camp.portafirmes.tipus.buit"))
						.addNode("fluxTipus")
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
