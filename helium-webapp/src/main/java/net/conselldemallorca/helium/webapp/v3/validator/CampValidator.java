package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.CampService;
import net.conselldemallorca.helium.webapp.v3.command.CampCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per al manteniment de variables del tipus d'expedient:
 * - Comprova que el codi:
 * 		- no estigui duplicat
 * - Comprova que el tipus:
 * 
 */
public class CampValidator implements ConstraintValidator<Camp, CampCommand>{

	private String codiMissatge;
	@Autowired
	private CampService campService;

	@Override
	public void initialize(Camp anotacio) {
		codiMissatge = anotacio.message();
	}

	@Override
	public boolean isValid(CampCommand camp, ConstraintValidatorContext context) {
		boolean valid = true;
		// Comprova si ja hi ha una variable del tipus d'expedient amb el mateix codi
		if (camp.getCodi() != null) {
			CampDto repetit = campService.findAmbCodi(
						camp.getExpedientTipusId(),
						camp.getDefinicioProcesId(),
						camp.getCodi(), 
						false  );				
			if(repetit != null && (camp.getId() == null || !camp.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage(this.codiMissatge + ".codi.repetit", null))
						.addNode("codi")
						.addConstraintViolation();	
				valid = false;
			}
		}
		if (camp.getTipus() != null) {
				if (camp.getTipus().equals(CampTipusDto.ACCIO)) {
					if (camp.getDefinicioProcesId() == null &&  (camp.getDefprocJbpmKey() == null || "".equals(camp.getDefprocJbpmKey().trim()))) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("NotEmpty", null))
								.addNode("defprocJbpmKey")
								.addConstraintViolation();	
						valid = false;								
					}
					if(camp.getJbpmAction() == null || "".equals(camp.getJbpmAction().trim())) {
						context.buildConstraintViolationWithTemplate(
								MessageHelper.getInstance().getMessage("NotEmpty", null))
								.addNode("jbpmAction")
								.addConstraintViolation();	
						valid = false;								
					}
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
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}

}
