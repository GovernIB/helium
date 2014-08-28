/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEinesCancelCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ExpedientCancelController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	
	@Autowired
	private PluginService pluginService;

	@RequestMapping(value = "/{expedientId}/cancel", method = RequestMethod.GET)
	public String cancelForm(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		model.addAttribute("expedientId", expedientId);
		ExpedientEinesCancelCommand cancelExpedient = new ExpedientEinesCancelCommand();
		model.addAttribute(cancelExpedient);
		return "v3/expedient/cancel";
	}

	@RequestMapping(value = "/{expedientId}/cancelExpedient", method = RequestMethod.POST)
	public String cancel(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model, 
			@ModelAttribute("cancelExpedient") 
			ExpedientEinesCancelCommand cancelExpedient, 
			BindingResult result, 
			SessionStatus status) {
		ExpedientDto expedient = expedientService.findById(expedientId);			
		if (!expedient.isAnulat()) {
			new ExpedientCancelValidator().validate(cancelExpedient, result);
			if (result.hasErrors()) {
				MissatgesHelper.error(request, getMessage(request, "error.validacio"));
				model.addAttribute("expedientId", expedientId);
				model.addAttribute(cancelExpedient);
				return "v3/expedient/cancel";
			}
			try {
				expedientService.cancel(expedientId, cancelExpedient.getMotiu());
				MissatgesHelper.info(request, getMessage(request, "info.expedient.cancelat") );
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.cancelar.expedient"));
				ex.getLocalizedMessage();
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.expedient.ja.anulat"));
		}
		
		return modalUrlTancar();
	}

	private class ExpedientCancelValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesCancelCommand.class);
		}

		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "motiu", "not.blank");
		}
	}
}
