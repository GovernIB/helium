package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParellaCodiValorDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pestanya de tasques del disseny de les definicions de procés.
 *
 */
@Controller(value = "definicioProcesVariableControllerV3")
@RequestMapping("/v3/definicioProces")
public class DefinicioProcesVariableController extends BaseDefinicioProcesController {
	
	@Autowired
	ExpedientTipusService expedientTipusService;
	
	/** Pipella del tasques. */
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variables")
	public String variables(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			Model model) {
		if (!NodecoHelper.isNodeco(request)) {
			return mostrarInformacioDefinicioProcesPerPipelles(
					request,
					jbmpKey,
					model,
					"variables");
		}
		
		omplirModelVariablesPestanya(
				request,
				definicioProcesId,
				model);
		
		model.addAttribute("jbpmKey", jbmpKey);
		model.addAttribute("definicioProcesId", definicioProcesId);
				
		return "v3/expedientTipusVariable";
	}	
	
	@RequestMapping(value = "/{jbmpKey}/{definicioProcesId}/variables/datatable")
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable String jbmpKey,
			@PathVariable Long definicioProcesId,
			@RequestParam(required = false) Long agrupacioId,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				definicioProcesService.campFindPerDatatable(
						entornActual.getId(),
						definicioProcesId,
						agrupacioId,
						paginacioParams.getFiltre(),
						paginacioParams),
				"id");
	}	
	
	
	
	private void omplirModelVariablesPestanya(
			HttpServletRequest request,
			Long definicioProcesId,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual != null) {
			DefinicioProcesDto definicioProces = definicioProcesService.findAmbIdAndEntorn(entornActual.getId(), definicioProcesId);
			model.addAttribute("definicioProces", definicioProces);
		}
		this.omplirModelAgrupacions(
				request, 
				definicioProcesId, 
				model);
	}
	
	private void omplirModelAgrupacions(
			HttpServletRequest request,
			Long definicioProcesId,
			Model model) {
		model.addAttribute("agrupacions", obtenirParellesAgrupacions(definicioProcesId));		
	}
	
	private List<ParellaCodiValorDto> obtenirParellesAgrupacions(Long definicioProcesId) {
		List<CampAgrupacioDto> agrupacions = expedientTipusService.agrupacioFindAll(expedientTipusId);
		List<ParellaCodiValorDto> resposta = new ArrayList<ParellaCodiValorDto>();
		for (CampAgrupacioDto agrupacio : agrupacions) {
			resposta.add(new ParellaCodiValorDto(agrupacio.getId().toString(), agrupacio.getNom()));
		}
		return resposta;
	}
	
}
