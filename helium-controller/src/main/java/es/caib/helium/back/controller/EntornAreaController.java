package es.caib.helium.back.controller;

import es.caib.helium.back.command.EntornAreaCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornAreaDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornAreaService;
import es.caib.helium.logic.intf.service.EntornService;
import es.caib.helium.logic.intf.service.EntornTipusAreaService;
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
 * Controlador per a la gestió de les àrees
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Controller
@RequestMapping("/v3/entorn-area")
public class EntornAreaController extends BaseController {

	@Autowired
	EntornService entornService;
	@Autowired
	private EntornAreaService entornAreaService;
	@Autowired
	private EntornTipusAreaService entornTipusAreaService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		return "v3/entornArea";
	}
	
	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return DatatablesHelper.getEmptyDatatableResponse(request);
		}
		
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, entornAreaService.findPerDatatable(paginacioParams));
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornService.findAmbIdPermisAcces(entornActual.getId()) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		model.addAttribute("entornTipusArea", entornTipusAreaService.findTipusAreaByEntorn(entornActual.getId()));
		model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
		model.addAttribute(new EntornAreaCommand());
		return "v3/entornAreaForm";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, @Validated(EntornAreaCommand.Creacio.class) EntornAreaCommand command,
			BindingResult bindingResult, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornService.findAmbIdPermisAcces(entornActual.getId()) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("entornTipusArea", entornTipusAreaService.findTipusAreaByEntorn(entornActual.getId()));
			model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
			return "v3/entornAreaForm";
		}

		entornAreaService.create(entornActual.getId(), conversioTipusHelper.convertir(command, EntornAreaDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/v3/entorn-area", "area.controller.creada");
	}

	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(HttpServletRequest request, @PathVariable Long id, Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornService.findAmbIdPermisAcces(entornActual.getId()) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		EntornAreaDto dto = entornAreaService.findAmbId(entornActual.getId(), id);
		dto.setTipusId(dto.getTipus().getId());
		if (dto.getPare() != null) { 
			dto.setPareId(dto.getPare().getId());
		}
		model.addAttribute("entornTipusArea", entornTipusAreaService.findTipusAreaByEntorn(entornActual.getId()));
		model.addAttribute("entornArees", entornAreaService.findPossiblesParesByEntorn(entornActual.getId(), id));
		EntornAreaCommand command = conversioTipusHelper.convertir(dto, EntornAreaCommand.class);

		model.addAttribute("entornAreaCommand", command);
		return "v3/entornAreaForm";
	}
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long id,
			@Validated(EntornAreaCommand.Modificacio.class) EntornAreaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornService.findAmbIdPermisAcces(entornActual.getId()) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
        if (bindingResult.hasErrors()) {
        	model.addAttribute("entornTipusArea", entornTipusAreaService.findTipusAreaByEntorn(entornActual.getId()));
			model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
        	return "v3/entornAreaForm";
        } 
        
    	entornAreaService.update(entornActual.getId(), conversioTipusHelper.convertir(command, EntornAreaDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/v3/entorn-area", "area.controller.modificada");
	}
	
	@RequestMapping(value = "{entornAreaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request,
			@PathVariable Long entornAreaId,
			Model model) {
		entornAreaService.delete(entornAreaId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/v3/entorn-area",
				"area.controller.esborrada");
	}
	
}
