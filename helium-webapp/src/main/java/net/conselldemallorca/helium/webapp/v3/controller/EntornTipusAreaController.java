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

import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornTipusAreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.EntornTipusAreaService;
import net.conselldemallorca.helium.webapp.v3.command.EntornTipusAreaCommand;
import net.conselldemallorca.helium.webapp.v3.command.EntornTipusAreaCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.EntornTipusAreaCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la gestió de tipus d'àrea
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Controller(value = "entornTipusAreaControllerV3")
@RequestMapping("/v3/entorn-tipus-area")
public class EntornTipusAreaController extends BaseController {

	@Autowired
	private EntornTipusAreaService entornTipusAreaService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntornHelper entornHelper;

	@RequestMapping(method = RequestMethod.GET)
	public String llistat(HttpServletRequest request, Model model) {
		return "v3/entornTipusArea";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(HttpServletRequest request, Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return DatatablesHelper.getEmptyDatatableResponse(request);
		}

		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, entornTipusAreaService.findPerDatatable(paginacioParams));
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, Model model) {
		
		model.addAttribute(new EntornTipusAreaCommand());
		return "v3/entornTipusAreaForm";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, @Validated(Creacio.class) EntornTipusAreaCommand command,
			BindingResult bindingResult, Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		if (bindingResult.hasErrors()) {
			return "v3/entornTipusAreaForm";
		}

		entornTipusAreaService.create(entornActual.getId(), conversioTipusHelper.convertir(command, EntornTipusAreaDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/v3/entorn-tipus-area", "tipusArea.controller.creat");
	}

	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(HttpServletRequest request, @PathVariable Long id, Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		EntornTipusAreaDto dto = entornTipusAreaService.findAmbId(entornActual.getId(), id);
		EntornTipusAreaCommand command = conversioTipusHelper.convertir(dto, EntornTipusAreaCommand.class);

		model.addAttribute("entornTipusAreaCommand", command);
		return "v3/entornTipusAreaForm";
	}
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long id,
			@Validated(Modificacio.class) EntornTipusAreaCommand command,
			BindingResult bindingResult,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
        if (bindingResult.hasErrors()) {
        	return "v3/entornTipusAreaForm";
        } 
    		
    	entornTipusAreaService.update(
    			entornActual.getId(),
    			conversioTipusHelper.convertir(
						command,
						EntornTipusAreaDto.class));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/v3/entorn-tipus-area",
				"tipusArea.controller.modificat");
	}

	@RequestMapping(value = "{entornTipusAreaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long entornTipusAreaId, Model model) {
		
		entornTipusAreaService.delete(entornTipusAreaId);
		return this.getAjaxControllerReturnValueSuccess(request, "redirect:/v3/entorn-tipus-area",
				"tipusArea.controller.esborrada");
	}
}
