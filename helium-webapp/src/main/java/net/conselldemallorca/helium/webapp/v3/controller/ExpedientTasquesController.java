/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTasquesController extends BaseExpedientController {

	@Resource(name="expedientServiceV3")
	private ExpedientService expedientService;
	@Resource(name="tascaServiceV3")
	private TascaService tascaService;

	@RequestMapping(value = "/{expedientId}/tasques", method = RequestMethod.GET)
	public String tasques(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		if (!NoDecorarHelper.isRequestSenseDecoracio(request)) {
			return mostrarInformacioExpedientPerPipella(
					request,
					expedientId,
					model,
					"tasques",
					expedientService);
		}
		model.addAttribute("expedientId", expedientId);
		model.addAttribute(
				"tasques",
				expedientService.findTasquesPerExpedient(
						expedientId));
		return "v3/expedientTasques";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/tramitar", method = RequestMethod.GET)
	public String tramitar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		return "redirect:/v3/expedient/" + expedientId + "/tasca/" + tascaId + "/form";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/form", method = RequestMethod.GET)
	public String form(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		NoDecorarHelper.marcarNoCapsaleraNiPeu(request);
		model.addAttribute(
				"tasca",
				expedientService.getTascaPerExpedient(
						expedientId,
						tascaId));
		model.addAttribute(
				"dades",
				tascaService.findDadesPerTasca(tascaId));
		return "v3/expedientTascaForm";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/camp/{campId}/valorsSeleccio", method = RequestMethod.GET)
	@ResponseBody
	public List<SeleccioOpcioDto> valorsSeleccio(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@PathVariable Long campId,
			Model model) {
		return tascaService.findOpcionsSeleccioPerCampTasca(
				tascaId,
				campId);
	}

}
