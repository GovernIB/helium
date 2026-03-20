package es.caib.helium.back.controller;

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

import es.caib.helium.back.command.EntornTipusAreaCommand;
import es.caib.helium.back.command.EntornTipusAreaCommand.Creacio;
import es.caib.helium.back.command.EntornTipusAreaCommand.Modificacio;
import es.caib.helium.back.helper.ConversioTipus;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.EntornTipusAreaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornTipusAreaService;
import es.caib.helium.service.helper.EntornHelper;

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

		entornTipusAreaService.create(entornActual.getId(), ConversioTipus.convertir(command, EntornTipusAreaDto.class));
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
		EntornTipusAreaCommand command = ConversioTipus.convertir(dto, EntornTipusAreaCommand.class);

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
    			ConversioTipus.convertir(
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
