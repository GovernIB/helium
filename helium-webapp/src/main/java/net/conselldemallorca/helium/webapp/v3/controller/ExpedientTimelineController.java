/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.api.service.TerminiService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per iniciar un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTimelineController extends BaseExpedientController {

	public static final String CLAU_SESSIO_TASKID = "iniciexp_taskId";
	public static final String CLAU_SESSIO_TITOL = "iniciexp_titol";
	public static final String CLAU_SESSIO_NUMERO = "iniciexp_numero";
	public static final String CLAU_SESSIO_ANY = "iniciexp_any";
	public static final String CLAU_SESSIO_FORM_VALIDAT = "iniciexp_form_validat";
	public static final String CLAU_SESSIO_FORM_COMMAND = "iniciexp_form_command";
	public static final String CLAU_SESSIO_FORM_VALORS = "iniciexp_form_registres";
	public static final String CLAU_SESSIO_PREFIX_REGISTRE = "ExpedientIniciarController_reg_";

	@Autowired
	private DissenyService dissenyService;

	@Autowired
	private ExpedientService expedientService;

	@Autowired
	private TerminiService terminiService;
	
	@Autowired
	private TascaService tascaService;

	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = "/{expedientId}/timeline", method = RequestMethod.GET)
	public String timeline(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"expedient",
						expedient);
				model.addAttribute(
						"arbreProcessos",
						expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId())));
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(expedient.getProcessInstanceId(), false, false, false));
				return "v3/expedient/timeline";
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.consultar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
		}
		
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/timelineXml", method = RequestMethod.GET)
	public String timelineXml(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potConsultarExpedient(expedient)) {
				model.addAttribute(
						"instanciaProces",
						expedientService.getInstanciaProcesById(expedient.getProcessInstanceId(), false, false, false));
				model.addAttribute(
						"terminisIniciats",
						terminiService.findIniciatsAmbProcessInstanceId(expedient.getProcessInstanceId()));
				return "v3/expedient/timelineXml";
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.consultar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
}
