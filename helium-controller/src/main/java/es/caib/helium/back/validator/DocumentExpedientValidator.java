package es.caib.helium.back.validator;

import es.caib.helium.back.command.DocumentExpedientCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.logic.intf.dto.DocumentTipusFirmaEnumDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.NtiEstadoElaboracionEnumDto;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.intf.service.ExpedientService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * Validador per a la comanda de creació o modificació de documents en la pestanya de documents de la tramitació de l'expedient
 * o en les accions massives.
 */
public class DocumentExpedientValidator implements ConstraintValidator<DocumentExpedient, DocumentExpedientCommand>{

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private DocumentService documentService;

	@Override
	public void initialize(DocumentExpedient anotacio) {
	}

	@Override
	public boolean isValid(DocumentExpedientCommand command, ConstraintValidatorContext context) {

		boolean valid = true;
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(command.getExpedientId());
		boolean ntiActiu = expedient.isNtiActiu();
		
		if (command.isValidarArxius()) {
			if (command.getArxiu() == null || command.getArxiu().isEmpty()) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("not.blank"))
						.addNode("arxiu")
						.addConstraintViolation();
				valid = false;
			}
			// Valida que si l'arxiu està habilitat per l'expedient llavors que l'adjunt sigui convertible a PDF
			if (!command.getArxiu().isEmpty() 
					&& expedient != null 
					&& expedient.isArxiuActiu()
					&& !documentService.isArxiuConvertiblePdf(command.getArxiuNom())) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("document.validacio.convertible.error"))
				.addNode("arxiu")
				.addConstraintViolation();
				valid = false;
			}
		}
		if (ntiActiu) {

			if(command.getNtiEstadoElaboracion() != null
					&& Arrays.asList(new NtiEstadoElaboracionEnumDto[] {
						NtiEstadoElaboracionEnumDto.COPIA_CF,
						NtiEstadoElaboracionEnumDto.COPIA_DP,
						NtiEstadoElaboracionEnumDto.COPIA_PR}).contains(command.getNtiEstadoElaboracion()) 
					&& (command.getNtiIdOrigen() == null || command.getNtiIdOrigen().isEmpty())) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("document.metadades.nti.iddoc.origen.validacio.copia"))
				.addNode("ntiIdOrigen")
				.addConstraintViolation();
				valid = false;								
			}
		}
		if (command.isValidarArxius() 
				&& command.isAmbFirma() 
				&& DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma())
				&& (command.getFirma() == null || command.getFirma().isEmpty())) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("not.blank"))
			.addNode("firma")
			.addConstraintViolation();
			valid = false;				
		}
		if (command.getDocId() == null
				&&(DocumentExpedientCommand.ADJUNTAR_ARXIU_CODI.equalsIgnoreCase(command.getDocumentCodi()) 
						|| command.getDocumentCodi() == null)
				&& (command.getNom() == null || "".equals(command.getNom().trim()))) {
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("not.blank"))
			.addNode("nom")
			.addConstraintViolation();
			valid = false;				
		}		
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}
}
