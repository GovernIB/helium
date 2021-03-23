package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.persones.PersonesPlugin;
import net.conselldemallorca.helium.v3.core.api.dto.AreaMembreDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.EntornAreaMembreService;
import net.conselldemallorca.helium.v3.core.api.service.EntornCarrecService;
import net.conselldemallorca.helium.webapp.v3.command.EntornAreaMembreCommand;
import net.conselldemallorca.helium.webapp.v3.command.EntornAreaMembreCommand.Creacio;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la gestió de les àrees
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "entornAreaMembresControllerV3")
@RequestMapping("/v3/entorn-area")
public class EntornAreaMembresController extends BaseController {

	private PersonesPlugin personesPlugin;
	@Autowired
	private EntornCarrecService entornCarrecService;
	@Autowired
	private EntornAreaMembreService entornAreaMembreService;
	@Autowired
	private EntornHelper entornHelper;

	@RequestMapping(value = "{entornAreaId}/membres/llista", method = RequestMethod.GET)
	public String llista(HttpServletRequest request, @PathVariable Long entornAreaId, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		prepararModel(request, entornAreaId, model, entornActual);
		model.addAttribute("mostraCreate", false);
		model.addAttribute("areaId", entornAreaId);
		EntornAreaMembreCommand command = new EntornAreaMembreCommand();
		model.addAttribute(command);
		return "v3/entornAreaMembre";
	}
	
	@RequestMapping(value = "{entornAreaId}/membres/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(HttpServletRequest request, Model model) {
		
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return DatatablesHelper.getEmptyDatatableResponse(request);
		}

		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, entornAreaMembreService.findPerDatatable(paginacioParams));
	}
	
	@RequestMapping(value = "{entornAreaId}/membres/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, @PathVariable Long entornAreaId, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		prepararModel(request, entornAreaId, model, entornActual);
		model.addAttribute("areaId", entornAreaId);
		EntornAreaMembreCommand command = new EntornAreaMembreCommand();
		command.setAreaId(entornAreaId);
		model.addAttribute(command);
		return "v3/entornAreaMembre";
	}
	
	@RequestMapping(value = "{entornAreaId}/membres/new", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, @PathVariable Long entornAreaId,
			@Validated(Creacio.class) EntornAreaMembreCommand command,
			BindingResult bindingResult, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "v3/modalBlank";
		}
		prepararModel(request, entornAreaId, model, entornActual);
		
		model.addAttribute("entornCarrecs", entornCarrecService.findCarrecsByEntornAndArea(entornActual.getId(), entornAreaId));
		if (bindingResult.hasErrors()) {
			model.addAttribute("mostraCreate", true);
			return "v3/entornAreaMembre";
		}
		model.addAttribute("mostraCreate", false);
		AreaMembreDto areaMembre = new AreaMembreDto();
		areaMembre.setAreaId(entornAreaId);
		areaMembre.setCodi(command.getCodi());
		entornAreaMembreService.create(entornActual.getId(), command.getCarrecId(), areaMembre);
		
		return "v3/entornAreaMembre";
	}
	
	private String prepararModel(HttpServletRequest request,Long entornAreaId, Model model, EntornDto entornActual) {
		
		try {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.persones.plugin.class");
			if (pluginClass != null) {
				personesPlugin = (PersonesPlugin)(Class.forName(pluginClass).newInstance());
				model.addAttribute("persones", personesPlugin.findAll());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "v3/modalBlank";
		}
		
		model.addAttribute("entornCarrecs", entornCarrecService.findCarrecsByEntornAndArea(entornActual.getId(), entornAreaId));
		return "";
	}
	
	@RequestMapping(value = "/{entornAreaId}/membres/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request, 
			@PathVariable Long entornAreaId,
			@PathVariable Long id,
			Model model) {
		try {
			entornAreaMembreService.delete(entornAreaId, id);
			EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
			if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
				MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
				return "v3/modalBlank";
			}
			model.addAttribute("entornCarrecs", entornCarrecService.findCarrecsByEntornAndArea(entornActual.getId(), entornAreaId));
			prepararModel(request, entornAreaId, model, entornActual);
			model.addAttribute("areaId", entornAreaId);
			EntornAreaMembreCommand command = new EntornAreaMembreCommand();
			model.addAttribute(command);
			
//			MissatgesHelper.success(
//					request,
//					getMessage(
//							request,
//							"expedient.tipus.enumeracio.valors.controller.eliminat"));			
		} catch(Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request,
							"area.membre.esborrar.error"));
			logger.error("S'ha produit un error al intentar el membre amb id '" + id + "' de l'àrea amb id '" + entornAreaId, e);
		}
		
		return "/v3/entornAreaMembre";
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntornAreaMembresController.class);
	
}
