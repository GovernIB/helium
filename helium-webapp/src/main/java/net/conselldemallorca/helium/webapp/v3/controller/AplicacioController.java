/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.helium.logic.helper.EntornHelper;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.dto.UsuariPreferenciesDto;
import es.caib.helium.logic.intf.service.AdminService;
import es.caib.helium.logic.intf.service.EntornService;
import es.caib.helium.logic.util.EntornActual;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

/**
 * Controlador per a la pàgina inicial (index).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class AplicacioController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private EntornService entornService;
	@Autowired
	private EntornHelper entornHelper;


	@RequestMapping(value = "/v3", method = RequestMethod.GET)
	public String get(HttpServletRequest request) {
		return "redirect:/v3/index";
	}

	@RequestMapping(value = "/v3/missatges", method = RequestMethod.GET)
	public String getMissatges(HttpServletRequest request) {
		return "v3/missatges";
	}

	@RequestMapping(value = "/v3/utils/modalTancar", method = RequestMethod.GET)
	public String modalTancar() {
		return "utils/modalTancar";
	}

	@RequestMapping(value = "/v3/index", method = RequestMethod.GET)
	public String index(HttpServletRequest request) {
		UsuariPreferenciesDto preferencies = SessionHelper.getSessionManager(request).getPreferenciesUsuari();
		if (preferencies != null) {
			if (preferencies.getListado() == 2 && 
					preferencies.getConsultaId() != null && 
					SessionHelper.getSessionManager(request).getEntornActual().getCodi().equals(preferencies.getDefaultEntornCodi())) {
				// Informes
				return "redirect:/v3/informe?consultaId="+preferencies.getConsultaId();
			} else if (preferencies.getListado() == 1) {
				// Tareas
				return "redirect:/v3/tasca";
			} else if (preferencies.getConsultaId() != null && 
						SessionHelper.getSessionManager(request).getEntornActual().getCodi().equals(preferencies.getDefaultEntornCodi())) {
				// Consulta per defecte
				return "redirect:/v3/expedient/consulta/" + preferencies.getConsultaId();
			} 
		}
		// Expedientes
		return "redirect:/v3/expedient";
	}

	@RequestMapping(value = "/v3/metrics", method = RequestMethod.GET)
	@ResponseBody
	public String metrics(
			HttpServletRequest request) {
		return adminService.getMetrics();
	}

	@RequestMapping(value = "/v3/metriques", method = RequestMethod.GET)
	public String metricsView(
			HttpServletRequest request,
			Model model) {
		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		model.addAttribute("metriques", adminService.getMetrics());
		model.addAttribute("entorns", 
				(persona != null && persona.isAdmin())?entornService.findActiusAll():
					(entornHelper.esAdminEntorn(EntornActual.getEntornId()))? entornService.findActiusAmbPermisAdmin():new ArrayList<EntornDto>());
		return "v3/metrics";
	}
}
