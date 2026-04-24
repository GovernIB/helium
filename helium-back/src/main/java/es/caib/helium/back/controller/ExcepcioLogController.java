package es.caib.helium.back.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.UsuariActualHelper;
import es.caib.helium.commons.dto.ExcepcioLogDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AplicacioService;

@Controller
@RequestMapping("/excepcions")
public class ExcepcioLogController extends BaseController {

	@Autowired private AplicacioService aplicacioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		if (UsuariActualHelper.isAdministrador(SecurityContextHolder.getContext().getAuthentication())) {
			return "excepcio";
		} else {
			MissatgesHelper.error(request, "Es requereix el rol d'administrador per consultar el registre d'excepcions.");
			return "redirect:/";
		}
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {

		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		List<ExcepcioLogDto> resultat = aplicacioService.excepcioFindAll();
		List<ExcepcioLogDto> llistaFiltrada = new ArrayList<ExcepcioLogDto>();

		boolean filtreAlpicat = false;
		if (paginacioParams!=null) {

			//FILTRE
			if (paginacioParams.getFiltre()!=null && !"".equals(paginacioParams.getFiltre())) {
				filtreAlpicat = true;
				for (ExcepcioLogDto aux: resultat) {
					if ((aux.getPeticio()!=null && aux.getPeticio().contains(paginacioParams.getFiltre())) ||
						(aux.getParams()!=null && aux.getParams().contains(paginacioParams.getFiltre())) ||
						(aux.getObjectClass()!=null && aux.getObjectClass().toString().contains(paginacioParams.getFiltre())) ||
						(aux.getMessage()!=null && aux.getMessage().contains(paginacioParams.getFiltre())) ){
							llistaFiltrada.add(aux);
					}
				}
			}

			if (!filtreAlpicat) { llistaFiltrada = resultat; }
		}

		if (filtreAlpicat) {
			return DatatablesHelper.getDatatableResponse(request, null, llistaFiltrada);
		} else {
			return DatatablesHelper.getDatatableResponse(request, null, resultat);
		}
	}

	@RequestMapping(value = "/{index}", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request,
			@PathVariable Long index,
			Model model) {
		model.addAttribute("excepcio", aplicacioService.excepcioFindOne(index));
		return "excepcioDetall";
	}

}
