package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controlador per a la pàgina d'accions de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientAccioController extends BaseExpedientController {

	@RequestMapping(value = "/{expedientId}/accions", method = RequestMethod.GET)
	public String documents(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
		Map<InstanciaProcesDto, List<AccioDto>> accions = new LinkedHashMap<InstanciaProcesDto, List<AccioDto>>();
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			List<AccioDto> accionsPI = expedientService.findAccionsVisiblesAmbProcessInstanceId(instanciaProces.getId());
			accions.put(instanciaProces, accionsPI);
		}
		model.addAttribute("tasques", accions);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());		
		model.addAttribute("expedient", expedient);
		model.addAttribute("accions", accions);
		return "v3/expedientAccio";
	}
	
//	@RequestMapping(value = "/{expedientId}/terminis/{procesId}", method = RequestMethod.GET)
//	public String dadesProces(
//			HttpServletRequest request,
//			@PathVariable Long expedientId,
//			@PathVariable String procesId,
//			Model model) {
//		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);		
//		Map<InstanciaProcesDto, List<TerminiDto>> terminis = new LinkedHashMap<InstanciaProcesDto, List<TerminiDto>>();
//		Map<String, List<TerminiIniciatDto>> iniciats = new LinkedHashMap<String, List<TerminiIniciatDto>>();
//		terminis.put(instanciaProces, terminiService.findTerminisAmbProcessInstanceId(instanciaProces.getId()));
//		iniciats.put(instanciaProces.getId(), terminiService.findIniciatsAmbProcessInstanceId(instanciaProces.getId()));
//		model.addAttribute("inicialProcesInstanceId", procesId);
//		model.addAttribute("terminis",terminis);
//		model.addAttribute("iniciats",iniciats);
//		return "v3/procesTerminis";
//	}

	private static final Log logger = LogFactory.getLog(ExpedientAccioController.class);
}
