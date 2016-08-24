package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment de les definicions de procés. Controla les pipelles del
 * detall i dels recursos. La pipella de tasques la serveix el controlador {@link DefinicioProcesTascaController}
 *
 */
@Controller(value = "definicioProcesControllerV3")
@RequestMapping("/v3/definicioProces")
public class DefinicioProcesController extends BaseDefinicioProcesController {
	
	/** Vista de les pipelles per a la definició de procés. */
	@RequestMapping(value = "/{jbmpKey}", method = RequestMethod.GET)
	public String pipelles(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			Model model) {		
		return mostrarInformacioDefinicioProcesPerPipelles(
				request,
				jbmpKey,
				model,
				"detall");
	}
	
	/** Pipella del detall. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/detall")
	public String detall(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioDefinicioProcesPerPipelles(
					request,
					jbmpKey,
					model,
					"detall");
		}
		model.addAttribute("jbpmKey", jbmpKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = null;
		if (entornActual != null) {
			definicioProces = definicioProcesService.findById(definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			if (definicioProces != null) {
				model.addAttribute("subDefinicionsProces", 
						definicioProcesService.findSubDefinicionsProces(definicioProcesId));				
			}
		}		
		return "v3/definicioProcesDetall";
	}
	
	/** Pipella dels recursos. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/recurs")
	public String recurs(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioDefinicioProcesPerPipelles(
					request,
					jbmpKey,
					model,
					"recurs");
		}
		model.addAttribute("jbpmKey", jbmpKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = null;
		if (entornActual != null) {
			definicioProces = definicioProcesService.findById(definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
			if (definicioProces != null) {
				// Llistat de recursos
				Set<String> recursos = dissenyService.getRecursosNom(definicioProcesId);
				model.addAttribute("recursos", recursos);
			}
		}		
		return "v3/definicioProcesRecurs";
	}

	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/recurs/descarregar")
	public String recursDescarregar(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@RequestParam String nom,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		DefinicioProcesDto definicioProces = null;
		if (entornActual != null) {
			definicioProces = definicioProcesService.findById(definicioProcesId);
			if (definicioProces != null) {
				model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME,nom);
				model.addAttribute(
						ArxiuView.MODEL_ATTRIBUTE_DATA, 
						dissenyService.getRecursContingut(
								definicioProcesId, 
								nom));
			}
		}		
		return "arxiuView";
	}	
}
