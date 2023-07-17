/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesVersioDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.webapp.mvc.ArxiuView;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;

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
		EntornDto entornActual = SessionHelper.getSessionManager(request).getEntornActual();
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
		ExpedientTipusDto expedientTipusDto = expedientTipusService.findAmbId(expedient.getTipus().getId());
		if(expedientTipusDto!=null && expedientTipusDto.getManualAjudaNom()!=null) {
			model.addAttribute(ArxiuView.MODEL_ATTRIBUTE_FILENAME, expedientTipusDto.getManualAjudaNom());
			expedient.getTipus().setManualAjudaNom(expedientTipusDto.getManualAjudaNom());
		}
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
		model.addAttribute("perEstats", ExpedientTipusTipusEnumDto.ESTAT.equals(expedient.getTipus().getTipus()));
		if (ExpedientTipusTipusEnumDto.ESTAT.equals(expedient.getTipus().getTipus())) {
			model.addAttribute("estatsAvancar", expedientTipusService.estatGetAvancar(expedient.getId()));
			model.addAttribute("estatsRetrocedir", expedientTipusService.estatGetRetrocedir(expedient.getId()));
		}
		return "v3/expedientPipelles";
	}

}
