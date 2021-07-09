package es.caib.helium.back.controller;

import es.caib.helium.back.helper.DatatablesHelper;
import es.caib.helium.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.helium.back.helper.SessionHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.service.AdminService;
import es.caib.helium.logic.intf.service.EntornService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Controlador per a la gestió del monitor d'integracions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/monitorDomini")
public class MonitorDominiController extends BaseController {

	@Resource
	private AdminService adminService;
	@Resource(name="entornServiceV3")
	private EntornService entornService;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@RequestParam(value = "entornId", required = false) Long entornId,
			Model model) {
		Long entornActualId = entornId;
		PersonaDto persona = (PersonaDto) request.getSession().getAttribute("dadesPersona");
		if (entornActualId == null) {
			EntornDto entornActual = (EntornDto) SessionHelper.getAttribute(
					request,
					SessionHelper.VARIABLE_ENTORN_ACTUAL_V3);
			entornActualId = entornActual.getId();
		}
		
		model.addAttribute(
				"dominis",
				adminService.monitorDominiFindByEntorn(entornActualId));
		model.addAttribute(
				"entorns",
				(persona != null && persona.isAdmin())?entornService.findActiusAll():entornService.findActiusAmbPermisAdmin());
		model.addAttribute(
				"entornActualId",
				entornActualId);
		return "v3/monitorDomini";
	}

	@RequestMapping(value="/{dominiId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse accions(
			HttpServletRequest request,
			@PathVariable Long dominiId,
			Model model) {
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				adminService.monitorDominiFindAccionsByDomini(dominiId));
	}

}
