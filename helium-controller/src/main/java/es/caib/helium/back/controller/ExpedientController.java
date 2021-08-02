/** HERÈNCIA
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.command.CanviVersioProcesCommand;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ObjectTypeEditorHelper;
import es.caib.helium.back.view.ArxiuView;
import es.caib.helium.logic.intf.dto.AlertaDto;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientErrorDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.service.AplicacioService;
import es.caib.helium.logic.intf.service.ExpedientRegistreService;
import es.caib.helium.logic.intf.service.ExpedientService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientRegistreService expedientRegistreService;

	@Autowired
	private AplicacioService aplicacioService;

	/** Per donar format a les dates sense haver d'instanciar l'objecte cada cop. */
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");


	@RequestMapping(value = "/{expedientId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		return mostrarInformacioExpedientPerPipella(
				request,
				expedientId,
				model,
				null);
	}
	
	@RequestMapping(value = "/proces/{processInstanceId}", method = RequestMethod.GET)
	public String infoProces(
			HttpServletRequest request, 
			@PathVariable String processInstanceId, 
			Model model) {
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		if (expedientId == null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"error.expedientService.noExisteix"));
			return "redirect:/v3/expedient";
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long expedientId) {

		try {
			expedientService.delete(expedientId);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"info.expedient.esborrat"));
			return "redirect:/v3/expedient";
		} catch (Exception e) {
			String errMsg = getMessage(request, "error.esborrant.expedient", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
			String referer = request.getHeader("Referer");
		    return "redirect:"+ referer;
		}			
	}

	@RequestMapping(value = "/{expedientId}/reindexa", method = RequestMethod.GET)
	public String reindexa(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			Model model) {
		try {
			if (expedientService.luceneReindexarExpedient(expedientId))
				MissatgesHelper.success(
						request,
						getMessage(
								request,
								"info.expedient.reindexat"));
			else 
				MissatgesHelper.error(
						request,
						getMessage(
								request,
								"info.expedient.reindexat.error"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.reindexar.expedient") + ". " + ex.getMessage());
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/finalitzar", method = RequestMethod.GET)
	public String finalitzar(
			HttpServletRequest request,
			@PathVariable Long expedientId, 
			Model model) {
		try {
			expedientService.finalitzar(expedientId);
			MissatgesHelper.success(
					request,
					getMessage(
							request,
							"info.expedient.finalitzat"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "expedient.error.finalitzant.expedient") + ". " + ex.getMessage());
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/alertes", method = RequestMethod.GET)
	public String alertes(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		List<AlertaDto> alertes = expedientService.findAlertes(expedientId);
		Map<String, String> persones = getNomPersonaPerAlertes(alertes);
		
		model.addAttribute("expedientId", expedientId);		
		model.addAttribute("alertes",alertes);
		model.addAttribute("persones", persones);
		return "v3/expedient/alertes";
	}
	
	private Map<String, String> getNomPersonaPerAlertes(List<AlertaDto> alertes) {
		Map<String, String> resposta = new HashMap<String, String>();
		for (AlertaDto alerta: alertes) {
			if (alerta.getDestinatari() != null && resposta.get(alerta.getDestinatari()) == null) {
				PersonaDto persona = aplicacioService.findPersonaAmbCodi(alerta.getDestinatari());
				if (persona != null)
					resposta.put(persona.getCodi(), persona.getNomSencer());
			}
		}
		return resposta;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{expedientId}/errors", method = RequestMethod.GET)
	public String errors(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		
		Object[] errors = expedientService.findErrorsExpedient(expedientId);
		
		List<ExpedientErrorDto> errors_bas = (List<ExpedientErrorDto>) errors[0];
		List<ExpedientErrorDto> errors_int = (List<ExpedientErrorDto>) errors[1];
		
		model.addAttribute("expedientId", expedientId);		
		model.addAttribute("errors_bas",errors_bas);
		model.addAttribute("errors_int",errors_int);
		return "v3/expedient/errors";
	}

	@RequestMapping(value = "/{expedientId}/imatgeDefProces", method = RequestMethod.GET)
	public String imatgeProces(
			HttpServletRequest request,
			@PathVariable(value = "expedientId") Long expedientId, 
			Model model) {
		ArxiuDto imatge = expedientService.getImatgeDefinicioProces(
				expedientId,
				null);
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_FILENAME,
				imatge.getNom());
		model.addAttribute(
				ArxiuView.MODEL_ATTRIBUTE_DATA,
				imatge.getContingut());
		return "arxiuView";
	}
	
	@RequestMapping(value = "/{expedientId}/canviVersio", method = RequestMethod.GET)
	public String changeDefProc(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			ModelMap model) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			DefinicioProcesExpedientDto definicioProces = dissenyService.getDefinicioProcesByTipusExpedientById(expedient.getTipus().getId());
			List<DefinicioProcesExpedientDto> subDefinicioProces = dissenyService.getSubprocessosByProces(expedient.getTipus().getId(), definicioProces.getJbpmId());
			CanviVersioProcesCommand canviVersioProcesCommand = new CanviVersioProcesCommand();
			canviVersioProcesCommand.setDefinicioProcesId(definicioProces.getId());		

			model.addAttribute("expedient", expedient);
			model.addAttribute(canviVersioProcesCommand);
			model.addAttribute("definicioProces",definicioProces);
			model.addAttribute("subDefinicioProces", subDefinicioProces);
		} catch (Exception ex) {
			logger.error("Canviant versió de la definició de procés (" + "id=" + expedientId + ")", ex);
			MissatgesHelper.error(request, getMessage(request, "error.canviar.versio.proces"));
		}
		return "v3/expedient/canviVersio";
	}
	
	@RequestMapping(value = "/{expedientId}/canviVersio", method = RequestMethod.POST)
	public String changeDefProc(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@ModelAttribute CanviVersioProcesCommand command, 
			@RequestParam(value = "accio", required = true) String accio,
			ModelMap model) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			DefinicioProcesExpedientDto definicioProces = dissenyService.getDefinicioProcesByTipusExpedientById(expedient.getTipus().getId());
			List<DefinicioProcesExpedientDto> subDefinicioProces = dissenyService.getSubprocessosByProces(expedient.getTipus().getId(), definicioProces.getJbpmId());
			expedientService.procesDefinicioProcesCanviVersio(
					expedientId, 
					command.getDefinicioProcesId(), 
					command.getSubprocesId(), 
					subDefinicioProces);
			MissatgesHelper.success(request, getMessage(request, "info.expedient.canviversio"));
		} catch (Exception ex) {
			logger.error("Canviant versió de la definició de procés (" + "id=" + expedientId + ")", ex);
			MissatgesHelper.error(request, getMessage(request, "error.canviar.versio.proces"));
		}
		return modalUrlTancar();
	}
	
//	@RequestMapping(value = "/{expedientId}/updateDefinicioProces/{versio}", method = RequestMethod.GET)
//	@ResponseBody
//	public String changeDefProc(
//			HttpServletRequest request,
//			@PathVariable Long expedientId,
//			@PathVariable int versio,
//			ModelMap model) {
//		String nom = null;
//		try {
//			nom = expedientService.canviVersioDefinicioProces(
//					expedientId,
//					versio);
//			MissatgesHelper.success(request, getMessage(request, "info.canvi.versio.realitzat") );
//		} catch (Exception ex) {
//			logger.error("Canviant versió de la definició de procés (" +
//					"id=" + expedientId + ", " +
//					"versio=" + versio + ")", ex);
//			if (ex.getCause() instanceof ChangeLogException)
//				MissatgesHelper.error(request, getMessage(request, "error.canviar.versio.proces.logs") + "<br/> " + ex.getCause().getMessage());
//			else
//				MissatgesHelper.error(request, getMessage(request, "error.canviar.versio.proces"));
//		}
//	        	
//		return JSONValue.toJSONString(nom);
//	}
	
	@RequestMapping(value = "/{expedientId}/buidalog", method = RequestMethod.GET)
	public String buidaLog(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			if (expedient.isPermisLogManage()) {
				expedientRegistreService.eliminaInformacioRetroaccio(
						expedient.getId());
				MissatgesHelper.success(request, getMessage(request, "info.expedient.buidatlog"));
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.buidar.logs"));
			}
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.buidarlog.expedient") + ": " + ex.getLocalizedMessage());
		}
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	@RequestMapping(value = "/{expedientId}/desfinalitzar", method = RequestMethod.GET)
	public String desfinalitzar(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {
		try {
			expedientService.desfinalitzar(expedientId);
			MissatgesHelper.success(request, getMessage(request, "info.expedient.desfinalitzat") );
		} catch (Exception e) {
			String errMsg = getMessage(request, "error.desfinalitzant.expedient", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}		
		return "redirect:/v3/expedient/" + expedientId;
	}
	
	/** Mètode Ajax per refrescar l'estat de l'expedient quan es tramiten tasques des de la gestió
	 * de l'expedient.
	 * @return Retorna un JSON amb {estat: "Estat", dataFi : "dd/MM/yyyy HH:mm"}
	 */
	@RequestMapping(value = "/{expedientId}/consultaEstat", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> consultaEstat(
			HttpServletRequest request, 
			@PathVariable Long expedientId, 
			Model model) {

		// Objecte amb les propietats de retorn
		Map<String, Object> data = new HashMap<String, Object>();
		// Recupera l'informació de l'expedient
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		Date dataFi = expedient.getDataFi();
		String estat;
		if (dataFi == null) {
			estat = expedient.getEstat() != null? 
					expedient.getEstatNom() 
					: getMessage(request, "comu.estat.iniciat");
		} else {
			estat = getMessage(request, "comu.estat.finalitzat");
		}
		data.put("estat", estat);
		data.put("dataFi", dataFi != null? sdf.format(dataFi) : null);
		data.put("ambErrors", expedient.isAmbErrors());
		data.put("aturat", expedient.isAturat());
		data.put("anulat", expedient.isAnulat());
		data.put("anulatComentari", expedient.getComentariAnulat());
		data.put("alertesTotals", expedient.getAlertesTotals());
		data.put("alertesPendents", expedient.getAlertesPendents());
		data.put("reindexarData", (expedient.getReindexarData() != null) ?
				sdf.format(expedient.getReindexarData())
				: null);
		data.put("reindexarError", expedient.isReindexarError());
		
		return data;
	}

	@RequestMapping(value = "/{expedientId}/metadadesNti", method = RequestMethod.GET)
	public String info(
			@PathVariable Long expedientId,
			HttpServletRequest request,
			Model model) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			model.addAttribute(
					"expedient",
					expedient);
			if (expedient.isArxiuActiu() && expedient.getNtiOrgano() == null) {
				// La migració d'expedients no NTI provocava errors
				model.addAttribute("errorMetadadesNti", Boolean.TRUE);
			}
			model.addAttribute(
					"arxiuDetall",
					expedientService.getArxiuDetall(expedientId));
		} catch (Exception e) {
			String errMsg = getMessage(request, "expedient.info.error.consulta.arxiu", new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}			
		return "v3/expedientMetadadesNtiInfo";
	}
	
	/** Mètode per incoporar el document a l'Arxiu en el cas que l'expedient estigui integrat però el document no. Acció des
	 * de la modal de metadades NTI del document.
	 * @return Retorna cap a la pàgina de metadades nti del document.
	 */
	@RequestMapping(value = "/{expedientId}/metadadesNti/arreglar", method = RequestMethod.POST)
	public String arreglarNti(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		try {
			expedientService.arreglarMetadadesNti(expedientId);
			MissatgesHelper.success(request, getMessage(request, "expedient.metadades.nti.dades.error.arreglar.success"));
		} catch(Exception e) {
			String errMsg = getMessage(request, "expedient.metadades.nti.dades.error.arreglar.error", new Object[] {e.getMessage()}); 
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		return "redirect:" + request.getHeader("Referer");
	}
	

	@RequestMapping(value = "/{expedientId}/migrarArxiu", method = RequestMethod.GET)
	public String migrarArxiu(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		try {
			ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
			if (expedient.isPermisAdministration()) {
				expedientService.migrarArxiu(expedient.getId());
				MissatgesHelper.success(request, getMessage(request, "info.expedient.migrat.arxiu"));
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.migrar.expedient.arxiu"));
			}
		} catch (Exception ex) {
			logger.error("Error migrant l'expedient a l'arxiu: ", ex);
			MissatgesHelper.error(request, getMessage(request, "error.migrar.expedient.arxiu") + ": " + ex.getLocalizedMessage());
		}
		return "redirect:/v3/expedient/" + expedientId;
	}


	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				byte[].class,
				new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Long.class,
				new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(
				Double.class,
				new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(
				BigDecimal.class,
				new CustomNumberEditor(
						BigDecimal.class,
						new DecimalFormat("#,##0.00"),
						true));
		binder.registerCustomEditor(
				Boolean.class,
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Object.class,
				new ObjectTypeEditorHelper());
	}
	
	private static final Log logger = LogFactory.getLog(ExpedientController.class);
}