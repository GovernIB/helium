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

import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioDistribucioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxFormResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pestanya de d'integració dels tipus d'expedient com a backoffice de Distribucio.
 * Helium incorpora un WS per rebre peticions d'anotacions de registre quan actua com a backoffice de distribució
 * i des d'aquesta pàgina es poden configurar els tipus d'expedient per acceptar automàticament anotacions
 * segons el codi de procediment associat a l'anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedientTipus")
public class ExpedientTipusIntegracioDistribucioController extends BaseExpedientTipusController {

	@RequestMapping(value = "/{expedientTipusId}/integracioDistribucio")
	public String distribucio(
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
			
			
			ExpedientTipusIntegracioDistribucioCommand command = new ExpedientTipusIntegracioDistribucioCommand();			
			command.setId(expedientTipusId);
			command.setActiu(expedientTipus.isDistribucioActiu());
			command.setCodiProcediment(expedientTipus.getDistribucioCodiProcediment());
			command.setCodiAssumpte(expedientTipus.getDistribucioCodiAssumpte());
			command.setProcesAuto(expedientTipus.isDistribucioProcesAuto());
			command.setSistra(expedientTipus.isDistribucioSistra());
			
			model.addAttribute("expedientTipusIntegracioDistribucioCommand", command);
		}
		
		return "v3/expedientTipusIntegracioDistribucio";
	}
	
	@RequestMapping(value = "/{expedientTipusId}/integracioDistribucio", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse distribubioPost(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@Validated(ExpedientTipusIntegracioDistribucioCommand.Modificacio.class) ExpedientTipusIntegracioDistribucioCommand command,
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
								"expedient.tipus.integracio.distribucio.validacio"));
	        	response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
	        } else {
        		expedientTipusService.updateIntegracioDistribucio(
	        				entornActual.getId(),
	        				expedientTipusId,
	        				command.isActiu(),
	        				command.getCodiProcediment(),
	        				command.getCodiAssumpte(),
	        				command.isProcesAuto(),
	        				command.isSistra());
		        MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"expedient.tipus.integracio.distribucio.controller.guardat"));
	        }
		}
    	return response;
	}	
}
