/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentStoreDto;

/**
 * Controlador per iniciar un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class DocumentMetadadesNtiInfoController extends BaseExpedientController {
	
	@Autowired
	private DocumentHelperV3 documentHelperV3;
	
	
	@RequestMapping(value = "/{expedientId}/proces/{processInstanceId}/document/{documentStoreId}/metadadesNti", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String processInstanceId,
			@PathVariable Long documentStoreId,
			Model model) {
		
		DocumentStoreDto documentStoreDto = documentHelperV3.findDocumentStoreById(documentStoreId);
		
		String tdCodi = documentStoreDto.getNtiTipusDocumental();
		String td = DocumentDto.TipoDocumental.getNameByCodi(tdCodi); 
		if (td != null)
			documentStoreDto.setNtiTipusDocumental(
					getMessage(request, "tipus.documental." + td) + " ("+ tdCodi+")");
		
		model.addAttribute("documentStore", documentStoreDto);
		
		return "v3/documentMetadadesNtiInfo";
	}
	
	
	private static final Log logger = LogFactory.getLog(DocumentMetadadesNtiInfoController.class);
	
}
