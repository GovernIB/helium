package net.conselldemallorca.helium.webapp.v3.controller;

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

import net.conselldemallorca.helium.v3.core.api.dto.AvisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.AvisService;
import net.conselldemallorca.helium.webapp.v3.command.AvisCommand;
import net.conselldemallorca.helium.webapp.v3.command.AvisCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.command.AvisCommand.Modificacio;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment de avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "avisControllerV3")
@RequestMapping("/v3/avis")
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
					return "v3/avis";

		}
		return "v3/avis";
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
		return "v3/avisForm";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newPost(
			HttpServletRequest request,
			@Validated(Creacio.class) AvisCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/avisForm";
        } else {
    		avisService.create(
    				ConversioTipusHelper.convertir(
    						command,
    						AvisDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/avis",
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
				ConversioTipusHelper.convertir(
						dto,
						AvisCommand.class));
		return "v3/avisForm";
	}
	
	@RequestMapping(value = "/{avisId}/update", method = RequestMethod.POST)
	public String updatePost(
			HttpServletRequest request,
			@PathVariable Long avisId,
			@Validated(Modificacio.class) AvisCommand command,
			BindingResult bindingResult,
			Model model) {
        if (bindingResult.hasErrors()) {
        	return "v3/avisForm";
        } else {
        	command.setId(avisId);
        	avisService.update(
        			ConversioTipusHelper.convertir(
    						command,
    						AvisDto.class));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/v3/avis",
					"avis.controller.modificat");
        }
	}
	
//	@RequestMapping(method = RequestMethod.POST)
//	public String save(
//			HttpServletRequest request,
//			@Valid AvisCommand command,
//			BindingResult bindingResult) {
//		if (bindingResult.hasErrors()) {
//			return "v3/avisForm";
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
