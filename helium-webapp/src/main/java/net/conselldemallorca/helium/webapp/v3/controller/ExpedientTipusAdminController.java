package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientTipusAdminCommand;
import net.conselldemallorca.helium.webapp.v3.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al menú Administrador de cercador de tipologies de tipus d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "expedientTipusAdminControllerV3")
@RequestMapping("/v3/cercadorTipologies")
public class ExpedientTipusAdminController extends BaseController {

	@Resource
	private ExpedientTipusService expedientTipusService;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	private static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientTipusAdminController.session.filtre";

	/** Resposta GET i POST de la pàgina i formulari de tipologies.
	 * 
	 * @param request
	 * @param model
	 * @return Retorna com si fos una crida post de consulta.
	 */
	
	/** Accés al llistat de tipologies des del desplegable Administració - Cercador de Tipologies */
	@RequestMapping(method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			Model model) {
		ExpedientTipusAdminCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		// Valida l'accés a l'entorn
		Boolean potAdministrarEntorn = SessionHelper.getSessionManager(request).getPotAdministrarEntorn();
		if (potAdministrarEntorn != null && !potAdministrarEntorn) {
					MissatgesHelper.error(
							request,
							getMessage(
									request,
									"error.permis.administracio.entorn"));
					return "v3/cercadorTipologies";

		}
		
		return "v3/cercadorTipologies";
	}
	
	/** Mètode quan s'envia el formulari del filtre. Actualitza el filtre en sessió. */
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ExpedientTipusAdminCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			SessionHelper.removeAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:cercadorTipologies";
	}

	
	@RequestMapping(value="/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		
		ExpedientTipusAdminCommand filtreCommand = getFiltreCommand(request);
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		
		Long entornId = null;
		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		if (!persona.isAdmin()) {
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			entornId = entornActual.getId();
		}
		return DatatablesHelper.getDatatableResponse(
					request,
					null,
					expedientTipusService.findTipologiesByFiltrePaginat(
							entornId,
							conversioTipusHelper.convertir(filtreCommand, ExpedientTipusFiltreDto.class),
							paginacioParams)
					);
	}


	/** Mètode per obtenir o inicialitzar el filtre del formulari de cerca.
	 * 
	 * @param request
	 * @return
	 */
	private ExpedientTipusAdminCommand getFiltreCommand(
			HttpServletRequest request) {
		ExpedientTipusAdminCommand filtreCommand = (ExpedientTipusAdminCommand) SessionHelper.getAttribute(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientTipusAdminCommand();
			SessionHelper.setAttribute(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
}
