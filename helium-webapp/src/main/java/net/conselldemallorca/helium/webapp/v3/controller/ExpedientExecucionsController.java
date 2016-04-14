/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEinesScriptCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
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

	@Autowired
	private ExpedientService expedientService;

	@RequestMapping(value = "/{expedientId}/execucions", method = RequestMethod.GET)
	public String execucions(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		model.addAttribute("expedientId", expedientId);
		ExpedientEinesScriptCommand expedientEinesScriptCommand = new ExpedientEinesScriptCommand();
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		model.addAttribute("processos", arbreProcessos);
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
		new ExpedientScriptValidator().validate(expedientEinesScriptCommand, result);
		if (result.hasErrors()) {
			ExpedientDto expedient = expedientService.findAmbId(expedientId);
			List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
			model.addAttribute("processos", arbreProcessos);
			model.addAttribute("expedientId", expedientId);
			model.addAttribute(expedientEinesScriptCommand);
			return "v3/expedient/execucions";
		}
		try {
//			if (expedient.isPermisAdministration()) {
			expedientService.evaluateScript(
					expedientId,
					expedientEinesScriptCommand.getScript(),
					expedientEinesScriptCommand.getScriptProcessId());
			MissatgesHelper.success(request, getMessage(request, "info.script.executat"));
//			} else {
		} catch (NotAllowedException ex) {
			logger.error("ENTORNID:"+entorn.getId()+" NUMEROEXPEDIENT:"+expedientId+" No disposa de permisos per a executar scripts");
			MissatgesHelper.error(request, getMessage(request, "error.executar.script.permis.no"));
			if (!ModalHelper.isModal(request))
				return "redirect:/v3/expedient/" + expedientId;
		} catch (Exception ex) {
//			Long entornId = entorn.getId();
			logger.error("ENTORNID:"+entorn.getId()+" NUMEROEXPEDIENT:"+expedientId+" No s'ha pogut executar l'script", ex);
			MissatgesHelper.error(request, getMessage(request, "error.executar.script") +": "+ expedientEinesScriptCommand.getScript());
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
	
	private static final Log logger = LogFactory.getLog(ExpedientExecucionsController.class);
}
