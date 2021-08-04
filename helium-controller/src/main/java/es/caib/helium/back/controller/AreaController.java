package es.caib.helium.back.controller;

import es.caib.helium.back.command.AreaCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.logic.intf.dto.AreaJbpmIdDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador per a la gestió de d'arees
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Controller
@RequestMapping("/v3/area")
public class AreaController extends BaseController {
	
	@Autowired
	private AreaService areaService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		return "v3/areaPipelles";
	}
	
	@RequestMapping(value = "/configurades", method = RequestMethod.GET)
	public String configurades(
			HttpServletRequest request,
			Model model) {
		return "v3/areaConfigurades";
	}

	@RequestMapping(value = "/configurades/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				areaService.findConfigurades(paginacioParams));
	}

	@RequestMapping(value = "/sense/configurar", method = RequestMethod.GET)
	public String senseConfigurar(
			HttpServletRequest request,
			Model model) {
		return "v3/areaSenseConfigurar";
	}

	@RequestMapping(value = "/sense/configurar/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatableSenseConfigurar(
			HttpServletRequest request,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				areaService.findSenseConfigurar(paginacioParams));
	}
	
	@RequestMapping(value = "{codi}/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, @PathVariable String codi, Model model) {
	
		AreaCommand command = new AreaCommand();
		command.setCodi(codi);
		model.addAttribute("areaCommand", command);
		return "v3/areaConfigurarForm";
	}
	
	@RequestMapping(value = "{codi}/new", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, @Validated(AreaCommand.Creacio.class) AreaCommand command,
			BindingResult bindingResult, Model model) {
		
		if (bindingResult.hasErrors()) {
			return "v3/areaConfigurarForm";
		}
		
		areaService.create(conversioTipusHelper.convertir(command, AreaJbpmIdDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/v3/area", "area.controller.creada");
	}
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(HttpServletRequest request, @PathVariable Long id, Model model) {

		AreaJbpmIdDto dto = areaService.findAmbId(id);
		AreaCommand command = conversioTipusHelper.convertir(dto, AreaCommand.class);
		model.addAttribute("areaCommand", command);
		return "v3/areaConfigurarForm";
	}
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long id,
			@Validated(AreaCommand.Modificacio.class) AreaCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/areaConfigurarForm";
        } 
        
    	areaService.update(conversioTipusHelper.convertir(command, AreaJbpmIdDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/v3/area", "area.controller.modificada");
	}
	
	@RequestMapping(value ="{areaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request,
			@PathVariable Long areaId,
			Model model) {
		areaService.delete(areaId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/v3/area",
				"area.controller.esborrada");
	}
		
}
