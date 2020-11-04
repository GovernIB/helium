package net.conselldemallorca.helium.webapp.v3.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientReindexacioService;

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
		// Cua
		ret.put("cua", expedientReindexacioService.countPendentReindexacioAsincrona());

		// Filtre per entorn
		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		Long entornId = persona.isAdmin() ? null : EntornActual.getEntornId();

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
	 * @param consultaId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{tipusId}/expedientsErrorsReindexacio")
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
	 * @param consultaId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{tipusId}/expedientsPendentsReindexacio")	
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
	 * @param consultaId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{tipusId}/expedients")	
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

	private static final Log logger = LogFactory.getLog(ReindexacioController.class);
}
