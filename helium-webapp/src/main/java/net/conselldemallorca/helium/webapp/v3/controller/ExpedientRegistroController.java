/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

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

	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = "/{expedientId}/registre", method = RequestMethod.GET)
	public String registre(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			@RequestParam(value = "tipus_retroces", required = false) Integer tipus_retroces,
			Model model) {
		model.addAttribute("id", expedientId);
		model.addAttribute("tipus_retroces", tipus_retroces);
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
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
							expedientService.getRegistrePerExpedient(expedient.getId()));
					return "v3/expedient/registre";
				} else {
					// Llevam els logs retrocedits
					Iterator<ExpedientLogDto> itLogs = logs.iterator();
					while (itLogs.hasNext()) {
						ExpedientLogDto log = itLogs.next();
						if ("RETROCEDIT".equals(log.getEstat()) ||
								"RETROCEDIT_TASQUES".equals(log.getEstat()))
							itLogs.remove();
					}
					model.addAttribute("logs", logs);
					model.addAttribute(
							"tasques",
							expedientService.getTasquesPerLogExpedient(expedient.getId()));
					return "v3/expedient/log";
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.consultar.expedient"));				
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "retrocedir")
	public String retrocedir(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientId,
			@RequestParam(value = "tipus_retroces", required = false) Integer tipus_retroces,
			@RequestParam(value = "logId", required = true) Long logId,
			@RequestParam(value = "retorn", required = true) String retorn,
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
			mostrarInformacioExpedientPerPipella(request, expedientId, model, "registre", expedientService);
		}
		if (entorn != null) {
			try {
				if (tipus_retroces == null || tipus_retroces != 0) {
					expedientService.retrocedirFinsLog(logId, true);
				} else {
					expedientService.retrocedirFinsLog(logId, false);
				}
			}catch (JbpmException ex ) {
				Long entornId = entorn.getId();
				MissatgesHelper.error(request, getMessage(request, "error.executar.retroces") + ": "+ ex.getCause().getMessage());
				logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+expedientId+" No s'ha pogut executar el retrocés", ex);
			}
			
			if("t".equals(retorn)){
				return "redirect:/v3/expedient/tasques.html?id=" + expedientId;
			}			
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
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
		if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
			mostrarInformacioExpedientPerPipella(request, expedientId, mod, "registre", expedientService);
		}
		NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(expedient.getProcessInstanceId(), false, false, false));
				List<ExpedientLogDto> logs = expedientService.findLogsRetroceditsOrdenatsPerData(logId);
				model.addAttribute("logs", logs);
				model.addAttribute(
						"tasques",
						expedientService.getTasquesPerLogExpedient(expedient.getId()));
				return "v3/expedient/logRetrocedit";
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.consultar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "logAccionsTasca")
	public String logAccionsTasca(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long expedientId,
			@RequestParam(value = "targetId", required = true) Long targetId,
			ModelMap model) {
		NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(expedient.getProcessInstanceId(), false, false, false));
				List<ExpedientLogDto> logs = expedientService.findLogsTascaOrdenatsPerData(targetId);
				model.addAttribute("logs", logs);
				model.addAttribute(
						"tasques",
						expedientService.getTasquesPerLogExpedient(expedient.getId()));
				return "v3/expedient/logRetrocedit";
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.consultar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientRegistroController.class);

}
