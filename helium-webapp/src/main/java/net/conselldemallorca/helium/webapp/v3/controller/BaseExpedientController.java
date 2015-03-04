/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesVersioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

/**
 * Controlador base per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientController extends BaseController {

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected DissenyService dissenyService;

	protected String mostrarInformacioExpedientPerPipella(
			HttpServletRequest request,
			Long expedientId,
			Model model,
			String pipellaActiva) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		model.addAttribute("expedient", expedient);
		model.addAttribute("participants", expedientService.findParticipants(expedientId));
		model.addAttribute("relacionats", expedientService.findRelacionats(expedientId));
		DefinicioProcesVersioDto versions = dissenyService.getByVersionsInstanciaProcesById(expedient.getProcessInstanceId());
		model.addAttribute("definicioProces", versions);
		model.addAttribute("definicioProcesVersio",versions.getVersio());
		if (pipellaActiva != null)
			model.addAttribute("pipellaActiva", pipellaActiva);
		else if (request.getParameter("pipellaActiva") != null)
			model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva"));
		else
			model.addAttribute("pipellaActiva", "tasques");
		return "v3/expedientPipelles";
	}
}
