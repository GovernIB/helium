/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.ExpedientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió dels formularis dels camps de tipus registre
 * a dins l'expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class ExpedientRegistreController extends CommonRegistreController {

	private ExpedientService expedientService;



	@Autowired
	public ExpedientRegistreController(
			ExpedientService expedientService,
			DissenyService dissenyService) {
		super(dissenyService);
		this.expedientService = expedientService;
	}

	@ModelAttribute("instanciaProces")
	public InstanciaProcesDto populateInstanciaProces(
			@RequestParam(value = "id", required = true) String id) {
		return expedientService.getInstanciaProcesById(id, true);
	}

	@RequestMapping(value = "/expedient/varRegistre", method = RequestMethod.GET)
	public String registreGet(HttpServletRequest request) {
		return super.registreGet(request);
	}
	@RequestMapping(value = "/expedient/varRegistre", method = RequestMethod.POST)
	public String registrePost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "registreId", required = true) Long registreId,
			@RequestParam(value = "index", required = false) Integer index,
			@RequestParam(value = "submit", required = true) String submit,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		return super.registrePost(request, id, registreId, index, submit, command, result, status, model);
	}

	@RequestMapping(value = "/expedient/varRegistreEsborrar")
	public String esborrarMembre(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "registreId", required = true) Long registreId,
			@RequestParam(value = "index", required = true) int index) {
		return super.esborrarMembre(request, id, registreId, index);
	}

	@Override
	public void esborrarRegistre(HttpServletRequest request, String id, String campCodi, int index) {
		expedientService.esborrarRegistre(id, campCodi, index);
	}
	@Override
	public Object[] getValorRegistre(HttpServletRequest request, Long entornId, String id, String campCodi) {
		return (Object[])expedientService.getVariable(id, campCodi);
	}
	@Override
	public void guardarRegistre(HttpServletRequest request, String id, String campCodi, Object[] valors,
			int index) {
		expedientService.guardarRegistre(id, campCodi, valors, index);
	}
	@Override
	public void guardarRegistre(HttpServletRequest request, String id, String campCodi, Object[] valors) {
		expedientService.guardarRegistre(id, campCodi, valors);
	}
	@Override
	public String redirectUrl(String id, String campCodi) {
		return "redirect:/expedient/dadaModificar.html?id=" + id + "&var=" + campCodi;
	}
	@Override
	public String registreUrl() {
		return "expedient/varRegistre";
	}

}
