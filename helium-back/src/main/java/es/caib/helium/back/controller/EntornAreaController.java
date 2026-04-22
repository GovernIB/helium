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

import es.caib.helium.back.command.EntornAreaCommand;
import es.caib.helium.back.command.EntornAreaCommand.Creacio;
import es.caib.helium.back.command.EntornAreaCommand.Modificacio;
import es.caib.helium.back.helper.ConversioTipus;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.EntornAreaDto;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornAreaService;
import es.caib.helium.logic.intf.service.EntornTipusAreaService;
import es.caib.helium.logic.helper.EntornHelper;

/**
 * Controlador per a la gestió de les àrees
 *
 * @author Limit Tecnologies <limit@limit.es>
 */

@Controller(value = "entornAreaControllerV3")
@RequestMapping("/entorn-area")
public class EntornAreaController extends BaseController {

	@Autowired
	private EntornAreaService entornAreaService;
	@Autowired
	private EntornTipusAreaService entornTipusAreaService;
	@Autowired
	private EntornHelper entornHelper;

	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		return "entornArea";
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
			return "modalBlank";
		}
		model.addAttribute("entornTipusArea", entornTipusAreaService.findTipusAreaByEntorn(entornActual.getId()));
		model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
		model.addAttribute(new EntornAreaCommand());
		return "entornAreaForm";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, @Validated(Creacio.class) EntornAreaCommand command,
			BindingResult bindingResult, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "modalBlank";
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("entornTipusArea", entornTipusAreaService.findTipusAreaByEntorn(entornActual.getId()));
			model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
			return "entornAreaForm";
		}

		entornAreaService.create(entornActual.getId(), ConversioTipus.convertir(command, EntornAreaDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/entorn-area", "area.controller.creada");
	}

	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(HttpServletRequest request, @PathVariable Long id, Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "modalBlank";
		}
		EntornAreaDto dto = entornAreaService.findAmbId(entornActual.getId(), id);
		dto.setTipusId(dto.getTipus().getId());
		if (dto.getPare() != null) {
			dto.setPareId(dto.getPare().getId());
		}
		model.addAttribute("entornTipusArea", entornTipusAreaService.findTipusAreaByEntorn(entornActual.getId()));
		model.addAttribute("entornArees", entornAreaService.findPossiblesParesByEntorn(entornActual.getId(), id));
		EntornAreaCommand command = ConversioTipus.convertir(dto, EntornAreaCommand.class);

		model.addAttribute("entornAreaCommand", command);
		return "entornAreaForm";
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
			return "modalBlank";
		}
        if (bindingResult.hasErrors()) {
        	model.addAttribute("entornTipusArea", entornTipusAreaService.findTipusAreaByEntorn(entornActual.getId()));
			model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
        	return "entornAreaForm";
        }

    	entornAreaService.update(entornActual.getId(), ConversioTipus.convertir(command, EntornAreaDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/entorn-area", "area.controller.modificada");
	}

	@RequestMapping(value = "{entornAreaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request,
			@PathVariable Long entornAreaId,
			Model model) {
		entornAreaService.delete(entornAreaId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/entorn-area",
				"area.controller.esborrada");
	}

}
