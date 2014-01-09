/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEinesScriptCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientExecucionsController extends BaseExpedientController {

	@Resource(name = "expedientServiceV3")
	private ExpedientService expedientService;
	
	@Resource(name = "pluginServiceV3")
	private PluginService pluginService;
	
	@Resource
	private JbpmHelper jbpmHelper;

	@ModelAttribute("scriptCommand")
	public ExpedientEinesScriptCommand populateScriptCommand() {
		return new ExpedientEinesScriptCommand();
	}

	@RequestMapping(value = "/{expedientId}/execucions", method = RequestMethod.GET)
	public String execucions(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
		model.addAttribute("expedientId", expedientId);
		return "v3/expedient/execucions";
	}

	@RequestMapping(value = "/{expedientId}/scriptCommand", method = RequestMethod.POST)
	public String scriptExecucions(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model, 
			@ModelAttribute("scriptCommand") 
			ExpedientEinesScriptCommand command, 
			BindingResult result, 
			SessionStatus status) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				new ExpedientScriptValidator().validate(command, result);
				if (!result.hasErrors()) {
					try {
						jbpmHelper.evaluateScript(
								expedient.getProcessInstanceId(),
								command.getScript(),
								null);
						MissatgesHelper.info(request, getMessage(request, "info.script.executat"));
					} catch (Exception ex) {
						Long entornId = entorn.getId();
						logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+expedientId+" No s'ha pogut executar l'script", ex);
						MissatgesHelper.error(request, getMessage(request, "error.executar.script") +": "+ command.getScript());
			        }
					return "/v3/utils/modalTancar";
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}	
		return "/v3/utils/modalTancar";
	}
	
	private class ExpedientScriptValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesScriptCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "script", "not.blank");
		}
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientExecucionsController.class);
}
