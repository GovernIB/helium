/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.logic.intf.dto.AlertaDto;
import es.caib.helium.logic.intf.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/alerta")
public class AlertaController extends BaseController {

	@Autowired
	private AlertaService alertaService;

	@RequestMapping(value = "/{alertaId}/llegir", method = RequestMethod.GET)
	public String llegir(HttpServletRequest request, @PathVariable Long alertaId, Model model) {
		AlertaDto alerta = alertaService.marcarLlegida(alertaId);
		
		return "redirect:/modal/v3/expedient/" + alerta.getExpedient().getId() + "/alertes";
	}
	
	@RequestMapping(value = "/{alertaId}/noLlegir", method = RequestMethod.GET)
	public String noLlegir(HttpServletRequest request, @PathVariable Long alertaId, Model model) {
		AlertaDto alerta = alertaService.marcarNoLlegida(alertaId);
		
		return "redirect:/modal/v3/expedient/" + alerta.getExpedient().getId() + "/alertes";
	}
	
	@RequestMapping(value = "/{alertaId}/esborrar", method = RequestMethod.GET)
	public String esborrar(HttpServletRequest request, @PathVariable Long alertaId, Model model) {
		AlertaDto alerta = alertaService.marcarEsborrada(alertaId);
		
		return "redirect:/modal/v3/expedient/" + alerta.getExpedient().getId() + "/alertes";
	}
}
