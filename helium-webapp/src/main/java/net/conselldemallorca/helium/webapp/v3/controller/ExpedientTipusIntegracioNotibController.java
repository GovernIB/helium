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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.integracio.plugins.notib.NotibSeuIdioma;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ParellaCodiValor;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioNotibCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxFormResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

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
			
			command.setNotificacioEmisor(expedientTipus.getNtiOrgano());
			command.setNotificacioCodiProcediment(expedientTipus.getNtiClasificacion());
			
			command.setSeuExpedientUnitatOrganitzativa(expedientTipus.getSeuExpedientUnitatOrganitzativa());
			command.setSeuRegistreOficina(expedientTipus.getSeuRegistreOficina());
			command.setSeuRegistreLlibre(expedientTipus.getSeuRegistreLlibre());
			command.setSeuRegistreOrgan(expedientTipus.getSeuRegistreOrgan());
			command.setSeuIdioma(expedientTipus.getSeuIdioma());
			command.setSeuAvisTitol(expedientTipus.getNotificacioAvisTitol());
			command.setSeuAvisText(expedientTipus.getNotificacioAvisText());
			command.setSeuAvisTextMobil(expedientTipus.getNotificacioAvisTextSms());
			command.setSeuOficiTitol(expedientTipus.getNotificacioOficiTitol());
			command.setSeuOficiText(expedientTipus.getNotificacioOficiText());
			command.setParametresNotibActius(expedientTipus.isParametresNotibActius());
			command.setNtiActiu(expedientTipus.isNtiActiu());
			
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
			
			this.validarCommand(command, bindingResult);
			
	        if (bindingResult.hasErrors()) {
	        	response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
	        } else {
	        	expedientTipusService.updateIntegracioNotib(
	        			expedientTipusId,
	        			command.getNotificacioCodiProcediment(),
//	        			command.getSeuExpedientSerieDocumental(),
	        			command.getSeuExpedientUnitatOrganitzativa(),
//	        			command.getSeuExpedientIdentificadorEni(),
//	        			command.getSeuExpedientTitol(),
	        			command.getSeuRegistreOficina(),
	        			command.getSeuRegistreLlibre(),
	        			command.getSeuRegistreOrgan(),
	        			command.getSeuIdioma(),
	        			command.getSeuAvisTitol(),
	        			command.getSeuAvisText(),
	        			command.getSeuAvisTextMobil(),
	        			command.getSeuOficiTitol(),
	        			command.getSeuOficiText(),
	        			command.isParametresNotibActius());
	        	
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
		resposta[0] = new ParellaCodiValor(getMessage(request, "seu.idioma.CA"), NotibSeuIdioma.CA);
		resposta[1] = new ParellaCodiValor(getMessage(request, "seu.idioma.ES"), NotibSeuIdioma.ES);
		return resposta;
	}

	private boolean isNullOrEmpty(String str) {
		return str == null? true : "".equals(str.trim());
	}
	private void validarCommand(
			ExpedientTipusIntegracioNotibCommand command,
			BindingResult bindingResult) {
		
		if (command.isParametresNotibActius()) {
			if (isNullOrEmpty(command.getNotificacioCodiProcediment())) {
				bindingResult.rejectValue("notificacioCodiProcediment", "NotEmpty");
			}
//			if (isNullOrEmpty(command.getSeuExpedientSerieDocumental())) {
//				bindingResult.rejectValue("seuExpedientSerieDocumental", "NotEmpty");
//			}
			if (isNullOrEmpty(command.getSeuExpedientUnitatOrganitzativa())) {
				bindingResult.rejectValue("seuExpedientUnitatOrganitzativa", "NotEmpty");
			}
//			if (isNullOrEmpty(command.getSeuExpedientIdentificadorEni())) {
//				bindingResult.rejectValue("seuExpedientIdentificadorEni", "NotEmpty");
//			}
//			if (isNullOrEmpty(command.getSeuExpedientTitol())) {
//				bindingResult.rejectValue("seuExpedientTitol", "NotEmpty");
//			}
			if (isNullOrEmpty(command.getSeuRegistreOficina())) {
				bindingResult.rejectValue("seuRegistreOficina", "NotEmpty");
			}
			if (isNullOrEmpty(command.getSeuRegistreLlibre())) {
				bindingResult.rejectValue("seuRegistreLlibre", "NotEmpty");
			}
			if (isNullOrEmpty(command.getSeuRegistreOrgan())) {
				bindingResult.rejectValue("seuRegistreOrgan", "NotEmpty");
			}
		}
	}

}
