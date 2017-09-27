/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.model.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusMetadadesNtiCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxFormResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pestanya de d'integració amb metadades
 * nti del manteniment dels tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientMetadadesNtiController extends BaseExpedientTipusController {

	@RequestMapping(value = "/{expedientTipusId}/metadadesNti")
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
			
			ExpedientTipusMetadadesNtiCommand command = ExpedientTipusMetadadesNtiCommand.toCommand(
					expedientTipus);
							
			model.addAttribute("expedientTipusMetadadesNtiCommand", command);
			omplirTipusFirma(request, model);
		}
		
		return "v3/expedientTipusMetadadesNti";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/metadadesNti", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse formextPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusMetadadesNtiCommand.Modificacio.class) ExpedientTipusMetadadesNtiCommand command,
			BindingResult bindingResult) throws PermisDenegatException, IOException {
		
		AjaxFormResponse response = AjaxHelper.generarAjaxFormOk();
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		if (entornActual != null) {
			// Comprova que la url estigui informada si està activat
			if (command.isNtiActiu() && (command.getOrganisme() == null || "".equals(command.getOrganisme().trim()))) {
				bindingResult.rejectValue("organisme", "NotEmpty");
			}
			if (command.isNtiActiu() && (command.getClassificacio() == null || "".equals(command.getClassificacio().trim()))) {
				bindingResult.rejectValue("classificacio", "NotEmpty");
			}
	        if (bindingResult.hasErrors()) {
	        	response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
	        } else {
        		expedientTipusService.updateMetadadesNti(
        				expedientTipusId,
        				command.isNtiActiu(),
        				command.getOrganisme(), 
        				command.getClassificacio(),
        				command.getNtiTipoFirma(),
        				("CSV".equals(command.getNtiTipoFirma())) ? command.getNtiValorCsv() : null,
        				("CSV".equals(command.getNtiTipoFirma())) ? command.getNtiDefGenCsv() : null);
	        	
		        MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.tipus.metadades.nti.controller.guardat"));
	        }
		}
    	return response;
	}
	
	private void omplirTipusFirma(
			HttpServletRequest request,
			Model model) {
		List<ParellaCodiValorDto> tdlist = new ArrayList<ParellaCodiValorDto>();
		for(ExpedientTipusDto.TipoFirma tf : ExpedientTipusDto.TipoFirma.values())
			tdlist.add(new ParellaCodiValorDto(
					tf.toString(),
					getMessage(request, "tipo.firma." + tf)));
		
		model.addAttribute("ntiTipoFirma", tdlist);
	}
	
}