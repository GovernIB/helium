package es.caib.helium.back.validator;

import com.google.common.collect.Lists;
import es.caib.helium.back.command.DocumentNotificacioCommand;
import es.caib.helium.back.helper.MessageHelper;
import es.caib.helium.commons.dto.DocumentDto;
import es.caib.helium.commons.dto.EnviamentTipusEnumDto;
import es.caib.helium.commons.dto.InteressatDto;
import es.caib.helium.commons.dto.InteressatTipusEnumDto;
import es.caib.helium.logic.helper.ExpedientDocumentHelper;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import es.caib.helium.logic.intf.service.ExpedientInteressatService;
import es.caib.helium.persistence.entity.DocumentStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Map;

public class DocumentNotificacioValidator implements ConstraintValidator<DocumentNotificacio, DocumentNotificacioCommand> {
	private final List<String> COMUNICACIO_SIR_ALLOWED_FORMATS = Lists.newArrayList("JPG", "JPEG", "ODT", "ODP", "ODS", "ODG",
		"DOCX", "XLSX", "PPTX", "PDF", "PNG", "RTF",
		"SVG", "TIFF", "TXT", "XML", "XSIG");

	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private ExpedientDocumentService expedientDocumentService;

	@Override
	public void initialize(DocumentNotificacio constraintAnnotation) {}

	@Override
	public boolean isValid(DocumentNotificacioCommand value, ConstraintValidatorContext context) {
		boolean valid = true;
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attrs.getRequest();
		@SuppressWarnings("unchecked")
		Map<String, String> pathVars = (Map<String, String>) request.getAttribute(
			HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Long documentStoreId = Long.valueOf(pathVars.get("documentStoreId"));
		DocumentDto document = expedientDocumentService.findDocumentAmbId(documentStoreId);
		String arxiuExtensio = document.getArxiuExtensio();

		if( value.getEnviamentTipus() == EnviamentTipusEnumDto.COMUNICACIO &&
			!COMUNICACIO_SIR_ALLOWED_FORMATS.contains(arxiuExtensio.toUpperCase())) {
			// Si es un enviament de tipus comunicació s'ha de comprovar si algun dels titulars es una Administració
			// i si el document a enviar es un zip
			for(Long interessatId : value.getInteressatsIds()) {
				InteressatDto interssat = expedientInteressatService.findOne(interessatId);
				if(interssat.getTipus() == InteressatTipusEnumDto.ADMINISTRACIO) {
					context
						.buildConstraintViolationWithTemplate(
							MessageHelper
								.getInstance()
								.getMessage("info.document.notificar.error.comunicacio.format"))
						.addNode("enviamentTipus")
						.addConstraintViolation();
					valid = false;
					break;
				}
			}
		}
		return valid;
	}
}
