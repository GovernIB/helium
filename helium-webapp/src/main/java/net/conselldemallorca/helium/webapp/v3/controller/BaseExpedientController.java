/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import es.caib.helium.logic.intf.dto.AccioDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesVersioDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;

/**
 * Controlador base per al llistat d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientController extends BaseController {

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected ExpedientTipusService expedientTipusService;
	@Autowired
	protected DissenyService dissenyService;

	protected String mostrarInformacioExpedientPerPipella(
			HttpServletRequest request,
			Long expedientId,
			Model model,
			String pipellaActiva) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		model.addAttribute("expedient", expedient);
		model.addAttribute("participants", expedientService.findParticipants(expedientId));
		model.addAttribute("relacionats", expedientService.relacioFindAmbExpedient(expedientId));
		DefinicioProcesVersioDto definicioProces = dissenyService.getByVersionsInstanciaProcesById(expedient.getProcessInstanceId());
		model.addAttribute("definicioProces", definicioProces);
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
