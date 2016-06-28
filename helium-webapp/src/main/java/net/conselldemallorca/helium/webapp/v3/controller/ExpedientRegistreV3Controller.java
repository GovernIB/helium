/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.jbpm.JbpmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientRegistreService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientRegistreV3Controller extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientRegistreService expedientRegistreService;



	@RequestMapping(value = "/{expedientId}/registre", method = RequestMethod.GET)
	public String registre(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@RequestParam(value = "tipus_retroces", required = false) Integer tipus_retroces,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);		
		boolean detall = tipus_retroces != null && tipus_retroces == 0;
		model.addAttribute(
				"tasques",
				expedientRegistreService.registreFindTasquesPerLogExpedient(
						expedientId));
		model.addAttribute(
				"inicialProcesInstanceId",
				expedient.getProcessInstanceId());
		model.addAttribute(
				"tipus_retroces",
				tipus_retroces);
		model.addAttribute(
				"expedient",
				expedient);
		model.addAttribute(
				"logs",
				expedientRegistreService.registreFindLogsOrdenatsPerData(
						expedient.getId(),
						detall));
		return "v3/expedientLog";
	}

	@RequestMapping(value = "retrocedir")
	@ResponseBody
	public boolean retrocedir(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientId,
			@RequestParam(value = "tipus_retroces", required = false) Integer tipus_retroces,
			@RequestParam(value = "logId", required = true) Long logId,
			@RequestParam(value = "retorn", required = true) String retorn,
			Model model) {
		boolean response = false;
		try {
			expedientRegistreService.registreRetrocedir(
					expedientId,
					logId,
					tipus_retroces == null || tipus_retroces != 0);
			MissatgesHelper.success(request, getMessage(request, "expedient.registre.correcte"));
			response = true;
		} catch (JbpmException ex ) {
			MissatgesHelper.error(request, getMessage(request, "error.executar.retroces") + ": "+ ex.getCause().getMessage());
			logger.error(" NUMEROEXPEDIENT:"+expedientId+" No s'ha pogut executar el retrocés", ex);
		}
		return response;
	}

	@RequestMapping(value = "logRetrocedit")
	public String logRetrocedit(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientId,
			@RequestParam(value = "logId", required = true) Long logId,
			Model mod,
			ModelMap model) {
		model.addAttribute(
				"logs",
				expedientRegistreService.registreFindLogsRetroceditsOrdenatsPerData(
						expedientId,
						logId));
		model.addAttribute(
				"tasques",
				expedientRegistreService.registreFindTasquesPerLogExpedient(
						expedientId));
		return "v3/expedient/logRetrocedit";
	}

	@RequestMapping(value = "logAccionsTasca")
	public String logAccionsTasca(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientId,
			@RequestParam(value = "targetId", required = true) Long targetId,
			ModelMap model) {
		model.addAttribute(
				"logs",
				expedientRegistreService.registreFindLogsTascaOrdenatsPerData(
						expedientId,
						targetId));
		model.addAttribute(
				"tasques",
				expedientRegistreService.registreFindTasquesPerLogExpedient(
						expedientId));
		return "v3/expedient/logRetrocedit";
	}

	@RequestMapping(value = "scriptForm/{logId}")
	public String logScript(
			HttpServletRequest request,
			@PathVariable Long logId, 
			ModelMap model) {
		model.addAttribute(
				"log",
				expedientRegistreService.registreFindLogById(
						logId));
		return "v3/expedient/logScript";
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientRegistreV3Controller.class);

}
