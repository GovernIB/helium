/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesVersioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;

/**
 * Controlador base per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientController extends BaseController {

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected DissenyService dissenyService;

	protected String mostrarInformacioExpedientPerPipella(
			HttpServletRequest request,
			Long expedientId,
			Model model,
			String pipellaActiva) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		
		model.addAttribute("metadades", expedient.isNtiActiu() && expedient.getTipus().isNtiActiu());
		
		model.addAttribute("expedient", expedient);
		model.addAttribute("participants", expedientService.findParticipants(expedientId));
		model.addAttribute("relacionats", expedientService.relacioFindAmbExpedient(expedientId));
		DefinicioProcesVersioDto versions = dissenyService.getByVersionsInstanciaProcesById(expedient.getProcessInstanceId());
		model.addAttribute("definicioProces", versions);
		if (pipellaActiva != null)
			model.addAttribute("pipellaActiva", pipellaActiva);
		else if (request.getParameter("pipellaActiva") != null)
			model.addAttribute("pipellaActiva", request.getParameter("pipellaActiva"));
		else
			model.addAttribute("pipellaActiva", "dades");
		
		List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
//		Map<InstanciaProcesDto, List<AccioDto>> accions = new LinkedHashMap<InstanciaProcesDto, List<AccioDto>>();
		int numAccions = 0;
		List<String> subprocessos = new ArrayList<String>();
		for (InstanciaProcesDto instanciaProces: arbreProcessos) {
			// Subprocessos
			if (!instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
				String subproces = instanciaProces.getTitol() + " v." + instanciaProces.getDefinicioProces().getVersio();
				subprocessos.add(subproces);
			}
			// Accions
			List<AccioDto> accionsTrobades = expedientService.accioFindVisiblesAmbProcessInstanceId(
					expedientId,
					instanciaProces.getId());
//			accions.put(instanciaProces, accionsTrobades);
			numAccions += accionsTrobades.size();
		}
		model.addAttribute("subprocessos", subprocessos);
		model.addAttribute("numAccions", numAccions);
		return "v3/expedientPipelles";
	}

}
