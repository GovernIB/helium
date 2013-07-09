/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientDocumentsController extends BaseExpedientController {

	@Resource(name="expedientServiceV3")
	private ExpedientService expedientService;

	@RequestMapping(value = "/{expedientId}/documents", method = RequestMethod.GET)
	public String documents(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"documents",
					expedientService);
		}
		model.addAttribute("expedientId", expedientId);
		model.addAttribute(
				"documents",
				expedientService.findDocumentsPerExpedient(
						expedientId));
		model.addAttribute(
				"agrupacions",
				expedientService.findAgrupacionsDadesExpedient(
						expedientId));
		return "v3/expedientDocuments";
	}

	@RequestMapping(value = "/{expedientId}/document/{documentStoreId}/descarregar", method = RequestMethod.GET)
	public String documentDescarregar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long documentStoreId,
			Model model) {
		ArxiuDto arxiu = null;
		try {
			arxiu = expedientService.getArxiuExpedient(
					expedientId,
					documentStoreId);
		} catch (Exception ex) {
			logger.error("Error al descarregar el document", ex);
			MissatgesHelper.error(
					request,
					"Error al descarregar el document: " + ex.getMessage());
			return "redirect:/v3/expedient/" + expedientId + "/documents";
		}
		if (arxiu != null) {
			model.addAttribute(
					ArxiuView.MODEL_ATTRIBUTE_FILENAME,
					arxiu.getNom());
			model.addAttribute(
					ArxiuView.MODEL_ATTRIBUTE_DATA,
					arxiu.getContingut());
			return "arxiuView";
		} else {
			MissatgesHelper.error(
					request,
					"No s'ha trobat el document.");
			return "redirect:/v3/expedient/" + expedientId + "/documents";
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientDocumentsController.class);

}
