/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

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

/**
 * Controlador per a la pàgina d'informació de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientTascaController extends BaseExpedientController {

	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected TascaService tascaService;

	@RequestMapping(value = "/{expedientId}/tasca", method = RequestMethod.GET)
	public String tasques(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (expedient.isPermisAdministration() || expedient.isPermisRead() || expedient.isPermisSupervision()) {
			List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
			Map<InstanciaProcesDto, List<ExpedientTascaDto>> tasques = new LinkedHashMap<InstanciaProcesDto, List<ExpedientTascaDto>>();
			model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
			model.addAttribute("expedient", expedient);
			boolean permisosVerOtrosUsuarios = veureTasquesAltresUsuaris(request, expedient);
			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
				tasques.put(instanciaProces, expedientService.findTasquesPerInstanciaProces(expedientId, instanciaProces.getId(), permisosVerOtrosUsuarios));
			}
			model.addAttribute("tasques", tasques);	
		}
		return "v3/expedientTasca";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/refrescarPanel/{procesId}", method = RequestMethod.GET)
	public String refrescarTasca(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			@PathVariable String procesId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		boolean permisosVerOtrosUsuarios = veureTasquesAltresUsuaris(request, expedient);
		InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(procesId);
		List<ExpedientTascaDto> dadesInstancia = expedientService.findTasquesPerInstanciaProces(expedientId, instanciaProces.getId(), permisosVerOtrosUsuarios);
		Map<InstanciaProcesDto, List<ExpedientTascaDto>> tasques = new LinkedHashMap<InstanciaProcesDto, List<ExpedientTascaDto>>();
		tasques.put(instanciaProces, dadesInstancia);
		model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
		model.addAttribute("expedient", expedient);
		model.addAttribute("tasques", tasques);	
		return "v3/procesTasques";
	}
	
	@RequestMapping(value = "/{expedientId}/refrescarLlistat", method = RequestMethod.GET)
	public String refrescarLlistat(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (expedient.isPermisAdministration() || expedient.isPermisRead() || expedient.isPermisSupervision()) {
			List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(Long.parseLong(expedient.getProcessInstanceId()));
			Map<InstanciaProcesDto, List<ExpedientTascaDto>> tasques = new LinkedHashMap<InstanciaProcesDto, List<ExpedientTascaDto>>();
			model.addAttribute("inicialProcesInstanceId", expedient.getProcessInstanceId());
			model.addAttribute("expedient", expedient);
			boolean permisosVerOtrosUsuarios = veureTasquesAltresUsuaris(request, expedient);
			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
				tasques.put(instanciaProces, expedientService.findTasquesPerInstanciaProces(expedientId, instanciaProces.getId(), permisosVerOtrosUsuarios));
			}
			model.addAttribute("tasques", tasques);	
		}
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
				expedientService.findAmbId(expedientId));	
		return "v3/expedientTasquesPendents";
	}

	@RequestMapping(value = "/{expedientId}/tascaPendent/{tascaId}", method = RequestMethod.GET)
	public String tascaPendent(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable String tascaId,
			Model model) {
		model.addAttribute("expedient", expedientService.findAmbId(expedientId));
		model.addAttribute("tasca", tascaService.findAmbIdPerExpedient(tascaId, expedientId));
		return "v3/expedientTascaPendent";
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/cancelar")
	public String tascaCancelar(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		try {
			expedientService.cancelarTasca(expedientId, tascaId);
			MissatgesHelper.success(request, getMessage(request, "info.tasca.cancelar", new Object[] {String.valueOf(tascaId)}));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.cancelar.tasca", new Object[] {String.valueOf(tascaId)} ));
        	logger.error("No s'ha pogut cancel·lar la tasca " + String.valueOf(tascaId), ex);
		}
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/suspendre")
	public String tascaSuspendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {		
		try {
			expedientService.suspendreTasca(expedientId, tascaId);
			MissatgesHelper.success(request, getMessage(request, "info.tasca.suspendre"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.suspendre.tasca", new Object[] {tascaId} ));
        	logger.error("No s'ha pogut suspendre la tasca " + tascaId, ex);
		}
			
		return "redirect:/v3/expedient/" + expedientId;
	}

	@RequestMapping(value = "/{expedientId}/tasca/{tascaId}/reprendre")
	public String tascaReprendre(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			@PathVariable Long tascaId,
			ModelMap model) {
		try {
			expedientService.reprendreTasca(expedientId, tascaId);
			MissatgesHelper.success(request, getMessage(request, "info.tasca.reprendre"));
		} catch (Exception ex) {
			MissatgesHelper.error(request, getMessage(request, "error.reprendre.tasca", new Object[] {tascaId} ));
        	logger.error("No s'ha pogut reprendre la tasca " + tascaId, ex);
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
			MissatgesHelper.error(request, getMessage(request, "error.agafar.tasca", new Object[] {tascaId} ));
        	logger.error("No s'ha pogut agafar la tasca " + tascaId, ex);
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
			MissatgesHelper.error(request, getMessage(request, "error.alliberar.tasca", new Object[] {tascaId} ));
        	logger.error("No s'ha pogut alliberar la tasca " + tascaId, ex);
		}
		return resultado;
	}

	private boolean veureTasquesAltresUsuaris(HttpServletRequest request, ExpedientDto expedient) {
		return (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("HEL_ADMIN")) || expedient.isPermisReassignment() || expedient.isPermisAdministration();
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientTascaController.class);

}
