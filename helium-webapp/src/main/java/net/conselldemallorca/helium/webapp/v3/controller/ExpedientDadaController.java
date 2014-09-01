/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Controlador per a la pipella de dades de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientDadaController extends BaseExpedientController {

	@Autowired
	private ExpedientService expedientService;

	@RequestMapping(value = "/{expedientId}/dada", method = RequestMethod.GET)
	public String dades(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"dades",
					expedientService);
		}
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		model.addAttribute(
				"expedient",
				expedient);
		/*model.addAttribute(
				"dades",
				expedientService.findDadesPerInstanciaProces(
						expedientId,
						null));
		model.addAttribute(
				"agrupacions",
				expedientService.findAgrupacionsDadesPerInstanciaProces(
						expedientId,
						null));*/
		// Obtenim l'arbre de processos, per a poder mostrar la informació de tots els processos
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, Map<CampAgrupacioDto, List<ExpedientDadaDto>>> dades = new LinkedHashMap<InstanciaProcesDto, Map<CampAgrupacioDto,List<ExpedientDadaDto>>>();
		// Per a cada instància de procés ordenem les dades per agrupació  
		// (si no tenen agrupació les primeres) i per ordre alfabètic de la etiqueta
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			Map<CampAgrupacioDto, List<ExpedientDadaDto>> dadesInstancia = null;
			if (instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
				dadesInstancia = getDadesInstanciaProces(expedientId, instanciaProces.getId());
			}
			dades.put(instanciaProces, dadesInstancia);
		}
		model.addAttribute("expedientId", expedientId);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("arbreProcessos", arbreProcessos);
		model.addAttribute("dades", dades);
		return "v3/expedientDades";
	}
	
	@RequestMapping(value = "/{expedientId}/dades/{procesId}", method = RequestMethod.GET)
	public String dadesProces(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String procesId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (potConsultarExpedient(expedient)) {
			model.addAttribute("dades", getDadesInstanciaProces(expedientId, procesId));
		}
		return "v3/procesDades";
	}
	
	private Map<CampAgrupacioDto, List<ExpedientDadaDto>> getDadesInstanciaProces(Long expedientId, String instaciaProcesId) {
		
		// definirem un mapa. La clau serà el nom de l'agrupació, i el valor el llistat de variables de l'agrupació
		Map<CampAgrupacioDto, List<ExpedientDadaDto>> dadesProces = new LinkedHashMap<CampAgrupacioDto, List<ExpedientDadaDto>>();
		
		// Obtenim les dades de la definició de procés
		List<ExpedientDadaDto> dadesInstancia = expedientService.findDadesPerInstanciaProces(expedientId, instaciaProcesId);
		if (dadesInstancia == null || dadesInstancia.isEmpty())
			return null;
		Collections.sort(
				dadesInstancia, 
				new Comparator<ExpedientDadaDto>() {
					public int compare(ExpedientDadaDto d1, ExpedientDadaDto d2) {
						if (d1.getAgrupacioId() == null && d2.getAgrupacioId() == null)
							return 0;
						if (d1.getAgrupacioId() == null ^ d2.getAgrupacioId() == null)
							return (d1.getAgrupacioId() == null ? -1 : 1);
						int c = d1.getAgrupacioId().compareTo(d2.getAgrupacioId());
						if (c != 0) 
							return c;
						else 
							return d1.getCampEtiqueta().compareToIgnoreCase(d2.getCampEtiqueta());
					}
				});
		
		// Obtenim les agrupacions de la definició de procés
		// Les posam amb un map per a que obtenir el nom sigui directe
		List<CampAgrupacioDto> agrupacions = expedientService.findAgrupacionsDadesPerInstanciaProces(expedientId, instaciaProcesId);
		Map<Long,CampAgrupacioDto> magrupacions = new HashMap<Long, CampAgrupacioDto>();
		for (CampAgrupacioDto agrupacio : agrupacions) {
			magrupacions.put(agrupacio.getId(), agrupacio);
		}
		magrupacions.put(null, null);
		
		Long agrupacioId = null;
		List<ExpedientDadaDto> dadesAgrupacio = new ArrayList<ExpedientDadaDto>();
		
		for (ExpedientDadaDto dada: dadesInstancia) {
			if ((agrupacioId == null && dada.getAgrupacioId() == null) || dada.getAgrupacioId().equals(agrupacioId)) {
				dadesAgrupacio.add(dada);
			} else {
				if (!dadesAgrupacio.isEmpty()) {
					dadesProces.put(magrupacions.get(agrupacioId), dadesAgrupacio);
					dadesAgrupacio = new ArrayList<ExpedientDadaDto>();
				}
				agrupacioId = dada.getAgrupacioId();
				dadesAgrupacio.add(dada);
			}
		}
		
		dadesProces.put(magrupacions.get(agrupacioId), dadesAgrupacio);
		return dadesProces;
	}
}
