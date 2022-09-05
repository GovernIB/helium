/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioPinbalCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxFormResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pestanya de d'integració amb Pinbal
 * del manteniment dels tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusIntegracioPinbalController extends BaseExpedientTipusController {

	@RequestMapping(value = "/{expedientTipusId}/integracioPinbal")
	public String pinbal(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"integracioPinbal");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			ExpedientTipusIntegracioPinbalCommand command = ExpedientTipusIntegracioPinbalCommand.toCommand(
					expedientTipus);
			command.setPinbalActiu(expedientTipus.isPinbalActiu());
			command.setPinbalNifCif(expedientTipus.getPinbalNifCif());
			model.addAttribute("expedientTipusIntegracioPinbalCommand", command);
		}
		return "v3/expedientTipusIntegracioPinbal";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioPinbal", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse ntiPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusIntegracioPinbalCommand.Modificacio.class) ExpedientTipusIntegracioPinbalCommand command,
			BindingResult bindingResult) throws PermisDenegatException, IOException {
		AjaxFormResponse response = AjaxHelper.generarAjaxFormOk();
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			if (command.isPinbalActiu() && (command.getPinbalNifCif() == null || "".equals(command.getPinbalNifCif().trim()))) {
				bindingResult.rejectValue("pinbalNnifCif", "NotEmpty");
			}
			if (bindingResult.hasErrors()) {
		        MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"expedient.tipus.integracio.pinbal.validacio"));
				response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			} else {
				expedientTipusService.updateIntegracioPinbal(
						entornActual.getId(),
						expedientTipusId,
						command.isPinbalActiu(), 
						command.getPinbalNifCif());
				
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.integracio.pinbal.controller.guardat"));
			}
		}
    	return response;
	}

}
