package net.conselldemallorca.helium.webapp.v3.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.DominiService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusDominiCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per al manteniment de les dominis a nivell d'entorn.
 *
 */
@Controller(value = "dominiControllerV3")
@RequestMapping("/v3/domini")
public class DominiController extends BaseDissenyController {
	
	@Autowired
	private DominiService dominiService;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	/** Accés al llistat de dominins de l'entorn des del menú de disseny. */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			return "v3/dominiLlistat";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}
	}
	
	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				dominiService.findPerDatatable(
						entornActual.getId(),
						null, // expedientTipusId
						true, // incloure globals
						paginacioParams.getFiltre(),
						paginacioParams));
	}	
	
	/** Formulari per crear un nou domini. */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String nou(
			HttpServletRequest request, 
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			ExpedientTipusDominiCommand command = new ExpedientTipusDominiCommand();
			model.addAttribute("expedientTipusDominiCommand", command);
			return "v3/expedientTipusDominiForm";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String nouPost(
			HttpServletRequest request, 
			@Validated(ExpedientTipusDominiCommand.Creacio.class) ExpedientTipusDominiCommand command,
			BindingResult bindingResult, Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			try {
				if (bindingResult.hasErrors()) {
					return "v3/expedientTipusDominiForm";
				} else {
				
					EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
					
					dominiService.create(
							entornActual.getId(), 
							null, //expedientTipusId 
							conversioTipusHelper.convertir(
									command, 
									DominiDto.class));
					
		    		MissatgesHelper.success(
							request, 
							getMessage(
									request, 
									"expedient.tipus.domini.controller.creat"));
				}
			} catch (Exception ex) {
				MissatgesHelper.error(request, 
						getMessage(
								request, 
								"expedient.tipus.domini.controller.creat.error",
								new Object[] {ex.getLocalizedMessage()}));
				logger.error("No s'ha pogut guardar l'enumeració", ex);
				return "v3/expedientTipusDominiForm";
		    }
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
		}
		return modalUrlTancar(true);
	}	
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String modificar(
			HttpServletRequest request, 
			@PathVariable Long id,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			DominiDto dto = dominiService.findAmbId(null, id);
			ExpedientTipusDominiCommand command = conversioTipusHelper.convertir(dto, ExpedientTipusDominiCommand.class);
			model.addAttribute("expedientTipusDominiCommand", command);
			model.addAttribute("heretat", dto.isHeretat());
			return "v3/expedientTipusDominiForm";
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}
		
	}

	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String modificarPost(
			HttpServletRequest request, 
			@PathVariable Long id,
			@Validated(ExpedientTipusDominiCommand.Modificacio.class) ExpedientTipusDominiCommand command,
			BindingResult bindingResult, Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			try {
				if (bindingResult.hasErrors()) {
		    		model.addAttribute("heretat", dominiService.findAmbId(null, id).isHeretat());
					return "v3/expedientTipusDominiForm";
				} else {
					dominiService.update(
							conversioTipusHelper.convertir(
								command, 
								DominiDto.class));
					
		    		MissatgesHelper.success(
							request, 
							getMessage(
									request, 
									"expedient.tipus.domini.controller.modificat"));
					return modalUrlTancar(true);
				}
			} catch (Exception ex) {
				MissatgesHelper.error(request, 
						getMessage(
								request, 
								"expedient.tipus.domini.controller.creat.error",
								new Object[] {ex.getLocalizedMessage()}));
				logger.error("No s'ha pogut guardar l'enumerat: " + id, ex);
	    		model.addAttribute("heretat", dominiService.findAmbId(null, id).isHeretat());
				return "v3/expedientTipusDominiForm";
		    }
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.permis.disseny.entorn"));
			return "redirect:/v3";
		}		
	}	

	
	/** Mètode per esborrar un domini. */
	@RequestMapping(value = "/{dominiId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long dominiId,
			Model model) {
		if (SessionHelper.getSessionManager(request).getPotDissenyarEntorn()) {
			try {
				dominiService.delete(dominiId);
				MissatgesHelper.success(request, getMessage(request, "expedient.tipus.domini.controller.eliminat"));
			}catch (ValidacioException ex) {
				MissatgesHelper.error(request, ex.getMessage());
			}
			return modalUrlTancar(false);
		} else {
			return "redirect:/v3";
		}
	}	

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	private static final Log logger = LogFactory.getLog(DominiController.class);
}
