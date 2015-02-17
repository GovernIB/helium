/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

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
		ExpedientDto expedient = expedientService.findAmbId(expedientId);		

		if (expedient.isPermisAdministration()  || expedient.isPermisSupervision()) {
			boolean detall = tipus_retroces != null && tipus_retroces == 0;
			Map<InstanciaProcesDto, List<ExpedientLogDto>> loggers  = expedientService.getLogsOrdenatsPerData(expedient, detall);
			
			SortedSet<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>> sortedEntries = new TreeSet<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>>(new Comparator<Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>>>() {
				@Override
				public int compare(Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>> e1, Map.Entry<InstanciaProcesDto, List<ExpedientLogDto>> e2) {
					if (e1.getKey() == null || e2.getKey() == null)
						return 0;
					int res = e1.getKey().getId().compareTo(e2.getKey().getId());
					if (e1.getKey().getId().equals(e2.getKey().getId())) {
						return res;
					} else {
						return res != 0 ? res : 1;
					}
				}
			});
			sortedEntries.addAll(loggers.entrySet());

			model.addAttribute("tasques", expedientService.getTasquesPerLogExpedient(expedientId));
			model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
			model.addAttribute("tipus_retroces", tipus_retroces);
			model.addAttribute("expedient", expedient);
			model.addAttribute("logs", sortedEntries);
		}		
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
			expedientService.retrocedirFinsLog(logId, tipus_retroces == null || tipus_retroces != 0);
			MissatgesHelper.info(request, getMessage(request, "expedient.registre.correcte"));
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
		model.addAttribute("logs", expedientService.findLogsRetroceditsOrdenatsPerData(logId));
		model.addAttribute("tasques", expedientService.getTasquesPerLogExpedient(expedientId));
		return "v3/expedient/logRetrocedit";
	}
	
	@RequestMapping(value = "logAccionsTasca")
	public String logAccionsTasca(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientId,
			@RequestParam(value = "targetId", required = true) Long targetId,
			ModelMap model) {
		model.addAttribute("logs", expedientService.findLogsTascaOrdenatsPerData(targetId));
		model.addAttribute("tasques", expedientService.getTasquesPerLogExpedient(expedientId));
		return "v3/expedient/logRetrocedit";
	}
	
	@RequestMapping(value = "scriptForm/{logId}")
	public String logScript(
			HttpServletRequest request,
			@PathVariable Long logId, 
			ModelMap model) {
		model.addAttribute("log", expedientService.findLogById(logId));
		return "v3/expedient/logScript";
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientRegistroController.class);

}
