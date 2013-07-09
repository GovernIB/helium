/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;

import org.springframework.ui.Model;

/**
 * Controlador per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientController {

	protected String mostrarInformacioExpedientPerPipella(
			HttpServletRequest request,
			Long expedientId,
			Model model,
			String pipellaActiva,
			ExpedientService expedientService) {
		ExpedientDto expedient = expedientService.findById(expedientId);
		model.addAttribute("expedient", expedient);
		List<PersonaDto> participants = expedientService.findParticipantsPerExpedient(
				expedientId);
		model.addAttribute("participants", participants);
		if (pipellaActiva != null)
			model.addAttribute("pipellaActiva", pipellaActiva);
		else
			model.addAttribute("pipellaActiva", "tasques");
		return "v3/expedientPipelles";
	}

}
