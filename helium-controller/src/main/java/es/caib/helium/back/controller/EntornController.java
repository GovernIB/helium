package es.caib.helium.back.controller;

import es.caib.helium.back.command.EntornCommand;
import es.caib.helium.back.command.PermisCommand;
import es.caib.helium.back.helper.ConversioTipusHelper;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PermisDto;
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
import javax.validation.Valid;

/**
 * Controlador per al manteniment d'entorns.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/entorn")
public class EntornController extends BaseController {

	@Autowired
	private EntornService entornService;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;


	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		return "v3/entornLlistat";
	}

	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				entornService.findPerDatatable(
						paginacioParams.getFiltre(),
						paginacioParams));
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new EntornCommand());
		return "v3/entornForm";
	}
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newPost(
			HttpServletRequest request,
			@Validated(EntornCommand.Creacio.class) EntornCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/entornForm";
        } else {
    		entornService.create(
    				conversioTipusHelper.convertir(
    						command,
    						EntornDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/entorn",
					"entorn.controller.creat");
        }
	}

	@RequestMapping(value = "/{entornId}/update", method = RequestMethod.GET)
	public String updateGet(
			HttpServletRequest request,
			@PathVariable Long entornId,
			Model model) {
		EntornDto dto = entornService.findOne(
				entornId);
		model.addAttribute(
				conversioTipusHelper.convertir(
						dto,
						EntornCommand.class));
		return "v3/entornForm";
	}
	@RequestMapping(value = "/{entornId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@PathVariable Long entornId,
			@Validated(EntornCommand.Modificacio.class) EntornCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/entornForm";
        } else {
        	command.setId(entornId);
        	entornService.update(
        			conversioTipusHelper.convertir(
    						command,
    						EntornDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/entorn",
					"entorn.controller.modificat");
        }
	}

	@RequestMapping(value = "/{entornId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long entornId,
			Model model) {
		entornService.delete(entornId);
		return this.getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/v3/entorn",
				"entorn.controller.esborrat");
	}

	@RequestMapping(value = "/{entornId}/permis", method = RequestMethod.GET)
	public String permisGet(
			HttpServletRequest request,
			@PathVariable Long entornId,
			Model model) {
		model.addAttribute(
				"entorn",
				entornService.findOne(
						entornId));
		return "v3/entornPermis";
	}

	@RequestMapping(value = "/{entornId}/permis/new", method = RequestMethod.GET)
	public String permisNewGet(
			HttpServletRequest request,
			@PathVariable Long entornId,
			Model model) {
		model.addAttribute(
				"entorn",
				entornService.findOne(
						entornId));
		model.addAttribute(new PermisCommand());
		return "v3/entornPermisForm";
	}
	@RequestMapping(value = "/{entornId}/permis/new", method = RequestMethod.POST)
	public String permisNewPost(
			HttpServletRequest request,
			@PathVariable Long entornId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		return permisUpdatePost(
				request,
				entornId,
				null,
				command,
				bindingResult,
				model);
	}
	@RequestMapping(value = "/{entornId}/permis/{permisId}", method = RequestMethod.GET)
	public String permisUpdateGet(
			HttpServletRequest request,
			@PathVariable Long entornId,
			@PathVariable Long permisId,
			Model model) {
		model.addAttribute(
				"entorn",
				entornService.findOne(
						entornId));
		PermisDto permis = entornService.permisFindById(
				entornId,
				permisId);
		model.addAttribute(
				conversioTipusHelper.convertir(
						permis,
						PermisCommand.class));
		return "v3/entornPermisForm";
	}
	@RequestMapping(value = "/{entornId}/permis/{permisId}", method = RequestMethod.POST)
	public String permisUpdatePost(
			HttpServletRequest request,
			@PathVariable Long entornId,
			@PathVariable Long permisId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute(
    				"entorn",
    				entornService.findOne(
    						entornId));
        	return "v3/entornPermisForm";
        } else {
    		entornService.permisUpdate(
    				entornId,
    				conversioTipusHelper.convertir(
    						command,
    						PermisDto.class));
    		    		
    		MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"entorn.controller.permis.actualitzat"));
			return modalUrlTancar();
		}
	}

	@RequestMapping(value = "/{entornId}/permis/{permisId}/delete")
	public String permisDelete(
			HttpServletRequest request,
			@PathVariable Long entornId,
			@PathVariable Long permisId,
			Model model) {
		
		entornService.permisDelete(
				entornId,
				permisId);
		
		model.addAttribute(
				"entorn",
				entornService.findOne(
						entornId));
		model.addAttribute(new PermisCommand());
		return "redirect:/v3/entorn/" + entornId + "/permis";
	}

	@RequestMapping(value = "/{entornId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse permisDatatable(
			HttpServletRequest request,
			@PathVariable Long entornId,
			Model model) {
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				entornService.permisFindAll(
						entornId));
	}

}
