package net.conselldemallorca.helium.webapp.v3.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
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
//		if (camp.getTipus() != null) {
//				if (camp.getTipus().equals(CampTipusDto.ACCIO)) {
//					ValidationUtils.rejectIfEmpty(errors, "jbpmAction", "not.blank");
//				}
//				if (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST)) {
//					if ((camp.getDomini() == null && !camp.isDominiIntern()) && camp.getEnumeracio() == null && camp.getConsulta() == null) {
//						errors.rejectValue("enumeracio", "error.camp.enumdomcons.buit");
//						errors.rejectValue("domini", "error.camp.enumdomcons.buit");
//						errors.rejectValue("consulta", "error.camp.enumdomcons.buit");
//					} else	if (camp.getDomini() != null && camp.isDominiIntern()){
//							errors.rejectValue("domini", "error.camp.domini");
//							errors.rejectValue("dominiIntern", "error.camp.domini");
//					} else {
//						if(camp.getDomini() != null){
//							if (camp.getEnumeracio() != null && camp.getConsulta() != null) {
//								errors.rejectValue("enumeracio", "error.camp.enumdomcons.tots");
//								errors.rejectValue("domini", "error.camp.enumdomcons.tots");
//								errors.rejectValue("consulta", "error.camp.enumdomcons.tots");
//							} else if (camp.getEnumeracio() != null) {
//								errors.rejectValue("enumeracio", "error.camp.enumdomcons.tots");
//								errors.rejectValue("domini", "error.camp.enumdomcons.tots");
//							} else if (camp.getConsulta() != null) {
//								errors.rejectValue("domini", "error.camp.enumdomcons.tots");
//								errors.rejectValue("consulta", "error.camp.enumdomcons.tots");
//							}
//						} else {
//							if(camp.isDominiIntern()){
//								if (camp.getEnumeracio() != null && camp.getConsulta() != null) {
//									errors.rejectValue("enumeracio", "error.camp.enumdomcons.tots");
//									errors.rejectValue("dominiIntern", "error.camp.enumdomcons.tots");
//									errors.rejectValue("consulta", "error.camp.enumdomcons.tots");
//								} else if (camp.getEnumeracio() != null) {
//									errors.rejectValue("enumeracio", "error.camp.enumdomcons.tots");
//									errors.rejectValue("dominiIntern", "error.camp.enumdomcons.tots");
//								} else if (camp.getConsulta() != null) {
//									errors.rejectValue("dominiIntern", "error.camp.enumdomcons.tots");
//									errors.rejectValue("consulta", "error.camp.enumdomcons.tots");
//								} 
//							} else 	if(camp.getEnumeracio() != null && camp.getConsulta() != null) {
//								errors.rejectValue("enumeracio", "error.camp.enumdomcons.tots");
//								errors.rejectValue("consulta", "error.camp.enumdomcons.tots");
//							}
//						}
//						if (camp.getDomini() != null) {
//							ValidationUtils.rejectIfEmpty(errors, "dominiId", "not.blank");
//							ValidationUtils.rejectIfEmpty(errors, "dominiCampText", "not.blank");
//							ValidationUtils.rejectIfEmpty(errors, "dominiCampValor", "not.blank");
//						}
//					}
//				}
//			}
//		}
		return valid;
	}

}
