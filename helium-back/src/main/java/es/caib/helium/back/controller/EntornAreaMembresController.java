package es.caib.helium.back.controller;

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

import es.caib.helium.back.command.EntornAreaMembreCommand;
import es.caib.helium.back.command.EntornAreaMembreCommand.Creacio;
import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.commons.dto.AreaMembreDto;
import es.caib.helium.commons.dto.EntornDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.integracio.plugins.persones.PersonesPlugin;
import es.caib.helium.logic.intf.service.EntornAreaMembreService;
import es.caib.helium.logic.intf.service.EntornCarrecService;
import es.caib.helium.logic.helper.EntornHelper;

/**
 * Controlador per a la gestió de les àrees
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller(value = "entornAreaMembresControllerV3")
@RequestMapping("/entorn-area")
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
			return "modalBlank";
		}
		prepararModel(request, entornAreaId, model, entornActual);
		model.addAttribute("mostraCreate", false);
		model.addAttribute("areaId", entornAreaId);
		EntornAreaMembreCommand command = new EntornAreaMembreCommand();
		model.addAttribute(command);
		return "entornAreaMembre";
	}

	@RequestMapping(value = "{entornAreaId}/membres/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse datatable(
			@PathVariable Long entornAreaId,
			HttpServletRequest request,
			Model model) {

		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return DatatablesHelper.getEmptyDatatableResponse(request);
		}

		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, null, entornAreaMembreService.findPerDatatable(entornAreaId, paginacioParams));
	}

	@RequestMapping(value = "{entornAreaId}/membres/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, @PathVariable Long entornAreaId, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "modalBlank";
		}
		prepararModel(request, entornAreaId, model, entornActual);
		model.addAttribute("areaId", entornAreaId);
		EntornAreaMembreCommand command = new EntornAreaMembreCommand();
		command.setAreaId(entornAreaId);
		model.addAttribute(command);
		return "entornAreaMembre";
	}

	@RequestMapping(value = "{entornAreaId}/membres/new", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, @PathVariable Long entornAreaId,
			@Validated(Creacio.class) EntornAreaMembreCommand command,
			BindingResult bindingResult, Model model) {
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
		if (entornActual == null || entornActual.getId() == null || entornHelper.getEntornComprovantPermisos(entornActual.getId(), true) == null) {
			MissatgesHelper.error(request, getMessage(request, "error.cap.entorn"));
			return "modalBlank";
		}
		prepararModel(request, entornAreaId, model, entornActual);

		model.addAttribute("entornCarrecs", entornCarrecService.findCarrecsByEntornAndArea(entornActual.getId(), entornAreaId));
		if (bindingResult.hasErrors()) {
			model.addAttribute("mostraCreate", true);
			return "entornAreaMembre";
		}
		model.addAttribute("mostraCreate", false);
		AreaMembreDto areaMembre = new AreaMembreDto();
		areaMembre.setAreaId(entornAreaId);
		areaMembre.setCodi(command.getCodi());
		entornAreaMembreService.create(entornActual.getId(), command.getCarrecId(), areaMembre);

		return "entornAreaMembre";
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
			return "modalBlank";
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
				return "modalBlank";
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
							"area.membre.esborrar.error"),
					e);
			logger.error("S'ha produit un error al intentar el membre amb id '" + id + "' de l'àrea amb id '" + entornAreaId, e);
		}

		return "/entornAreaMembre";
	}

	private static final Logger logger = LoggerFactory.getLogger(EntornAreaMembresController.class);

}
