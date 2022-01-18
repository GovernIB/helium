/**
 * 
 */
package es.caib.helium.back.controller;

import es.caib.helium.back.helper.MissatgesHelper;
import es.caib.helium.back.helper.ModalHelper;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.ExpedientTascaService;
import es.caib.helium.logic.intf.service.TascaService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTascaController extends BaseExpedientController {

	@Autowired
	protected ExpedientTascaService expedientTascaService;
	@Autowired
	protected TascaService tascaService;

	/** Càrrega le la pipella de tasques en la gestió de l'expedient. */
	@RequestMapping(value = "/{expedientId}/tasca", method = RequestMethod.GET)
	public String tasques(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
	
		this.emplenarModelTasques(model, expedientId);		
		return "v3/expedientTasca";
	}

	/** Refresc de les tasques de la pipella de tasques en la gestió de l'expedient. */
	@RequestMapping(value = "/{expedientId}/refrescarLlistat", method = RequestMethod.GET)
	public String refrescarLlistat(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		this.emplenarModelTasques(model, expedientId);		
		return "v3/procesTasques";
	}	

	/** Mètode comú per emplenar l'arbre de processos de la pipella de tasques en la gestió d'expedients.
	 * @param model
	 * @param expedientId
	 */
	private void emplenarModelTasques(Model model, Long expedientId) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		if (expedient.isPermisAdministration() || expedient.isPermisRead() || expedient.isPermisSupervision()) {
//			List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(expedient.getProcessInstanceId());
//			Map<InstanciaProcesDto, List<ExpedientTascaDto>> tasques = new LinkedHashMap<InstanciaProcesDto, List<ExpedientTascaDto>>();
//			model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
//			model.addAttribute("expedient", expedient);
//			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
//				tasques.put(instanciaProces, expedientTascaService.findAmbInstanciaProces(
//						expedientId,
//						instanciaProces.getId()));
//			}
//			model.addAttribute("tasques", tasques);	
			
			List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(expedient.getProcessInstanceId());
			Map<InstanciaProcesDto, List<ExpedientTascaDto>> tasques = new LinkedHashMap<InstanciaProcesDto, List<ExpedientTascaDto>>();
			model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
			model.addAttribute("expedient", expedient);
			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
				tasques.put(instanciaProces, expedientTascaService.findAmbInstanciaProces(
						expedientId,
						instanciaProces.getId()));
			}
			model.addAttribute("tasques", tasques);	
		}
	}

	
	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/refrescarPanel/{procesId}", method = RequestMethod.GET)
	public String refrescarTasca(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@PathVariable String procesId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbIdAmbPermis(expedientId);
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
		List<ExpedientTascaDto> dadesInstancia = expedientTascaService.findAmbInstanciaProces(
				expedientId,
				instanciaProces.getId());
		Map<InstanciaProcesDto, List<ExpedientTascaDto>> tasques = new LinkedHashMap<InstanciaProcesDto, List<ExpedientTascaDto>>();
		tasques.put(instanciaProces, dadesInstancia);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("expedient", expedient);
		model.addAttribute("tasques", tasques);	
		return "v3/procesTasques";
	}

	@RequestMapping(value = "/{expedientId}/tasquesPendents/{nomesTasquesPersonals}/{nomesTasquesGrup}/consultesTipus", method = RequestMethod.GET)
	public String tasquesPendents2(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable boolean nomesTasquesPersonals,
			@PathVariable boolean nomesTasquesGrup,
			Model model) {
		model.addAttribute(
				"retrocedirModalPath",
				true);
		return tasquesPendents(
				request,
				expedientId,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				model);
	}

	@RequestMapping(value = "/{expedientId}/tasquesPendents/{nomesTasquesPersonals}/{nomesTasquesGrup}", method = RequestMethod.GET)
	public String tasquesPendents(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable boolean nomesTasquesPersonals,
			@PathVariable boolean nomesTasquesGrup,
			Model model) {
		model.addAttribute(
				"tasques",
				expedientService.findTasquesPendents(
						expedientId,
						nomesTasquesPersonals,
						nomesTasquesGrup));
		model.addAttribute(
				"expedient",
				expedientService.findAmbIdAmbPermis(expedientId));	
		return "v3/expedientTasquesPendents";
	}

	@RequestMapping(value = "/{expedientId}/tascaPendent/{tascaId}", method = RequestMethod.GET)
	public String tascaPendent(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		model.addAttribute("expedient", expedientService.findAmbIdAmbPermis(expedientId));
		model.addAttribute("tasca", tascaService.findAmbIdPerExpedient(tascaId, expedientId));
		return "v3/expedientTascaPendent";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/cancelar")
	public String tascaCancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		try {
			expedientTascaService.cancelar(expedientId, tascaId);
			MissatgesHelper.success(request, getMessage(request, "info.tasca.cancelar", new Object[] {String.valueOf(tascaId)}));
		} catch (Exception ex) {
			String errMsg = getMessage(request, "error.cancelar.tasca", new Object[] {String.valueOf(tascaId),  ExceptionUtils.getRootCauseMessage(ex)} );
			MissatgesHelper.error(request, errMsg);
        	logger.error(errMsg, ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/suspendre")
	public String tascaSuspendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {		
		try {
			expedientTascaService.suspendre(expedientId, tascaId);
			MissatgesHelper.success(request, getMessage(request, "info.tasca.suspendre"));
		} catch (Exception ex) {
			String errMsg = getMessage(request, "error.suspendre.tasca", new Object[] {String.valueOf(tascaId),  ExceptionUtils.getRootCauseMessage(ex)} );
			MissatgesHelper.error(request, errMsg);
        	logger.error(errMsg, ex);        	
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reprendre")
	public String tascaReprendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		try {
			expedientTascaService.reprendre(expedientId, tascaId);
			MissatgesHelper.success(request, getMessage(request, "info.tasca.reprendre"));
		} catch (Exception ex) {
			String errMsg = getMessage(request, "error.reprendre.tasca", new Object[] {String.valueOf(tascaId), ExceptionUtils.getRootCauseMessage(ex)} );
			MissatgesHelper.error(request, errMsg);
        	logger.error(errMsg, ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/agafar", method = RequestMethod.GET)
	@ResponseBody
	public boolean agafar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		boolean resultado = false;
		try {
			tascaService.agafar(tascaId);
			resultado = true;
			MissatgesHelper.success(request, getMessage(request, "info.tasca.disponible.personals"));
		} catch (Exception ex) {
			String errMsg = getMessage(request, "error.agafar.tasca", new Object[] {String.valueOf(tascaId), ExceptionUtils.getRootCauseMessage(ex)} );
			MissatgesHelper.error(request, errMsg);
        	logger.error(errMsg, ex);
		}
		return resultado;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/alliberar", method = RequestMethod.GET)
	@ResponseBody
	public boolean alliberar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			ModelMap model) {
		boolean resultado = false;
		try {
			tascaService.alliberar(tascaId);
			resultado = true;
			MissatgesHelper.success(request, getMessage(request, "info.tasca.alliberada"));
		} catch (Exception ex) {
			String errMsg = getMessage(request, "error.alliberar.tasca", new Object[] {String.valueOf(tascaId), ExceptionUtils.getRootCauseMessage(ex)} );
			MissatgesHelper.error(request, errMsg);
        	logger.error(errMsg, ex);
		}
		return resultado;
	}

	@RequestMapping(value = "/{expedientId}/execucioInfo/{tascaId}", method = RequestMethod.GET)
	public String execucioInfo(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		
		try {
			List<String[]> missatges = tascaService.getMissatgesExecucioSegonPla(tascaId);
			model.addAttribute("tasca", tascaService.findAmbIdPerExpedient(tascaId, expedientId));
			model.addAttribute("missatges", missatges);
		} catch (NoTrobatException ex) {
			MissatgesHelper.warning(request, getMessage(request, "expedient.tasca.segon.pla.finalitzada"));
			if (ModalHelper.isModal(request)) {
				return modalUrlTancar(false);
			} else {
				String referer = request.getHeader("Referer");
				return "redirect:"+ referer;
			}
		}
		return "v3/missatgesExecucioSegonPla";
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaController.class);

}