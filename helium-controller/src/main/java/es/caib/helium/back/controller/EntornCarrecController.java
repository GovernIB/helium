package es.caib.helium.back.controller;

import es.caib.helium.back.command.EntornCarrecCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.CarrecDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.EntornAreaService;
import es.caib.helium.logic.intf.service.EntornCarrecService;
import es.caib.helium.logic.intf.service.EntornService;
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
 * Controlador per a la gestió de càrrecs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Controller(value = "entornCarrecControllerV3")
@RequestMapping("/v3/entorn-carrec")
public class EntornCarrecController extends BaseController {

	@Autowired
	private EntornCarrecService entornCarrecService;
	@Autowired
	private EntornAreaService entornAreaService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntornService entornService;

	@RequestMapping(method = RequestMethod.GET)
	public String llistat(HttpServletRequest request, Model model) {
		return "v3/entornCarrec";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(HttpServletRequest request, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornService.findAmbIdPermisAcces(entornActual.getId()) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return DatatablesHelper.getEmptyDatatableResponse(request);
		}
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, entornCarrecService.findPerDatatable(paginacioParams));
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornService.findAmbIdPermisAcces(entornActual.getId()) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
		model.addAttribute(new EntornCarrecCommand());
		return "v3/entornCarrecForm";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, @Validated(EntornCarrecCommand.Creacio.class) EntornCarrecCommand command,
			BindingResult bindingResult, Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornService.findAmbIdPermisAcces(entornActual.getId()) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
			return "v3/entornCarrecForm";
		}

		entornCarrecService.create(entornActual.getId(), conversioTipusHelper.convertir(command, CarrecDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/v3/entorn-carrec", "carrec.controller.creat");
	}

	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(HttpServletRequest request, @PathVariable Long id, Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornService.findAmbIdPermisAcces(entornActual.getId()) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		CarrecDto dto = entornCarrecService.findAmbId(entornActual.getId(), id);
		model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
		EntornCarrecCommand command = conversioTipusHelper.convertir(dto, EntornCarrecCommand.class);

		model.addAttribute("entornCarrecCommand", command);
		return "v3/entornCarrecForm";
	}
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request,
			@PathVariable Long id,
			@Validated(EntornCarrecCommand.Modificacio.class) EntornCarrecCommand command,
			BindingResult bindingResult,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornService.findAmbIdPermisAcces(entornActual.getId()) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
        if (bindingResult.hasErrors()) {
    		model.addAttribute("entornArees", entornAreaService.findAreesByEntorn(entornActual.getId()));
        	return "v3/entornCarrecForm";
        } 
        
    	entornCarrecService.update(entornActual.getId(), conversioTipusHelper.convertir(command, CarrecDto.class));
		return getModalControllerReturnValueSuccess(request, "redirect:/v3/entorn-carrec", "carrec.controller.modificat");
	}

	@RequestMapping(value = "{entornCarrecId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long entornCarrecId, Model model) {
		entornCarrecService.delete(entornCarrecId);
		return this.getAjaxControllerReturnValueSuccess(request, "redirect:/v3/entorn-carrec", "carrec.controller.esborrat");
	}
}
