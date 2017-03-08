/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioSicerCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxFormResponse;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pestanya de d'integració dels tipus d'expedient amb els tràmits de Sistra.
 * També és el controlador pel mapeig de variables, documents i adjunts. Els mapejos es fan
 * sobre una mateixa taula, però en el cas dels adjunts nomès és necessari un camp mentre que per
 * documents i variables és necessària la variable. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusIntegracioSicerController extends BaseExpedientTipusController {
	
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@RequestMapping(value = "/{expedientTipusId}/integracioSicer")
	public String sicer(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientTipusPerPipelles(
					request,
					expedientTipusId,
					model,
					"integracio-sicer");
		}
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			
			ExpedientTipusIntegracioSicerCommand command = new ExpedientTipusIntegracioSicerCommand();			
			
			command.setId(expedientTipusId);
			command.setSicerIntegracioActiva(expedientTipus.isSicerIntegracioActiva());
			command.setSicerProducteCodi(expedientTipus.getSicerProducteCodi());
			command.setSicerClientCodi(expedientTipus.getSicerClientCodi());
			command.setSicerPuntAdmissioCodi(expedientTipus.getSicerPuntAdmissioCodi());
			command.setSicerNomLlinatges(expedientTipus.getSicerNomLlinatges());
			command.setSicerDireccio(expedientTipus.getSicerDireccio());
			command.setSicerPoblacio(expedientTipus.getSicerPoblacio());
			command.setSicerCodiPostal(expedientTipus.getSicerCodiPostal());
			command.setSicerSftpUser(expedientTipus.getSicerSftpUser());
			command.setSicerSftpPassword(expedientTipus.getSicerSftpPassword());
			
			model.addAttribute("expedientTipusIntegracioSicerCommand", command);
		}
		
		return "v3/expedientTipusIntegracioSicer";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioSicer", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse sicerPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusIntegracioSicerCommand.Modificacio.class) ExpedientTipusIntegracioSicerCommand command,
			BindingResult bindingResult,
			Model model) throws PermisDenegatException, IOException {
		
		AjaxFormResponse response = AjaxHelper.generarAjaxFormOk();
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		if (entornActual != null) {
	        if (bindingResult.hasErrors()) {
	        	response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
	        } else {
	        	
	        	ExpedientTipusDto expedientTipus = conversioTipusHelper.convertir(command, ExpedientTipusDto.class);
	        	expedientTipusService.updateIntegracioSicer(
	        			entornActual.getId(),
	        			expedientTipusId,
	        			expedientTipus);
	        	
		        MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.integracio.tramits.controller.guardat"));
	        }
		}
    	return response;
	}	
}
