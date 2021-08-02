package es.caib.helium.back.controller;

import es.caib.helium.back.helper.AjaxHelper.AjaxResponse;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientReindexacioDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.service.ExpedientReindexacioService;
import es.caib.helium.logic.intf.service.TascaProgramadaService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Controlador per a la pàgina de monitoratge de reindexacions pendents
 * des del menú dels administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/reindexacions")
public class ReindexacioController extends BaseExpedientController {
	
	@Autowired
	ExpedientReindexacioService expedientReindexacioService;

	@Autowired
	TascaProgramadaService tascaProgramadaService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
						
		return "v3/reindexacions";
	}

	/** Mètode ajax per consultar les dades JSON a mostrar en la modal de reindexacions. Conté les dades
	 * dels elements de la cua pendents, la data de la consulta i les dades per tipus d'expedient.
	 * Filtra les dades per entorn si l'usuari no és administrador d'Helium.

	 * @param request
	 * @return
	 */
	@RequestMapping(value = "dades", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> dades(
			HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<String, Object>();
		// Data
		ret.put("data", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		// Dades reindexació
		Map<String, Object> dadesReindexacio = expedientReindexacioService.getDadesReindexacio();
		int cuaTotal = (Integer) dadesReindexacio.get("cuaTotal");
		Long cuaExpedients = (Long) dadesReindexacio.get("cuaExpedients");
		ExpedientReindexacioDto primer = dadesReindexacio.containsKey("primer") ? (ExpedientReindexacioDto) dadesReindexacio.get("primer") : null;
		ExpedientReindexacioDto darrer = dadesReindexacio.containsKey("darrer") ? (ExpedientReindexacioDto) dadesReindexacio.get("darrer") : null;
		@SuppressWarnings("unchecked")
		List<ExpedientReindexacioDto> cuaLlista = (List<ExpedientReindexacioDto>) dadesReindexacio.get("cuaLlista");
		ret.put("cuaTotal", cuaTotal);
		ret.put("cuaExpedients", cuaExpedients);
		ret.put("primer", primer);
		ret.put("darrer", darrer);
		ret.put("cuaLlista", cuaLlista);
		// Cua
		ret.put("cua", expedientReindexacioService.countPendentReindexacioAsincrona());
		// Estat actual de la reindexació
		ret.put("reindexant", tascaProgramadaService.isReindexarAsincronament());

		// Filtre per entorn
		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		Long entornId = persona.isAdmin() ? null : entornActual.getId();

		// Expedients tipus
		
		// <ExpedientTipusDto, Long errors, Long reindexacions>
		List<Map<String, String>> dades = new ArrayList<Map<String,String>>();
		Map<String, String> dadaMap;
		ExpedientTipusDto expedientTipus;
		for (Object[] d : expedientReindexacioService.getDades(entornId)) {
			dadaMap = new HashMap<String, String>();
			expedientTipus = (ExpedientTipusDto) d[0];
			// Entorn
			dadaMap.put("entornId", expedientTipus.getEntorn().getId().toString());
			dadaMap.put("entornCodi", expedientTipus.getEntorn().getCodi());
			dadaMap.put("entornNom", expedientTipus.getEntorn().getNom());
			// Expedient tipus
			dadaMap.put("tipusId", expedientTipus.getId().toString());
			dadaMap.put("tipusCodi", expedientTipus.getCodi());
			dadaMap.put("tipusNom", expedientTipus.getNom());
			// errors
			dadaMap.put("errors", String.valueOf(d[1]));
			// pendents
			dadaMap.put("pendents", String.valueOf(d[2]));
			
			dades.add(dadaMap);
		}
		ret.put("dades", dades);

		return ret;
	}
	
	/** Mètode per consultar via Ajax el llistat d'expedients amb errors de reindexació.
	 * Aquest contingut es consulta expandint la alerta d'expedients amb errors en la pàgina
	 * de la consulta.
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/tipus/{tipusId}/expedientsErrorsReindexacio")
	public String expedientsErrorReindexacio(
			HttpServletRequest request,
			@PathVariable Long tipusId,
			Model model) {


		// Consulta si hi ha expedients amb error de reindexació o pendents de reindexar
		List<ExpedientDto> expedients = new ArrayList<ExpedientDto>();
		try {
			List<Long> ids = expedientReindexacioService.consultaIdsErrorReindexació(tipusId);
			if (!ids.isEmpty())
				expedients = expedientService.findAmbIds(new HashSet<Long>(ids));
		} catch (Exception e) {
			String errMsg = "Error consultant expedients amb error reindexació pel tipus d'expedient " + tipusId + ": " + e.getMessage();
			logger.error(errMsg, e);
			model.addAttribute("error", errMsg);
		}
		model.addAttribute("expedients", expedients);
		model.addAttribute("dataComprovacio", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));

		return "v3/expedientConsultaErrorsReindexacio";
	}
	
	/** Mètode per consultar via Ajax el llistat d'expedients pendents de reindexació.
	 * Aquest contingut es consulta expandint la alerta d'expedients pendent de reindexació en la pàgina
	 * de la consulta.
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/tipus/{tipusId}/expedientsPendentsReindexacio")	
	public String expedientsPendentsReindexacio(
			HttpServletRequest request,
			@PathVariable Long tipusId,
			Model model) {

		// Consulta si hi ha expedients amb error de reindexació o pendents de reindexar
		List<ExpedientDto> expedients = new ArrayList<ExpedientDto>();
		try {
			List<Long> ids = expedientReindexacioService.consultaIdsPendentReindexació(tipusId);
			if (!ids.isEmpty())
				expedients = expedientService.findAmbIds(new HashSet<Long>(ids));
		} catch (Exception e) {
			String errMsg = "Error consultant expedients pendents de reindexació pel tipus d'expedient " + tipusId + ": " + e.getMessage();
			logger.error(errMsg, e);
			model.addAttribute("error", errMsg);
		}
		model.addAttribute("expedients", expedients);
		model.addAttribute("dataComprovacio", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));

		return "v3/expedientConsultaErrorsReindexacio";
	}
	
	
	/** Mètode per consultar via Ajax el llistat d'expedients pendents de reindexació o amb error de reindexació.
	 * Aquest contingut es pot consultar des de l'opció
	 * d'administració de reindexacions en prèmer sobre un tipus d'expedient amb error.
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/tipus/{tipusId}/expedients")	
	public String expedients(
			HttpServletRequest request,
			@PathVariable Long tipusId,
			Model model) {

		// Consulta si hi ha expedients amb error de reindexació o pendents de reindexar
		List<ExpedientDto> expedients = new ArrayList<ExpedientDto>();
		try {
			List<Long> ids = expedientReindexacioService.consultaIdsExpedients(tipusId);
			if (!ids.isEmpty())
				expedients = expedientService.findAmbIds(new HashSet<Long>(ids));
		} catch (Exception e) {
			String errMsg = "Error consultant expedients pendents de reindexació pel tipus d'expedient " + tipusId + ": " + e.getMessage();
			logger.error(errMsg, e);
			model.addAttribute("error", errMsg);
		}
		model.addAttribute("expedients", expedients);
		model.addAttribute("dataComprovacio", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));

		return "v3/expedientConsultaErrorsReindexacio";
	}
	
	/** Mètode ajax per reindexar un expedient.

	 * @param request
	 * @return Retonra un objecte json amb el resultat de la reindexació
	 */
	@RequestMapping(value = "expedient/{expedientId}/reindexar", method = RequestMethod.GET)
	@ResponseBody
	public AjaxResponse expedientReindexar(
			HttpServletRequest request,
			@PathVariable Long expedientId) {
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		try {
			boolean success = expedientService.luceneReindexarExpedient(expedientId);
			ajaxResponse.setError(!success);
			ajaxResponse.setMissatge(getMessage(
					request,
					"info.expedient.reindexat" + (success ? "" : ".error")));
		} catch (Exception ex) {
			ajaxResponse.setError(true);
			ajaxResponse.setMissatge(getMessage(request, "error.reindexar.expedient") + ". " + ex.getMessage());
		}
		return ajaxResponse;
	}

	/** Mètode per aturar o reemprendre la tasca en 2n pla de reindexacions asíncrones.

	 * @param request
	 * @param accio Acció per paràmetre, pot ser "aturar" o "iniciar".
	 * @return Retonra un objecte json amb el resultat de la reindexació
	 */
	@RequestMapping(value = "reindexacionsAsincrones/{accio}", method = RequestMethod.GET)
	@ResponseBody
	public AjaxResponse reindexacionsAsincrones(
			HttpServletRequest request,
			@PathVariable String accio) {
		
		AjaxResponse ajaxResponse = new AjaxResponse();
		
		// Comprovació que sigui un usuari administrador
		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		if (!persona.isAdmin()) {
			ajaxResponse.setError(true);
			ajaxResponse.setMissatge("Només un usuari administrador pot iniciar o aturar les reindexacions");
		}

		boolean reindexar = !"aturar".equals(accio);
		try {
			boolean isReindexar = tascaProgramadaService.isReindexarAsincronament();
			if (reindexar != isReindexar) {
				tascaProgramadaService.setReindexarAsincronament(reindexar);
				ajaxResponse.setMissatge("La tasca de reindexació s'ha " + (reindexar ? "iniciat" : "aturat") + " correctament.");
			} else {
				ajaxResponse.setMissatge("La tasca de reindexació assíncrona ja estava " + (reindexar ? "iniciada" : "aturada"));
			}
			ajaxResponse.getDades().put("reindexar", tascaProgramadaService.isReindexarAsincronament());
		} catch(Exception e) {
			String errMsg = "Error fixant la propietat de reindexació a " + reindexar + ": " + e.getMessage();
			logger.error(errMsg, e);
			ajaxResponse.setError(true);
			ajaxResponse.setMissatge(errMsg);
		}
				
		return ajaxResponse;
	}

	
	private static final Log logger = LogFactory.getLog(ReindexacioController.class);
}