/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;

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

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientRegistroController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@RequestMapping(value = "/{expedientId}/registre", method = RequestMethod.GET)
	public String registre(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@RequestParam(value = "tipus_retroces", required = false) Integer tipus_retroces,
			Model model) {
		model.addAttribute("id", expedientId);
		model.addAttribute("tipus_retroces", tipus_retroces);		
		ExpedientDto expedient = expedientService.findAmbId(expedientId);			
		model.addAttribute(
				"expedient",
				expedient);
		model.addAttribute(
				"isAdmin",
				potAdministrarExpedient(expedient));
		model.addAttribute(
				"arbreProcessos",
				expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId())));
		List<ExpedientLogDto> logs = null;
		if (tipus_retroces == null || tipus_retroces != 0) {
			logs = expedientService.getLogsPerTascaOrdenatsPerData(expedient);
		} else {
			logs = expedientService.getLogsOrdenatsPerData(expedient);
		}
		if (logs == null || logs.isEmpty()) {
			model.addAttribute(
					"registre",
					expedientService.getRegistrePerExpedient(expedientId));
			return "v3/expedient/registre";
		} else {
			// Llevam els logs retrocedits
			Iterator<ExpedientLogDto> itLogs = logs.iterator();
			while (itLogs.hasNext()) {
				ExpedientLogDto log = itLogs.next();
				if ("RETROCEDIT".equals(log.getEstat()) || "RETROCEDIT_TASQUES".equals(log.getEstat()) || ("EXPEDIENT_MODIFICAR".equals(log.getAccioTipus()) && (tipus_retroces == null || tipus_retroces != 0)))
					itLogs.remove();
			}
			model.addAttribute("logs", logs);
			model.addAttribute(
					"tasques",
					expedientService.getTasquesPerLogExpedient(expedientId));
			return "v3/expedient/log";
		}
	}

	@RequestMapping(value = "retrocedir")
	public String retrocedir(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientId,
			@RequestParam(value = "tipus_retroces", required = false) Integer tipus_retroces,
			@RequestParam(value = "logId", required = true) Long logId,
			@RequestParam(value = "retorn", required = true) String retorn,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			mostrarInformacioExpedientPerPipella(request, expedientId, model, "registre", expedientService);
		}
		
			try {
				expedientService.retrocedirFinsLog(logId, (tipus_retroces == null || tipus_retroces != 0));
			}catch (JbpmException ex ) {
				MissatgesHelper.error(request, getMessage(request, "error.executar.retroces") + ": "+ ex.getCause().getMessage());
				logger.error(" NUMEROEXPEDIENT:"+expedientId+" No s'ha pogut executar el retrocés", ex);
			}
			
			if("t".equals(retorn)){
				return "redirect:/v3/expedient/tasques.html?id=" + expedientId;
			}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "logRetrocedit")
	public String logRetrocedit(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientId,
			@RequestParam(value = "logId", required = true) Long logId,
			Model mod,
			ModelMap model) {
		if (!NodecoHelper.isNodeco(request)) {
			mostrarInformacioExpedientPerPipella(request, expedientId, mod, "registre", expedientService);
		}
		List<ExpedientLogDto> logs = expedientService.findLogsRetroceditsOrdenatsPerData(logId);
		model.addAttribute("logs", logs);
		model.addAttribute(
				"tasques",
				expedientService.getTasquesPerLogExpedient(expedientId));
		return "v3/expedient/logRetrocedit";
	}
	
	@RequestMapping(value = "logAccionsTasca")
	public String logAccionsTasca(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientId,
			@RequestParam(value = "targetId", required = true) Long targetId,
			ModelMap model) {
		List<ExpedientLogDto> logs = expedientService.findLogsTascaOrdenatsPerData(targetId);
		model.addAttribute("logs", logs);
		model.addAttribute(
				"tasques",
				expedientService.getTasquesPerLogExpedient(expedientId));
		return "v3/expedient/logRetrocedit";
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientRegistroController.class);

}
