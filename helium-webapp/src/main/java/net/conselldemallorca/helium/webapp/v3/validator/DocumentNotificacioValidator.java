package net.conselldemallorca.helium.webapp.v3.validator;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import com.google.common.collect.Lists;

import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientInteressatService;
import net.conselldemallorca.helium.webapp.v3.command.DocumentNotificacioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MessageHelper;

public class DocumentNotificacioValidator implements ConstraintValidator<DocumentNotificacio, DocumentNotificacioCommand> {

	private final List<String> COMUNICACIO_SIR_ALLOWED_FORMATS = Lists.newArrayList("JPG", "JPEG", "ODT", "ODP", "ODS", "ODG", 
												"DOCX", "XLSX", "PPTX", "PDF", "PNG", "RTF", 
												"SVG", "TIFF", "TXT", "XML", "XSIG");
	
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private DocumentHelperV3 documentHelperV3;
	
	
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
		DocumentStore document = documentHelperV3.findById(documentStoreId);
		String arxiuExtensio = null;
		int indexPunt = document.getArxiuNom().lastIndexOf(".");
		if (indexPunt != -1) {
			arxiuExtensio = document.getArxiuNom().substring(indexPunt + 1);
		} else {
			arxiuExtensio = null;
		}
		
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
