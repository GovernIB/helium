package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.util.CsvHelper;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper.AjaxResponse;

/**
 * Controlador per la execucions massives
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/execucionsMassives")
public class ExecucionsMassivesController extends BaseExpedientController {

	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	
	/** Mètode per descarregar l'arxiu CSV amb el resultat de l'execució massiva.
	 * 
	 * @param request
	 * @param response
	 * @param execucioMassivaId
	 * @throws Exception
	 */
	@RequestMapping(value = "getCsvResultat/{execucioMassivaId}", method = RequestMethod.GET)
	@ResponseBody
	public void getCsvResultat(HttpServletRequest request, 
			HttpServletResponse response, 
			@PathVariable Long execucioMassivaId)
			throws Exception {
		try {
    		ExecucioMassivaDto execucioMassiva = execucioMassivaService.findAmbId(execucioMassivaId);
    		String[][] informacioCsv = execucioMassivaService.getResultatAltaMassiva(execucioMassivaId);
    		CsvHelper csvHelper = new CsvHelper();
    		byte[] contingutCsv = csvHelper.toCsv(informacioCsv);
    		this.writeFileToResponse("Resultats_" + execucioMassivaId  + "_" + new SimpleDateFormat("yyyy.MM.dd_HHmmss").format(execucioMassiva.getDataInici()) + ".csv", contingutCsv, response);
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.alta.massiva.csvoriginal.success"));        			

    	} catch(Exception e) {
    		String errMsg = getMessage(
					request, 
					"expedient.alta.massiva.csvoriginal.error",
					new Object[]{e.getClass() + " " + e.getMessage()});
    		logger.error(errMsg, e);
    		MissatgesHelper.error(
    				request,
    				errMsg,
    				e);
    		throw(e);
    	}        
	}
	
	/** Mètode per descarregar l'arxiu CSV original
	 * 
	 * @param request
	 * @param response
	 * @param execucioMassivaId
	 * @throws Exception
	 */
	@RequestMapping(value = "/getCsvOriginalContent/{execucioMassivaId}", method = RequestMethod.GET)
	@ResponseBody
	public void getCsvOriginalContent (
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long execucioMassivaId) throws Exception {

    	try { 
    		ExecucioMassivaDto execucioMassiva = execucioMassivaService.findAmbId(execucioMassivaId);
    		byte[] contentCsv = execucioMassivaService.getCsvOriginalContent(execucioMassivaId);
    		this.writeFileToResponse("CSV_original_" + execucioMassivaId + "_" + new SimpleDateFormat("yyyy.MM.dd_HHmmss").format(execucioMassiva.getDataInici()) + ".csv", contentCsv, response);
    	    
    	    MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.alta.massiva.exemple.success"));
    	} catch(Exception e) {
    		MissatgesHelper.error(
    				request,
    				getMessage(
    						request, 
    						"expedient.alta.massiva.exemple.error",
    						new Object[]{e.getLocalizedMessage()}),
					e);
    	}        
	}
	

	@RequestMapping(value = "/{nivell}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable String nivell, Model model) {
		model.addAttribute("nivell", nivell);
		return "v3/execucionsMassives";
	}

	@RequestMapping(value = "{nivell}/refreshBarsExpedientMassive", method = RequestMethod.GET, produces = {
			"application/json; charset=UTF-8" })
	@ResponseBody
	public String refreshBarsExpedientMassiveAct(@RequestParam(value = "results", required = false) int numResults,
			HttpServletRequest request, HttpServletResponse response, @PathVariable String nivell, ModelMap model,
			HttpSession session) throws ServletException, IOException {

		return execucioMassivaService.getJsonExecucionsMassives(numResults, nivell);

	}

	/**
	 * Obtenir les dades de l'execució massiva i els seus expedients.
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "getExecucioMassivaDetall", method = RequestMethod.GET, produces = {
			"application/json; charset=UTF-8" })
	@ResponseBody
	public String getExecucioMassivaDetall(
			@RequestParam(value = "execucioMassivaId", required = false) Long execucioMassivaId,
			HttpServletRequest request, HttpServletResponse response, ModelMap model, HttpSession session)
			throws ServletException, IOException {
		return execucioMassivaService.getExecucioMassivaDetall(execucioMassivaId);
	}

	/**
	 * Mètode per cancel·lar les execucions massives de tots els expedients d'una
	 * execució massiva.
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "cancelExecucioMassiva", method = RequestMethod.POST)
	@ResponseBody
	public AjaxResponse cancelExecucioMassiva(@RequestParam(value = "id") Long id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model, HttpSession session) throws Exception {

		AjaxResponse ajaxResponse = new AjaxResponse();
		try {
			int n = execucioMassivaService.cancelarExecucioMassiva(id);
			ajaxResponse.setMissatge("L'execució massiva amb id " + id + " s'ha cancel·lat correctament per " + n
					+ " expedients pendents.");
		} catch (Exception ex) {
			String errMsg = "No s'han pogut cancel·lar les execucions massives d'expedients per l'execució massiva amb id "
					+ id + ": " + ex.getMessage();
			logger.error(errMsg, ex);
			ajaxResponse.setError(true);
			ajaxResponse.setMissatge(errMsg);
		}
		return ajaxResponse;
	}

	@RequestMapping(value = "rependreExecucioMassiva", method = RequestMethod.POST)
	@ResponseBody
	public AjaxResponse rependreExecucioMassiva(@RequestParam(value = "id") Long id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model, HttpSession session) throws Exception {
			
			AjaxResponse ajaxResponse = new AjaxResponse();
			try {
				execucioMassivaService.rependreExecucioMassiva(id);
				ajaxResponse.setMissatge("L'execució massiva amb id " + id + " s'ha reprès correctament ");
				
			} catch (Exception ex) {
				String errMsg = "No s'ha pogut rependre la execució massiva d'expedients amb id " + id + ": " + ex.getMessage();
				logger.error(errMsg, ex);
				ajaxResponse.setError(true);
				ajaxResponse.setMissatge(errMsg);
			}
			
			return ajaxResponse;
	}
	
	@RequestMapping(value = "reintentarExecucioMassiva", method = RequestMethod.POST)
	@ResponseBody
	public AjaxResponse reintentarExecucioMassiva(@RequestParam(value = "id") Long id, HttpServletRequest request,
			HttpServletResponse response, ModelMap model, HttpSession session) throws Exception {
			
			AjaxResponse ajaxResponse = new AjaxResponse();
			try {
				execucioMassivaService.reintentarExecucioMassiva(id);
				ajaxResponse.setMissatge("L'execució massiva amb id " + id + " s'ha reintentat correctament ");
				
			} catch (Exception ex) {
				String errMsg = "Error reintentant l'acció massiva amb id " + id + ": " + ex.getMessage();
				logger.error(errMsg, ex);
				ajaxResponse.setError(true);
				ajaxResponse.setMissatge(errMsg);
			}
			
			return ajaxResponse;
	}


	/**
	 * Refresca las barras de progreso de detalle de las acciones masivas
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "cancelExpedientMassiveAct", method = RequestMethod.POST)
	@ResponseBody
	public void cancelExpedientMassiveAct(@RequestParam(value = "idExp", required = false) Long idExp,
			HttpServletRequest request, HttpServletResponse response, ModelMap model, HttpSession session)
			throws Exception {
		execucioMassivaService.cancelarExecucioMassivaExpedient(idExp);
	}

	private static final Log logger = LogFactory.getLog(ExecucionsMassivesController.class);
}
