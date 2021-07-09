/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.ExpedientTipusIntegracioNotibCommand;
import es.caib.helium.back.helper.AjaxHelper;
import es.caib.helium.back.helper.AjaxHelper.AjaxFormResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.extern.domini.ParellaCodiValor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Controlador per a la pestanya de d'integració dels tipus d'expedient amb els tràmits de Notib.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusIntegracioNotibController extends BaseExpedientTipusController {

	@RequestMapping(value = "/{expedientTipusId}/integracioNotib")
	public String updateGet(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			
			ExpedientTipusIntegracioNotibCommand command = new ExpedientTipusIntegracioNotibCommand();
			
			command.setNotibActiu(expedientTipus.getNotibActiu());
			command.setNotibEmisor(expedientTipus.getNotibEmisor());
			command.setNotibCodiProcediment(expedientTipus.getNotibCodiProcediment());
			// Valors d'inicialització
			if (!Boolean.TRUE.equals(expedientTipus.getNotibActiu()) 
					&& expedientTipus.isNtiActiu()) {
				if (expedientTipus.getNotibEmisor() == null || expedientTipus.getNotibEmisor().isEmpty())
					command.setNotibEmisor(expedientTipus.getNtiOrgano());
				if (expedientTipus.getNotibCodiProcediment() == null || expedientTipus.getNotibCodiProcediment().isEmpty())
				command.setNotibCodiProcediment(expedientTipus.getNtiClasificacion());
			}
			model.addAttribute("expedientTipusIntegracioNotibCommand", command);
		}
		
		return "v3/expedientTipusIntegracioNotib";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioNotib", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse updatePost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusIntegracioNotibCommand.Modificacio.class) ExpedientTipusIntegracioNotibCommand command,
			BindingResult bindingResult,
			Model model) throws PermisDenegatException, IOException {
		
		AjaxFormResponse response = AjaxHelper.generarAjaxFormOk();
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		if (entornActual != null) {
			
	        if (bindingResult.hasErrors()) {
		        MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"expedient.tipus.integracio.notib.validacio"));
	        	response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
	        } else {
	        	expedientTipusService.updateIntegracioNotib(
	        			expedientTipusId,
	        			command.getNotibEmisor(),
	        			command.getNotibCodiProcediment(),
	        			command.getNotibActiu());
	        	
		        MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.integracio.notib.controller.guardat"));
	        }
		}
    	return response;
	}
	
	
	@ModelAttribute("seuIdioma")
	public ParellaCodiValor[] populateSexes(HttpServletRequest request) {
		ParellaCodiValor[] resposta = new ParellaCodiValor[2];
		resposta[0] = new ParellaCodiValor(getMessage(request, "seu.idioma.CA"), "CA");
		resposta[1] = new ParellaCodiValor(getMessage(request, "seu.idioma.ES"), "ES");
		return resposta;
	}

}
