/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientEinesAturarCommand;
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
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientAturarController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;



	@RequestMapping(value = "/{expedientId}/aturar", method = RequestMethod.GET)
	public String aturarGet(HttpServletRequest request, @PathVariable Long expedientId, Model model) {
		model.addAttribute("expedientId", expedientId);
		ExpedientEinesAturarCommand aturarExpedient = new ExpedientEinesAturarCommand();
		model.addAttribute(aturarExpedient);
		return "v3/expedient/aturar";
	}

	@RequestMapping(value = "/{expedientId}/aturar", method = RequestMethod.POST)
	public String aturarPost(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model, 
			@ModelAttribute("aturarExpedient") 
			ExpedientEinesAturarCommand aturarExpedient, 
			BindingResult result, 
			SessionStatus status) {
			ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!expedient.isAturat()) {
			new ExpedientAturarValidator().validate(aturarExpedient, result);
			if (result.hasErrors()) {
				MissatgesHelper.error(request, getMessage(request, "error.validacio"));
				model.addAttribute("expedientId", expedientId);
				model.addAttribute(aturarExpedient);
				return "v3/expedient/aturar";
			}
			try {
				expedientService.aturar(expedientId, aturarExpedient.getMotiu());
				MissatgesHelper.success(request, getMessage(request, "info.expedient.aturat"));
			} catch (Exception ex) {
				MissatgesHelper.error(request, getMessage(request, "error.aturar.expedient"));
				ex.getLocalizedMessage();
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.expedient.ja.aturat"));
		}
		return modalUrlTancar();
	}

	@RequestMapping(value = "/{expedientId}/reprendre", method = RequestMethod.GET)
	public String reprendre(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		try {
			expedientService.reprendre(expedientId);
			MissatgesHelper.success(request, getMessage(request, "info.expedient.reprendre") );
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.reprendre.expedient"));
			logger.error(getMessage(request, "error.reprendre.expedient"), ex);
		}
		
		return "redirect:/v3/expedient/" + expedientId;
	}

	private class ExpedientAturarValidator implements Validator {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientEinesAturarCommand.class);
		}

		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "motiu", "not.blank");
		}
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientAturarController.class);

}
