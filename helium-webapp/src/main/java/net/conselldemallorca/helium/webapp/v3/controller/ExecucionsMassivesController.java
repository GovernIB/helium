package net.conselldemallorca.helium.webapp.v3.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per la execucions massives
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/execucionsMassives")
public class ExecucionsMassivesController extends BaseController {
	
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	
	@RequestMapping(value = "/{nivell}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable String nivell, 
			Model model) {
		model.addAttribute("nivell",nivell);
		return "v3/execucionsMassives";
	}
	
	@RequestMapping(value="{nivell}/refreshBarsExpedientMassive", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
	@ResponseBody
	public String refreshBarsExpedientMassiveAct(
			@RequestParam(value = "results", required = false) int numResults, 
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String nivell, 
			ModelMap model, 
			HttpSession session)  throws ServletException, IOException {
		return execucioMassivaService.getJsonExecucionsMassivesByUser(numResults,(nivell.equalsIgnoreCase("admin") && request.isUserInRole("ROLE_ADMIN")));
	}
	
	/**
	 * Refresca las barras de progreso de detalle de las acciones masivas
	 * @throws Exception 
	 */
	@RequestMapping(value = "cancelExpedientMassiveAct", method = RequestMethod.POST)
	@ResponseBody
	public void cancelExpedientMassiveAct(
			@RequestParam(value = "idExp", required = false) Long idExp, 
			HttpServletRequest request, 
			HttpServletResponse response, 
			ModelMap model, 
			HttpSession session
		) throws Exception {
		execucioMassivaService.cancelarExecucio(idExp);
	}
}
