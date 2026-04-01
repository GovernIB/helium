/**
 * 
 */
package es.caib.helium.back.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.helium.commons.dto.ExpedientDto;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTerminiService;

/**
 * Controlador per a la pipella de timeline de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedient")
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
		model.addAttribute("arbreProcessos", expedientService.getArbreInstanciesProces(expedient.getProcessInstanceId()));
		model.addAttribute("instanciaProces", expedientService.getInstanciaProcesById(expedient.getProcessInstanceId()));
		return "expedient/timeline";
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
		return "expedient/timelineXml";
	}

}
