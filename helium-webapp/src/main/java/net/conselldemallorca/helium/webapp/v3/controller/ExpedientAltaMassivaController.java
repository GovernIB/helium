package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.util.CsvHelper;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaListDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientAltaMassivaCommand;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientAltaMassivaCommand.AltaMassiva;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/** Controlador pel formulari d'alta massiva d'expedients a partir d'una fulla CSV. 
 * Programa una execució massiva per cada fila de la fulla CSV per crear un
 * expedient de forma massiva.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient/altaMassiva")
public class ExpedientAltaMassivaController extends BaseExpedientController {


	@Autowired
	private ExecucioMassivaService execucioMassivaService;

	/** Informa el model dels expedients tipus permesos. */
	@ModelAttribute("expedientTipusPermesos")
	public List<ExpedientTipusDto> modelExpedientTipusPermesos() {
		// Buscar tipus d'expedient amb permís d'administració o execució d'scripts
		List<ExpedientTipusDto> expedientTipusPermesos = expedientTipusService.findAmbEntornPermisExecucioScript(EntornActual.getEntornId());
		return expedientTipusPermesos;
	}

	/** Pàgina per mostrar la informació de la darrera alta d'expedient massiva segons el tipus
	 * d'expedient seleccionat i per mostrar el formulari d'alta massiva si no hi ha cap en execució actualmen.
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String formulariGet(
			HttpServletRequest request,
			Model model) {
		ExpedientAltaMassivaCommand expedientAltaMassivaCommand = new ExpedientAltaMassivaCommand();
		model.addAttribute("command", expedientAltaMassivaCommand);
						
		return "v3/expedientAltaMassiva";
	}
	
	/** 
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String formulariPost(
			HttpServletRequest request,
			@Validated(AltaMassiva.class) 
			@ModelAttribute("command")
			ExpedientAltaMassivaCommand command,
			BindingResult binding,
			Model model) {
		model.addAttribute("command", command);
		if (! binding.hasErrors()) {
			try {
				// Consulta primer que tingui permís sobre el tipus d'expedient
				ExpedientTipusDto expedientTipus = null;
				for (ExpedientTipusDto et : this.modelExpedientTipusPermesos()) {
					if (et.getId().equals(command.getExpedientTipusId())) {
						expedientTipus = et;
						break;
					}
				}
				if (expedientTipus != null) {
					// Programa la execució massiva
					ExecucioMassivaDto dto = new ExecucioMassivaDto();
					dto.setExpedientTipusId(command.getExpedientTipusId());
					dto.setTipus(ExecucioMassivaTipusDto.ALTA_MASSIVA);
					dto.setEnviarCorreu(false);
					dto.setParam2(command.getFile().getBytes());
					dto.setContingutCsv(command.getContingutCsv());
					execucioMassivaService.crearExecucioMassiva(dto);
					MissatgesHelper.success(
							request,
							getMessage(
									request,
									"expedient.alta.massiva.form.success"));
				} else {
					MissatgesHelper.error(
							request,
							getMessage(
									request,
									"expedient.alta.massiva.form.error.permis"));
				}
			} catch(Exception e) {
				String errMsg = this.getMessage(request, "expedient.alta.massiva.form.error", new Object[] {e.getMessage()});
				logger.error(errMsg, e);
				MissatgesHelper.error(request, errMsg);
			}
		}		
		return "v3/expedientAltaMassiva";
	}
	
	/** Mètode ajax per consultar les dades JSON de la darrera execució d'alta massiva per saber en quin estat 
	 * es troba.

	 * @param request
	 * @return
	 */
	@RequestMapping(value = "dades/{expedientTipusId}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> dades(
			@PathVariable Long expedientTipusId,
			HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<String, Object>();
		// Data
		ret.put("data", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		// Dades darrera alta massiva per CSV
		ExecucioMassivaListDto execucioMassiva = execucioMassivaService.getDarreraAltaMassiva(expedientTipusId);
		ret.put("execucioMassiva", execucioMassiva);
		
		return ret;
	}
	
	/** Mètode per descarregar l'arxiu CSV amb el resultat de l'execució massiva.
	 * 
	 * @param request
	 * @param response
	 * @param execucioMassivaId
	 * @throws Exception
	 */
	@RequestMapping(value = "/{execucioMassivaId}/resultat", method = RequestMethod.GET)
	@ResponseBody
	public void resultat (
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long execucioMassivaId) throws Exception {

    	try {
    		ExecucioMassivaDto execucioMassiva = execucioMassivaService.findAmbId(execucioMassivaId);
    		String[][] informacioCsv = execucioMassivaService.getResultatAltaMassiva(execucioMassivaId);
    		CsvHelper csvHelper = new CsvHelper();
    		byte[] contingutCsv = csvHelper.toCsv(informacioCsv);
    		this.writeFileToResponse("resultat_alta_massiva_" + new SimpleDateFormat("yyyy.MM.dd_HHmmss").format(execucioMassiva.getDataInici()) + ".csv", contingutCsv, response);
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"expedient.alta.massiva.resultats.success"));        			

    	} catch(Exception e) {
    		String errMsg = getMessage(
					request, 
					"expedient.alta.massiva.resultats.error",
					new Object[]{e.getClass() + " " + e.getMessage()});
    		logger.error(errMsg, e);
    		MissatgesHelper.error(
    				request,
    				errMsg);
    		throw(e);
    	}        
	}
	
	/** Mètode per descarregar l'arxiu CSV d'exemple
	 * 
	 * @param request
	 * @param response
	 * @param execucioMassivaId
	 * @throws Exception
	 */
	@RequestMapping(value = "/exempleCsv", method = RequestMethod.GET)
	@ResponseBody
	public void exempleCsv (
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

    	try {      		
        	InputStream is = this.getClass().getResourceAsStream("/arxius/exemple_alta_massiva.csv");
        	byte[] arxiuExempleContingut = IOUtils.toByteArray(is);

    	    this.writeFileToResponse(
    				"exemple_alta_massiva.csv", 
    				arxiuExempleContingut, 
    				response);
    	    
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
    						new Object[]{e.getLocalizedMessage()}));
    	}        
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientAltaMassivaController.class);
}
