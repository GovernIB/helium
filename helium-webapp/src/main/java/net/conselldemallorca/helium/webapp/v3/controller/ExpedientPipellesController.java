/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;

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
public class ExpedientPipellesController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String info(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		return mostrarInformacioExpedientPerPipella(request, expedientId, model, null, expedientService);
	}
}
