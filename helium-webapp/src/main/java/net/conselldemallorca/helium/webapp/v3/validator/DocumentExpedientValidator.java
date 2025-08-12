package net.conselldemallorca.helium.webapp.v3.validator;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.ParametreHelper;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaValidacioDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTipusFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
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
	@Autowired
	private DocumentService documentService;
	@Resource(name="documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private ParametreHelper parametreHelper;
	
	@Override
	public void initialize(DocumentExpedient anotacio) {
	}

	@Override
	public boolean isValid(DocumentExpedientCommand command, ConstraintValidatorContext context) {

		boolean valid = true;
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(command.getExpedientId());
		boolean ntiActiu = expedient.isNtiActiu();
		
		if (command.isValidarArxius() && !command.isGenerarPlantilla()) {
			if (command.getArxiu() == null || command.getArxiu().isEmpty()) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("not.blank"))
						.addNode("arxiu")
						.addConstraintViolation();
				valid = false;
			}
		}
		
		Long MAX_FILE_SIZE = parametreHelper.getMidaMaximaFitxerInBytes();
		// Si la mida del fitxer es major que MAX_FILE_SIZE es retrona un error de validació  
		if (MAX_FILE_SIZE != null && command.getArxiu() != null && command.getArxiu().getSize() > MAX_FILE_SIZE) {
			String max = parametreHelper.getMidaMaximaFitxer();
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("error.fixer.max.size", new String[] {max}))
					.addNode("arxiu")
					.addConstraintViolation();
			valid = false;
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
		
		if (command.isAmbFirma() 
				&& command.getArxiu() != null && !command.getArxiu().isEmpty()) {
			// Valida la firma del document ja sigui inclosa o separada
			try {
				byte[] contingutArxiu = IOUtils.toByteArray(command.getArxiu().getInputStream());
				String arxiuContentType = command.getArxiu().getContentType();
				byte[] firmaContingut = null;
				if (command.getFirma() != null && command.getFirma().getSize() > 0) {
					firmaContingut = IOUtils.toByteArray(command.getFirma().getInputStream());
				}
				ArxiuFirmaValidacioDetallDto firmaEstat = documentService.validateFirmaDocument(
						contingutArxiu,
						arxiuContentType,
						command.getTipusFirma(),
						firmaContingut);
				if (firmaEstat == null) {
					// sense firmes
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("error.document.firma.nula"))
					.addNode("ambFirma")
					.addConstraintViolation();
					valid = false;				
				} else if (!firmaEstat.isValid()) {
					// Firma invàlida
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("error.document.firma.invalida"))
					.addNode("ambFirma")
					.addConstraintViolation();
					valid = false;				
				}
			} catch(Exception e) {
				String errMsg = MessageHelper.getInstance().getMessage("error.document.validacio.firma.error", new Object[] {e.getMessage()});
				context.buildConstraintViolationWithTemplate(errMsg)
				.addNode("ambFirma")
				.addConstraintViolation();
				valid = false;				
			}
		}
		if (!valid)
			context.disableDefaultConstraintViolation();
		
		return valid;
	}
}
