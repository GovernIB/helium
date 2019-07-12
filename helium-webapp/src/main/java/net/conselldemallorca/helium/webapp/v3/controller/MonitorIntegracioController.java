package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per a la gestió del monitor d'integracions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/monitorIntegracio")
public class MonitorIntegracioController extends BaseController {

	@Autowired
	private AdminService adminService;

	@RequestMapping(method = RequestMethod.GET)
	String get(
			HttpServletRequest request,
			Model model) {
		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		model.addAttribute(
				"integracions",
				(persona != null && persona.isAdmin())?
				adminService.monitorIntegracioFindAll():adminService.monitorIntegracioFindAllEntronActual());
		return "v3/monitorIntegracio";
	}

	@RequestMapping(value="/{integracioCodi}/datatable", method = RequestMethod.GET)
	@ResponseBody
	DatatablesResponse accions(
			HttpServletRequest request,
			@PathVariable String integracioCodi,
			Model model) {
		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		return DatatablesHelper.getDatatableResponse(
				request,
				null,
				(persona != null && persona.isAdmin())?
						adminService.monitorIntegracioFindAccionsByIntegracio(integracioCodi):adminService.monitorIntegracioFindAccionsByIntegracioEntornActual(integracioCodi));
	}

}
