/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ExpedientDadesController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@RequestMapping(value = "/{expedientId}/dades", method = RequestMethod.GET)
	public String dades(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"dades",
					expedientService);
		}
		model.addAttribute("expedientId", expedientId);
		model.addAttribute(
				"dades",
				expedientService.findDadesPerExpedient(
						expedientId));
		model.addAttribute(
				"agrupacions",
				expedientService.findAgrupacionsDadesExpedient(
						expedientId));
		return "v3/expedientDades";
	}

}
