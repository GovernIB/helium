/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTerminiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pipella de timeline de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTimelineController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientTerminiService expedientTerminiService;



	@RequestMapping(value = "/{expedientId}/timeline", method = RequestMethod.GET)
	public String timeline(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			ModelMap model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		model.addAttribute("expedient", expedient);
		model.addAttribute("arbreProcessos", expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId())));
		model.addAttribute("instanciaProces", expedientService.getInstanciaProcesById(expedient.getProcessInstanceId()));
		return "v3/expedient/timeline";
	}

	@RequestMapping(value = "/{expedientId}/timelineXml", method = RequestMethod.GET)
	public String timelineXml(
			HttpServletRequest request, 
			@PathVariable Long expedientId,
			ModelMap model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		model.addAttribute(
				"instanciaProces",
				expedientService.getInstanciaProcesById(expedient.getProcessInstanceId()));
		model.addAttribute(
				"terminisIniciats",
				expedientTerminiService.iniciatFindAmbProcessInstanceId(
						expedientId,
						expedient.getProcessInstanceId()));
		return "v3/expedient/timelineXml";
	}

}
