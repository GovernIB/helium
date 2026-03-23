package es.caib.helium.back.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.back.command.AvisCommand;
import es.caib.helium.back.command.AvisCommand.Creacio;
import es.caib.helium.back.command.AvisCommand.Modificacio;
import es.caib.helium.back.helper.ConversioTipus;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.AvisDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AvisService;

/**
 * Controlador per al manteniment de avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "avisControllerV3")
@RequestMapping("/avis")
public class AvisController extends BaseController {
	
	@Autowired
	private AvisService avisService;

	private static final String SESSION_ATTRIBUTE_FILTRE = "AvisController.session.filtre";
	
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		AvisCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		// Valida l'accés a l'entorn
		Boolean potDissenyarAvisos = SessionHelper.getSessionManager(request).getPotDissenyarAvisos();
		if (potDissenyarAvisos != null && !potDissenyarAvisos) {
					MissatgesHelper.error(
							request,
							getMessage(
									request,
									"error.permis.disseny.avis"));
					return "avis";

		}
		return "avis";
	}
	
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);

		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				avisService.findPaginat(
						paginacioParams));
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(Model model) {
		return get(null, model);
	}
	@RequestMapping(value = "/{avisId}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long avisId,
			Model model) {
		AvisDto avis = null;
		if (avisId != null)
			avis = avisService.findById(avisId);
		if (avis != null) {
			model.addAttribute(AvisCommand.asCommand(avis));
		} else {
			AvisCommand avisCommand = new AvisCommand();
			avisCommand.setDataInici(new Date());
			model.addAttribute(avisCommand);
		}
		return "avisForm";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newPost(
			HttpServletRequest request,
			@Validated(Creacio.class) AvisCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "avisForm";
        } else {
    		avisService.create(
    				ConversioTipus.convertir(
    						command,
    						AvisDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/avis",
					"avis.controller.creat");
        }
	}
	
	@RequestMapping(value = "/{avisId}/update", method = RequestMethod.GET)
	public String updateGet(
			HttpServletRequest request,
			@PathVariable Long avisId,
			Model model) {
		AvisDto dto = avisService.findById(
				avisId);
		model.addAttribute(
				ConversioTipus.convertir(
						dto,
						AvisCommand.class));
		return "avisForm";
	}
	
	@RequestMapping(value = "/{avisId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@PathVariable Long avisId,
			@Validated(Modificacio.class) AvisCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "avisForm";
        } else {
        	command.setId(avisId);
        	avisService.update(
        			ConversioTipus.convertir(
    						command,
    						AvisDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/avis",
					"avis.controller.modificat");
        }
	}
	
//	@RequestMapping(method = RequestMethod.POST)
//	public String save(
//			HttpServletRequest request,
//			@Valid AvisCommand command,
//			BindingResult bindingResult) {
//		if (bindingResult.hasErrors()) {
//			return "avisForm";
//		}
//		if (command.getId() != null) {
//			avisService.update(AvisCommand.asDto(command));
//			return getModalControllerReturnValueSuccess(
//					request,
//					"redirect:avis",
//					"avis.controller.modificat.ok");
//		} else {
//			avisService.create(AvisCommand.asDto(command));
//			return getModalControllerReturnValueSuccess(
//					request,
//					"redirect:avis",
//					"avis.controller.creat.ok");
//		}
//	}

	@RequestMapping(value = "/{avisId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long avisId) {
		avisService.updateActiva(avisId, true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../avis",
				"avis.controller.activat.ok");
	}
	@RequestMapping(value = "/{avisId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long avisId) {
		avisService.updateActiva(avisId, false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../avis",
				"avis.controller.desactivat.ok");
	}

	@RequestMapping(value = "/{avisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long avisId) {
		avisService.delete(avisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../avis",
				"avis.controller.esborrat.ok");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
	/** Mètode per obtenir o inicialitzar el filtre del formulari de cerca.
	 * 
	 * @param request
	 * @return
	 */
	private AvisCommand getFiltreCommand(
			HttpServletRequest request) {
		AvisCommand filtreCommand = (AvisCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new AvisCommand();
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
}
