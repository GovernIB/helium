/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInformacioController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = "/{expedientId}/persona/suggest/{text}", method = RequestMethod.GET)
	@ResponseBody
	public String suggestAction(
			@PathVariable String text,
			ModelMap model) {
		List<PersonaDto> lista = pluginService.findPersonaLikeNomSencer(text);
		String json = "[";
		for (PersonaDto persona: lista) {
			json += "{\"codi\":\"" + persona.getCodi() + "\", \"nom\":\"" + persona.getNomSencer() + "\"},";
		}
		if (json.length() > 1) json = json.substring(0, json.length() - 1);
		json += "]";
		return json;
	}

	@RequestMapping(value = "/{expedientId}/persona/suggestInici/{text}", method = RequestMethod.GET)
	@ResponseBody
	public String suggestIniciAction(
			@PathVariable String text,
			ModelMap model) {
		PersonaDto persona = pluginService.findPersonaAmbCodi(text);
		if (persona != null) {
			return "{\"codi\":\"" + persona.getCodi() + "\", \"nom\":\"" + persona.getNomSencer() + "\"}";
		}
		return null;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
}
