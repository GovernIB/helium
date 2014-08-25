/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEinesScriptCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientExecucionsController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private DissenyService dissenyService;
	
	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = "/{expedientId}/execucions", method = RequestMethod.GET)
	public String execucions(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		model.addAttribute("expedientId", expedientId);
		ExpedientEinesScriptCommand expedientEinesScriptCommand = new ExpedientEinesScriptCommand();
		model.addAttribute(expedientEinesScriptCommand);
		return "v3/expedient/execucions";
	}

	@RequestMapping(value = "/{expedientId}/scriptCommand", method = RequestMethod.POST)
	public String scriptExecucions(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model, 
			ExpedientEinesScriptCommand expedientEinesScriptCommand, 
			BindingResult result, 
			SessionStatus status) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findById(expedientId);
			if (potModificarExpedient(expedient)) {
				new ExpedientScriptValidator().validate(expedientEinesScriptCommand, result);
				if (result.hasErrors()) {
					model.addAttribute("expedientId", expedientId);
					model.addAttribute(expedientEinesScriptCommand);
					return "v3/expedient/execucions";
				}
				try {
					expedientService.evaluateScript(
							expedient.getProcessInstanceId(),
							expedientEinesScriptCommand.getScript(),
							null);
					MissatgesHelper.info(request, getMessage(request, "info.script.executat"));
				} catch (Exception ex) {
					Long entornId = entorn.getId();
					logger.error("ENTORNID:"+entornId+" NUMEROEXPEDIENT:"+expedientId+" No s'ha pogut executar l'script", ex);
					MissatgesHelper.error(request, getMessage(request, "error.executar.script") +": "+ expedientEinesScriptCommand.getScript());
		        }
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}	
		return modalUrlTancar();
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
	
	@RequestMapping(value = "/{expedientId}/accio", method = RequestMethod.GET)
	public String accio(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			@RequestParam(value = "accioId", required = true) Long accioId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			boolean permesa = false;
			AccioDto accio = dissenyService.findAccioAmbId(accioId);
			if (accio.getRols() == null || accio.getRols().length() == 0) {
				permesa = true;
			} else {
				String[] llistaRols = accio.getRols().split(",");
				for (String rol: llistaRols) {
					if (request.isUserInRole(rol)) {
						permesa = true;
						break;
					}
				}
			}
			if (permesa) {
				ExpedientDto expedient = expedientService.findById(expedientId);
				if (accio.isPublica() || potModificarExpedient(expedient)) {
					try {
						dissenyService.executarAccio(accio, expedient);
						MissatgesHelper.info(request, getMessage(request, "info.accio.executat"));
					} catch (JbpmException ex ) {
						MissatgesHelper.error(request, getMessage(request, "error.executar.accio") +" "+ accio.getJbpmAction() + ": "+ ex.getCause().getMessage());
			        	logger.error("ENTORNID:"+expedient.getEntorn().getId()+" NUMEROEXPEDIENT:"+expedient.getId()+" Error al executar la accio", ex);
					}
				} else {
					MissatgesHelper.error(request, getMessage(request, "error.permisos.modificar.expedient"));
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.accio.no.permesa"));
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientExecucionsController.class);
}
