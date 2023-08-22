/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.rest.client.regla.ReglesRestClient;
import es.caib.distribucio.rest.client.regla.domini.Regla;
import es.caib.distribucio.rest.client.regla.domini.ReglaResponse;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.unitat.UnitatOrganica;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusIntegracioDistribucioCommand;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxFormResponse;
import net.conselldemallorca.helium.webapp.v3.helper.EnumHelper.HtmlOption;
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
			command.setPresencial(expedientTipus.getDistribucioPresencial());
			
			model.addAttribute("expedientTipusIntegracioDistribucioCommand", command);
			model.addAttribute("sino", getSiNo());
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
	        	for(ObjectError e: bindingResult.getAllErrors()) {
	        		 MissatgesHelper.error(
	 						request, 
	 						getMessage(
		    						request, 
		    						e.getDefaultMessage()));
	        	}
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
	        				command.isSistra(),
							command.getPresencial());
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
	@RequestMapping(value = "/{expedientTipusId}/integracioDistribucio/crearRegla")
	@ResponseBody
	public AjaxFormResponse crearRegla(

			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(value = "codiProcediment", required = false) String codiProcediment,
			@RequestParam(value = "presencial", required = false) Boolean presencial) {
			
		AjaxFormResponse ajaxResponse = AjaxHelper.generarAjaxFormOk();
		
		try {
			
			String codiEntitat = this.comprovarDades(request, expedientTipusId, codiProcediment);
			String backoffice = this.getBackoffice();
			ReglesRestClient client = this.getReglesRestClient();

			// Invoca la creació 
			ReglaResponse response = client.add(
					codiEntitat, 
					codiProcediment, 
					backoffice,
					presencial);
	

			logger.debug("Resposta de la creació de la regla " + (response.isCorrecte() ? "OK" : "KO") + " " + response.getMsg());

			String msg = getMessage(
					request,
					"expedient.tipus.integracio.distribucio.regla.crearActualitar.success",
					new Object[] {codiProcediment, response.getMsg()});

			if (response.isCorrecte()) {
				if(response.getMsg() !=null && !response.getMsg().contains("Ja existeix"))
					MissatgesHelper.success(
								request, 
								msg);
				else
					if(response.getMsg()!=null && response.getMsg().contains("Ja existeix"))
						MissatgesHelper.warning(
								request, 
								msg);
			} else  {
				MissatgesHelper.error(
						request,
						getMessage(
								request, 
								"expedient.tipus.integracio.distribucio.regla.crearActualitzar.error",
								new Object[] {response.getMsg()}));
				logger.error("Error retornat al crear regla en distribucio: " + response.getMsg());
			}
			
		} catch(Exception e) {
			String errMsg = getMessage(
					request, 
					"expedient.tipus.integracio.distribucio.regla.crearActualitzar.error",
					new Object[] {e.getMessage()});
			logger.error(errMsg, e);			
			MissatgesHelper.error(request, errMsg);
		}
    	return ajaxResponse;
	}
		
	/** Mètode per consultar la regla a Distribució i mostrar el resultat en una modal. **/
	@RequestMapping(value = "/{expedientTipusId}/integracioDistribucio/consultarRegla")
	public String consultarRegla(
			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(value = "codiProcediment", required = false) String codiProcediment,
			Model model) {
		
		model.addAttribute("expedientTipusId", expedientTipusId);
		model.addAttribute("codiProcediment", codiProcediment);
		try {
			
			this.comprovarDades(request, expedientTipusId, codiProcediment);
			ReglesRestClient client = this.getReglesRestClient();

			// Invoca la consulta de la regla
			Regla regla = client.consultarRegla(codiProcediment);			
			model.addAttribute("regla", regla);
			
			if (!regla.isActiva()) {
				MissatgesHelper.warning(request, 
						"La regla no es troba activa a Distribucio.");
			}			
		} catch(Exception e) {
			String errMsg = getMessage(
					request, 
					"expedient.tipus.integracio.distribucio.regla.consulta.error",
					new Object[] {e.getMessage()});
			logger.error(errMsg, e);			
			MissatgesHelper.error(request, errMsg);
		}
		return "v3/expedientTipusIntegracioDistribucioConsultaRegla";
	}
	
	/** Mètode per consultar la regla a Distribució **/
	@RequestMapping(value = "/{expedientTipusId}/integracioDistribucio/canviEstatRegla")
	@ResponseBody
	public AjaxFormResponse canviEstatRegla(

			HttpServletRequest request,
			@PathVariable Long expedientTipusId,
			@RequestParam(value = "activa", required = false) Boolean activa,
			@RequestParam(value = "codiProcediment", required = false) String codiProcediment) {
			
		AjaxFormResponse ajaxResponse = AjaxHelper.generarAjaxFormOk();
		
		try {
			
			this.comprovarDades(request, expedientTipusId, codiProcediment);
			ReglesRestClient client = this.getReglesRestClient();

			// Invoca la consulta de la regla
			ReglaResponse response = client.canviEstat(codiProcediment, activa);
			
			if (response.isCorrecte()) {
				MissatgesHelper.success(
							request, 
							getMessage(
									request,
									"expedient.tipus.integracio.distribucio.regla.canviEstat.success",
									new Object[] {codiProcediment, response.getMsg()}));
			} else  {
				MissatgesHelper.error(
						request,
						getMessage(
								request, 
								"expedient.tipus.integracio.distribucio.regla.canviEstat.error",
								new Object[] {codiProcediment, response.getMsg()}));
				logger.error("Error retornat en canviar l'estat a la regla a Distribucio pel codi de procediment " + codiProcediment + ": " + response.getMsg());
			}

		} catch(Exception e) {
			String errMsg = getMessage(
					request, 
					"expedient.tipus.integracio.distribucio.regla.canviEstat.error",
					new Object[] {e.getMessage()});
			logger.error(errMsg, e);			
			MissatgesHelper.error(request, errMsg);
		}
    	return ajaxResponse;
	}
	

	private String comprovarDades (
			HttpServletRequest request, 
			Long expedientTipusId, 
			String codiProcediment) throws Exception {
		
		if (codiProcediment == null || "".equals(codiProcediment)) {
			throw new Exception("S'ha d'informar el codi de procediment per donar d'alta la regla");
		}

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null) {
			throw new Exception ("No hi ha entorn actual seleccionat.");
		}
		ExpedientTipusDto expedientTipus = expedientTipusService.findAmbIdPermisDissenyar(
				entornActual.getId(),
				expedientTipusId);
		
		if (!expedientTipus.isNtiActiu()) {
			throw new Exception("El tipus d'expedient no té les metadades NTI actives");
		}
		String codiEntitat = expedientTipus.getNtiOrgano();
		if(codiEntitat == null || "".equals(codiEntitat)) {
			throw new Exception(
					getMessage(
							request, 
							"expedient.tipus.integracio.distribucio.regla.unitatOrganica.error"));

		}
		// Consulta l'unitat arrel de la entitat del tipus d'expedinet
		try {
			UnitatOrganica unitatOrganica = pluginHelper.findUnitatOrganica(codiEntitat);
			codiEntitat = unitatOrganica.getCodiUnitatArrel();
		} catch (Exception e) {
			String errMsg = "Error consultant la unitat arrel de l'enitat " + codiEntitat + ": " + e.getMessage() + ". Es provarà la creació amb el codi de l'entitat";
			logger.error(errMsg, e);
			MissatgesHelper.warning(request, errMsg);
		}
		return codiEntitat;
	}

	private String getBackoffice() throws Exception {
		String backoffice = GlobalProperties.getInstance().getProperty("app.helium.distribucio.regles.api.rest.codi.backoffice");
		if (backoffice == null || "".equals(backoffice)) {
			throw new Exception("S'ha de configurar el codi del backoffice per l'API REST de regles de DISTRIBUCIO app.helium.distribucio.regles.api.rest.codi.backoffice " +
								"(backoffice= \"" + backoffice + "\")");
		}
		return backoffice;
	}

	private ReglesRestClient getReglesRestClient() throws Exception {
		
		String url = GlobalProperties.getInstance().getProperty("app.helium.distribucio.regles.api.rest.url");
		String usuari = GlobalProperties.getInstance().getProperty("app.helium.distribucio.regles.api.rest.usuari");
		String contrasenya = GlobalProperties.getInstance().getProperty("app.helium.distribucio.regles.api.rest.password");
		
		if (url == null || "".equals(url) ) {
			throw new Exception("S'han de configurar la URL per accedir a l'API REST de regles de DISTRIBUCIO app.helium.distribucio.regles.api.rest.url " +
								"(url= \"" + url + "\")");
		}

		ReglesRestClient client = new ReglesRestClient(
				url,
				usuari,
				contrasenya,
				true);		
		
		return client;
	}

	private List<HtmlOption> getSiNo() {
		List<HtmlOption> sino = new ArrayList<HtmlOption>();
		sino.add(new HtmlOption("", "-"));
		sino.add(new HtmlOption("true", "Sí"));
		sino.add(new HtmlOption("false", "No"));
		return sino;
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientTipusIntegracioDistribucioController.class);
}
