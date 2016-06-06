/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTascaReassignarCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
 * Controlador per la reassignació de tasques dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTasquesReassignarController extends BaseExpedientController {

	@Autowired
	private TascaService tascaService;
	
	@Autowired
	private ExpedientService expedientService;

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reassignar", method = RequestMethod.GET)
	public String tascaReassignarGet(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {		
		atributsModel(
    			expedientId,
    			tascaId,
    			model);
		return "v3/expedient/tasca/reassignar";
	}
	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reassignar", method = RequestMethod.POST)
	public String tascaReassignarPost(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@RequestParam(value = "submit", required = false) String submit,
			ExpedientTascaReassignarCommand expedientTascaReassignarCommand,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		if ("submit".equals(submit) || submit.length() == 0) {
			new TascaReassignarValidator().validate(expedientTascaReassignarCommand, result);
	        if (result.hasErrors()) {
	        	atributsModel(
	        			expedientId,
	        			tascaId,
	        			model);
				MissatgesHelper.error(request, result, getMessage(request, "error.validacio"));
	        	return "v3/expedient/tasca/reassignar";
	        }
			try {
				expedientService.reassignarTasca(
						expedientTascaReassignarCommand.getTaskId(),
						expedientTascaReassignarCommand.getExpression());
				MissatgesHelper.success(request, getMessage(request, "info.tasca.reassignada"));
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.reassignar.tasca", new Object[] { expedientTascaReassignarCommand.getTaskId() } ));
				atributsModel(
	        			expedientId,
	        			tascaId,
	        			model);
	        	logger.error("No s'ha pogut reassignar la tasca " + expedientTascaReassignarCommand.getTaskId(), ex);
	        	return "v3/expedient/tasca/reassignar";
			}
		}
		
		return modalUrlTancar(false);
	}
	
	private class TascaReassignarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientTascaReassignarCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "expression", "not.blank");
		}
	}
	
	private void atributsModel(
			Long expedientId,
			String tascaId,
			ModelMap model) {
		ExpedientTascaDto tasca = tascaService.findAmbIdPerExpedient(
				tascaId,
				expedientId);
		
		ExpedientTascaReassignarCommand expedientTascaReassignarCommand = new ExpedientTascaReassignarCommand();
		expedientTascaReassignarCommand.setTaskId(tascaId);
		
		model.addAttribute(expedientTascaReassignarCommand);
		model.addAttribute("expedientIdentificador",tasca.getExpedientIdentificador());
	}

	private static final Log logger = LogFactory.getLog(ExpedientTasquesReassignarController.class);
}
