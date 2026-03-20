package es.caib.helium.back.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import es.caib.helium.back.command.DocumentExpedientEnviarPortasignaturesCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.commons.dto.PortafirmesTipusEnumDto;


/**
 * Validador per l'enviament al portasignatures
 * - Si es de tipus FLUX:
 * 		- el fluxId ha d'estar informat
 */
public class DocumentEnviarPortasignaturesValidator implements ConstraintValidator<DocumentEnviarPortasignatures, DocumentExpedientEnviarPortasignaturesCommand>{

	
	@Override
	public void initialize(DocumentEnviarPortasignatures constraintAnnotation) {		
	}
	
	@Override
	public boolean isValid(DocumentExpedientEnviarPortasignaturesCommand command, ConstraintValidatorContext context) {
		boolean valid = true;
		if(PortafirmesTipusEnumDto.FLUX.equals(command.getPortafirmesFluxTipus()) && 
				(command.getPortafirmesEnviarFluxId()==null || command.getPortafirmesEnviarFluxId().isEmpty())) {
			//En principi no s'ha seleccionat cap flux de firma, pero pot ser hi hagi un flux creat desde el frame de portafib
			if (command.getPortafirmesNouFluxId()==null || command.getPortafirmesNouFluxId().isEmpty()) {
				context.buildConstraintViolationWithTemplate(
						MessageHelper.getInstance().getMessage("expedient.tipus.document.form.camp.portafirmes.flux.id.buit"))
						.addNode("portafirmesEnviarFluxId")
						.addConstraintViolation();	
				valid=false;
			} else {
				command.setPortafirmesEnviarFluxId(command.getPortafirmesNouFluxId());
			}
		}
		if(command.getMotiu()==null || command.getMotiu().equals("")) {
			context.buildConstraintViolationWithTemplate(
					MessageHelper.getInstance().getMessage("expedient.tipus.document.form.camp.portafirmes.motiu.buit"))
					.addNode("motiu")
					.addConstraintViolation();	
			valid=false;
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
	
		return valid;
	}

	

}
