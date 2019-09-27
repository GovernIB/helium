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
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioFormsCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxFormResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pestanya de d'integració amb formularis 
 * externs del manteniment dels tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusIntegracioFormsController extends BaseExpedientTipusController {

	@RequestMapping(value = "/{expedientTipusId}/integracioForms")
	public String formext(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"consultes");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			
			ExpedientTipusIntegracioFormsCommand command = new ExpedientTipusIntegracioFormsCommand();			
			command.setId(expedientTipusId);
			if (expedientTipus.getFormextUrl() != null) {
				command.setUrl(expedientTipus.getFormextUrl());
				command.setUsuari(expedientTipus.getFormextUsuari());
				command.setContrasenya(expedientTipus.getFormextContrasenya());
				command.setActiu(true);
			} else {
				command.setActiu(false);
			}			
			model.addAttribute("expedientTipusIntegracioFormsCommand", command);
		}
		
		return "v3/expedientTipusIntegracioForms";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioForms", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse formextPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusIntegracioFormsCommand.Modificacio.class) ExpedientTipusIntegracioFormsCommand command,
			BindingResult bindingResult,
			Model model) throws PermisDenegatException, IOException {
		
		AjaxFormResponse response = AjaxHelper.generarAjaxFormOk();
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		if (entornActual != null) {
			// Comprova que la url estigui informada si està activat
			if (command.isActiu() && (command.getUrl() == null || "".equals(command.getUrl().trim()))) {
				bindingResult.rejectValue("url", "NotEmpty");
			}
	        if (bindingResult.hasErrors()) {
		        MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"expedient.tipus.integracio.forms.validacio"));
	        	response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
	        } else {
	        	if (command.isActiu())
	        		expedientTipusService.updateIntegracioForms(
	        				entornActual.getId(),
	        				expedientTipusId,
	        				command.getUrl(),
	        				command.getUsuari(),
	        				command.getContrasenya());
	        	else
	        		expedientTipusService.updateIntegracioForms(
	        				entornActual.getId(),
	        				expedientTipusId,
	        				null,
	        				null,
	        				null);
	        	
		        MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.integracio.forms.controller.guardat"));
	        }
		}
    	return response;
	}	
}
