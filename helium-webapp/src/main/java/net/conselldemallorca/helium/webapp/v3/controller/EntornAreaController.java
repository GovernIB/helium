package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.logic.intf.dto.EntornAreaDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornAreaService;
import es.caib.helium.logic.intf.service.EntornTipusAreaService;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.webapp.v3.command.EntornAreaCommand;
import net.conselldemallorca.helium.webapp.v3.command.EntornAreaCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.EntornAreaCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la gestió de les àrees
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Controller(value = "entornAreaControllerV3")
@RequestMapping("/v3/entorn-area")
public class EntornAreaController extends BaseController {
	
	@Autowired
	private EntornAreaService entornAreaService;
	@Autowired
	private EntornTipusAreaService entornTipusAreaService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntornHelper entornHelper;

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
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		model.addAttribute("entornTipusArea", entornTipusAreaService.findTipusAreaByEntorn(entornActual.getId()));
		model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
		model.addAttribute(new EntornAreaCommand());
		return "v3/entornAreaForm";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, @Validated(Creacio.class) EntornAreaCommand command,
			BindingResult bindingResult, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
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
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
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
			@Validated(Modificacio.class) EntornAreaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
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
