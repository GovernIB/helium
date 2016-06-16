/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEinesCancelCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * Controlador per l'anulació d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientAnularController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@RequestMapping(value = "/{expedientId}/anular", method = RequestMethod.GET)
	public String anularGet(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		model.addAttribute("expedientId", expedientId);
		ExpedientEinesCancelCommand cancelExpedient = new ExpedientEinesCancelCommand();
		model.addAttribute(cancelExpedient);
		return "v3/expedient/anular";
	}	

	@RequestMapping(value = "/{expedientId}/anular", method = RequestMethod.POST)
	public String anularPost(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model, 
			@ModelAttribute("cancelExpedient") 
			ExpedientEinesCancelCommand cancelExpedient, 
			BindingResult result, 
			SessionStatus status) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);			
		if (!expedient.isAnulat()) {
			new ExpedientAnularValidator().validate(cancelExpedient, result);
			if (result.hasErrors()) {
				MissatgesHelper.error(request, getMessage(request, "error.validacio"));
				model.addAttribute("expedientId", expedientId);
				model.addAttribute(cancelExpedient);
				return "v3/expedient/anular";
			}
			try {
				expedientService.anular(expedientId, cancelExpedient.getMotiu());
				MissatgesHelper.success(request, getMessage(request, "info.expedient.anulat") );
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.cancelar.expedient"));
				logger.error(getMessage(request, "error.cancelar.expedient"), ex);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.expedient.ja.anulat"));
		}
		
		return modalUrlTancar();
	}

	@RequestMapping(value = "/{expedientId}/activar", method = RequestMethod.GET)
	public String reprendre(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		try {
			expedientService.desanular(expedientId);
			MissatgesHelper.success(request, getMessage(request, "info.expedient.reactivat") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.activar.expedient"));
			logger.error(getMessage(request, "error.activar.expedient"), ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}



	private class ExpedientAnularValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesCancelCommand.class);
		}

		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "motiu", "not.blank");
		}
	}

	private static final Log logger = LogFactory.getLog(ExpedientAnularController.class);

}
