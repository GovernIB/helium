/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.UsuariPreferenciesDto;
import net.conselldemallorca.helium.v3.core.api.service.AdminService;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina inicial (index).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class IndexV3Controller {

	@Autowired
	private AdminService adminService;

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
			if (preferencies.getListado() == 2 && preferencies.getConsultaId() != null) {
				// Informes
				return "redirect:/v3/informe/consulta/"+preferencies.getConsultaId();
			} else if (preferencies.getListado() == 1) {
				// Tareas
				return "redirect:/v3/tasca";
			} 
		}
		// Expedientes
		return "redirect:/v3/expedient";
	}
}
