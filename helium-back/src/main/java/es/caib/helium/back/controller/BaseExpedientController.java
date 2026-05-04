/**
 *
 */
package es.caib.helium.back.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import es.caib.helium.back.mvc.ArxiuView;
import es.caib.helium.commons.dto.AccioDto;
import es.caib.helium.commons.dto.DefinicioProcesVersioDto;
import es.caib.helium.commons.dto.ExpedientDto;
import es.caib.helium.commons.dto.ExpedientTipusDto;
import es.caib.helium.commons.dto.ExpedientTipusTipusEnumDto;
import es.caib.helium.commons.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.dto.engine.WProcessInstance;
import es.caib.helium.logic.intf.service.ConsultaPinbalService;
import es.caib.helium.logic.intf.service.DissenyService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;

/**
 * Controlador base per al llistat d'expedients.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseExpedientController extends BaseController {

	@Autowired protected ExpedientService expedientService;
	@Autowired protected ExpedientTipusService expedientTipusService;
	@Autowired protected DissenyService dissenyService;
	@Autowired protected ConsultaPinbalService consultaPinbalService;
	@Resource protected WorkflowEngineApi jbpmHelper;

	protected String mostrarInformacioExpedientPerPipella(
			HttpServletRequest request,
			Long expedientId,
			Model model,
			String pipellaActiva) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		model.addAttribute("expedient", expedient);
		model.addAttribute("participants", expedientService.findParticipants(expedientId));
		model.addAttribute("relacionats", expedientService.relacioFindAmbExpedient(expedientId));
		if(expedient.getTipus().getTipus() == ExpedientTipusTipusEnumDto.FLOW) {
			DefinicioProcesVersioDto definicioProces = dissenyService.getByVersionsInstanciaProcesById(expedient.getProcessInstanceId());
			model.addAttribute("definicioProces", definicioProces);
		}
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
		if(expedient.getTipus().getTipus() == ExpedientTipusTipusEnumDto.FLOW) {
			List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(expedient.getProcessInstanceId());
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
				numAccions += accionsTrobades.size();
			}

			WProcessInstance jbpmProcessInstance = jbpmHelper.getProcessInstance(expedient.getProcessInstanceId());
			model.addAttribute("processInstance", jbpmProcessInstance != null? jbpmProcessInstance.getProcessInstance() : null);
		model.addAttribute("subprocessos", subprocessos);
		model.addAttribute("numAccions", numAccions);
		}
		model.addAttribute("numPinbals", consultaPinbalService.findConsultesPinbalPerExpedient(expedientId).size());
		model.addAttribute("perEstats", ExpedientTipusTipusEnumDto.ESTAT.equals(expedient.getTipus().getTipus()));
		if (ExpedientTipusTipusEnumDto.ESTAT.equals(expedient.getTipus().getTipus())) {
			model.addAttribute("estatsAvancar", expedientTipusService.estatGetAvancar(expedient.getId()));
			model.addAttribute("estatsRetrocedir", expedientTipusService.estatGetRetrocedir(expedient.getId()));
		}
		return "expedientPipelles";
	}

}
