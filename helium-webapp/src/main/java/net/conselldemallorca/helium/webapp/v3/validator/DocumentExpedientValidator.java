package net.conselldemallorca.helium.webapp.v3.validator;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.util.PdfUtils;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTipusFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

/**
 * Validador per a la comanda de creació o modificació de documents en la pestanya de documents de la tramitació de l'expedient
 * o en les accions massives.
 */
public class DocumentExpedientValidator implements ConstraintValidator<DocumentExpedient, DocumentExpedientCommand>{

	@Autowired
	private ExpedientService expedientService;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	
	@Override
	public void initialize(DocumentExpedient anotacio) {
	}

	@Override
	public boolean isValid(DocumentExpedientCommand command, ConstraintValidatorContext context) {

		boolean valid = true;
		ExpedientDto expedient = expedientService.findAmbId(command.getExpedientId());
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
					&& !PdfUtils.isArxiuConvertiblePdf(command.getArxiuNom())) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("document.validacio.convertible.error"))
				.addNode("arxiu")
				.addConstraintViolation();
				valid = false;
			}
		}
		if (ntiActiu) {
			if (command.getNtiOrigen() == null) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("not.blank"))
				.addNode("ntiOrigen")
				.addConstraintViolation();
				valid = false;				
			}
			if (command.getNtiEstadoElaboracion() == null) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("not.blank"))
				.addNode("ntiEstadoElaboracion")
				.addConstraintViolation();
				valid = false;				
			}
			if (command.getNtiTipoDocumental() == null) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("not.blank"))
				.addNode("ntiTipoDocumental")
				.addConstraintViolation();
				valid = false;				
			}
			if(Arrays.asList(new NtiEstadoElaboracionEnumDto[] {
					NtiEstadoElaboracionEnumDto.COPIA_CF,
					NtiEstadoElaboracionEnumDto.COPIA_DP,
					NtiEstadoElaboracionEnumDto.COPIA_PR}).contains(command.getNtiEstadoElaboracion()) && command.getNtiIdOrigen() == null) {
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
