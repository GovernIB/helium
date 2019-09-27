package net.conselldemallorca.helium.webapp.v3.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.webapp.v3.command.AnotacioFiltreCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.PaginacioHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per visualitzar la llista d'anotacions que han arribat a Helium
 * com a Backoffice de Distribució amb la possibilitat d'acceptar o rebutjar anotacions.
 *
 */
@Controller
@RequestMapping("/v3/anotacio")
public class AnotacioController extends BaseDissenyController {
	
	@Autowired
	private AnotacioService anotacioService;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	private static final String SESSION_ATTRIBUTE_FILTRE = "AnotacioController.session.filtre";

	
	/** Accés al llistat d'anotacions des de l'opció a la capçalera per ususaris amb permís de relacionar expedients. */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		AnotacioFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		return "v3/anotacioLlistat";
	}
	
	/** Mètode quan s'envia el formulari del filtre. Actualitza el filtre en sessió. */
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid AnotacioFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:anotacio";
	}
	
	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		AnotacioFiltreCommand filtreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
					request,
					null,
					anotacioService.findAmbFiltrePaginat(
							entornActual.getId(),
							conversioTipusHelper.convertir(filtreCommand, AnotacioFiltreDto.class),
							DatatablesHelper.getPaginacioDtoFromRequest(request)));		
	}

	/** Mètode per obtenir o inicialitzar el filtre del formulari de cerca.
	 * 
	 * @param request
	 * @return
	 */
	private AnotacioFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		AnotacioFiltreCommand filtreCommand = (AnotacioFiltreCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new AnotacioFiltreCommand();
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	private static final Log logger = LogFactory.getLog(AnotacioController.class);
}
