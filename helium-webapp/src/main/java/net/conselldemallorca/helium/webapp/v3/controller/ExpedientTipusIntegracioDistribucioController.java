/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.unitat.UnitatOrganica;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.webapp.mvc.util.ReglesRestClient;
import net.conselldemallorca.helium.webapp.mvc.util.ReglesRestClient.AddResponse;
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

	@Autowired
	private PluginHelper pluginHelper;

	
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
	
	/** Mètode per crear regla de Distribució **/
	@RequestMapping(value = "/{expedientTipusId}/integracioDistribucio/addRule")
	@ResponseBody
	public AjaxFormResponse addRule(

			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(value = "codiProcediment", required = true) String codiProcediment,
			Model model) throws PermisDenegatException, IOException {
		
		String url = GlobalProperties.getInstance().getProperty("app.helium.distribucio.regles.api.rest.url");
		String usuari = GlobalProperties.getInstance().getProperty("app.helium.distribucio.regles.api.rest.usuari");
		String contrasenya = GlobalProperties.getInstance().getProperty("app.helium.distribucio.regles.api.rest.password");
	
		ReglesRestClient client = new ReglesRestClient(
				url,
				usuari,
				contrasenya,
				false);		
		AjaxFormResponse ajaxResponse = AjaxHelper.generarAjaxFormOk();
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		
		if (entornActual != null) {
			ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
					entornActual.getId(),
					expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			ajaxResponse = AjaxHelper.generarAjaxFormOk();		
			try {
				String codiEntitat = expedientTipus.getNtiOrgano();
				if(codiEntitat==null || "".equals(codiEntitat)) {
					MissatgesHelper.error(
							request,
							getMessage(
									request, 
									"expedient.tipus.integracio.distribucio.regla.unitatOrganica.error"));
					logger.error("expedient.tipus.integracio.distribucio.regla.unitatOrganica.error");

				}
				UnitatOrganica unitatOrganica = pluginHelper.findUnitatOrganica(codiEntitat);	
	
				String backoffice = GlobalProperties.getInstance().getProperty("app.helium.distribucio.regles.api.rest.codi.backoffice");
				String sia = codiProcediment;
				AddResponse response = client.add(unitatOrganica.getCodiUnitatArrel(), sia, backoffice);
		

				logger.debug("Resposta de la creació de la regla " + response.getMissatge());

				if (response.isCorrecte()) {
					if(response.getMissatge()!=null && !response.getMissatge().contains("Ja existeix"))
						MissatgesHelper.success(
									request, 
									response.getMissatge());
					else
						if(response.getMissatge()!=null && response.getMissatge().contains("Ja existeix"))
							MissatgesHelper.warning(
									request, 
									response.getMissatge());
				} else  {
					MissatgesHelper.error(
							request,
							getMessage(
									request, 
									"expedient.tipus.integracio.distribucio.regla.error")
							+ ": " + response.getMissatge());
					logger.error("Error retornat al crear regla en distribucio: " + response.getMissatge());

				}
			} catch (Exception e) {
				  MissatgesHelper.error(
							request, 
							getMessage(
									request, 
									"expedient.tipus.integracio.distribucio.validacio"));
				logger.error("Error creant la regla: " + e.getMessage(), e);
			}
				
		}
    	return ajaxResponse;
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusIntegracioDistribucioController.class);
}
