package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusCampCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment de variables del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 * 		- No comenci per majúscula seguida de minúscula
 * 		- No contingui espais
 * - Comprova que el tipus:
 * 
 */
public class ExpedientTipusCampValidator implements ConstraintValidator<ExpedientTipusCamp, ExpedientTipusCampCommand>{

	private String codiMissatge;
	@Autowired
	private ExpedientTipusService expedientTipusService;

	@Override
	public void initialize(ExpedientTipusCamp anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(ExpedientTipusCampCommand camp, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (camp.getCodi() != null) {
			CampDto repetit = expedientTipusService.campFindAmbCodiPerValidarRepeticio(
					camp.getExpedientTipusId(),
					camp.getCodi());
			if(repetit != null && (camp.getId() == null || !camp.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
			// Que no comenci amb una majúscula seguida de minúscula
			if (camp.getCodi().matches("^[A-Z]{1}[a-z]{1}.*")) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("error.camp.codi.maymin", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
			// Que no contingui punts
			if (camp.getCodi().contains(".")) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("error.camp.codi.char.nok", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
			if (camp.getCodi().contains(" ")) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("error.camp.codi.char.espai", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		if (camp.getTipus() != null) {
				if (camp.getTipus().equals(CampTipusDto.ACCIO)) {
					context.buildConstraintViolationWithTemplate(
							"Per al tipus ACCIO és obligatori un nom de handler")
							.addNode("tipus")
							.addConstraintViolation();	
					valid = false;
				}
				if (camp.getTipus().equals(CampTipusDto.SELECCIO) || camp.getTipus().equals(CampTipusDto.SUGGEST)) {
					if ((camp.getDominiId() == null 
							&& !camp.isDominiIntern()) 
							&& camp.getEnumeracioId() == null 
							&& camp.getConsultaId() == null) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(this.codiMissatge + ".enumdomcons.buit", null))
								.addNode("dominiId")
								.addConstraintViolation();	
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(this.codiMissatge + ".enumdomcons.buit", null))
								.addNode("dominiIntern")
								.addConstraintViolation();	
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(this.codiMissatge + ".enumdomcons.buit", null))
								.addNode("enumeracioId")
								.addConstraintViolation();	
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage(this.codiMissatge + ".enumdomcons.buit", null))
								.addNode("consultaId")
								.addConstraintViolation();	
						valid = false;
					} else {
						if (camp.getDominiId() != null) {
							if(camp.getDominiIdentificador() == null || "".equals(camp.getDominiIdentificador().trim())) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("NotEmpty", null))
										.addNode("dominiIdentificador")
										.addConstraintViolation();	
								valid = false;								
							}
							if(camp.getDominiCampText() == null || "".equals(camp.getDominiCampText().trim())) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("NotEmpty", null))
										.addNode("dominiCampText")
										.addConstraintViolation();	
								valid = false;								
							}
							if(camp.getDominiCampValor() == null || "".equals(camp.getDominiCampValor().trim())) {
								context.buildConstraintViolationWithTemplate(
										MessageHelper.getInstance().getMessage("NotEmpty", null))
										.addNode("dominiCampValor")
										.addConstraintViolation();	
								valid = false;								
							}
						}
					}
				}
		}
		return valid;
	}

}
