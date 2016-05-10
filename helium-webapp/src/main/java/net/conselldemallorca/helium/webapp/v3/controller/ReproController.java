/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReproDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ReproService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per Repros
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/repro")
public class ReproController extends BaseController {

	@Autowired
	private ReproService reproService;
	@Autowired
	protected ExpedientService expedientService;
	@Autowired
	protected DissenyService dissenyService;
	@Autowired
	protected TascaService tascaService;
	@Autowired
	private net.conselldemallorca.helium.core.model.service.TascaService tascaInicialService;
	
	@SuppressWarnings("unchecked")
	@ModelAttribute("command")
	protected Object populateCommand(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			Model model,
			Map<String, Object> valors) {
		try {
			Map<String, Object> campsAddicionals = new HashMap<String, Object>();
			Map<String, Class<?>> campsAddicionalsClasses = new HashMap<String, Class<?>>();
			EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
			ExpedientTascaDto tasca = obtenirTascaInicial(
					entorn.getId(),
					expedientTipusId,
					definicioProcesId,
					new HashMap<String, Object>(),
					request);
			campsAddicionals.put("id", tasca.getId());
			campsAddicionals.put("entornId", entorn.getId());
			campsAddicionals.put("expedientTipusId", expedientTipusId);
			campsAddicionals.put("definicioProcesId", definicioProcesId);
			campsAddicionalsClasses.put("id", String.class);
			campsAddicionalsClasses.put("entornId", Long.class);
			campsAddicionalsClasses.put("expedientTipusId", Long.class);
			campsAddicionalsClasses.put("definicioProcesId", Long.class);
			Map<String, Object> valorsFormulariExtern = null;
			if (tasca.isFormExtern()) {
				valorsFormulariExtern = tascaInicialService.obtenirValorsFormulariExternInicial(tasca.getId());
				if (valorsFormulariExtern != null) {
					request.getSession().setAttribute(
							ExpedientIniciController.CLAU_SESSIO_FORM_VALORS,
							valorsFormulariExtern);
				} else {
					valorsFormulariExtern = (Map<String, Object>)request.getSession().getAttribute(
							ExpedientIniciController.CLAU_SESSIO_FORM_VALORS);
				}
			}
			if (valors == null) {
				return TascaFormHelper.getCommandForCamps(
						tascaService.findDadesPerTascaDto(tasca),
						valorsFormulariExtern,
						campsAddicionals,
						campsAddicionalsClasses,
						false);
			} else {
				return TascaFormHelper.getCommandForCamps(
						tascaService.findDadesPerTascaDto(tasca),
						valors,
						campsAddicionals,
						campsAddicionalsClasses,
						false);
			}
		} catch (TascaNotFoundException ex) {
			MissatgesHelper.error(request, ex.getMessage());
			logger.error("No s'han pogut encontrar la tasca: " + ex.getMessage(), ex);
		}
		return null;
	}

	@RequestMapping(value = "/{expedientTipusId}/{definicioProcesId}/guardarRepro", method = RequestMethod.POST)
	public String guardarRepro(
			HttpServletRequest request, 
			@RequestParam(value = "nomRepro", required = true) String nomRepro, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			Model model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
		List<TascaDadaDto> tascaDades = tascaService.findDadesPerTascaDto(tasca);
		
		Map<String, Object> valors = TascaFormHelper.getValorsFromCommand(
				tascaDades,
				command,
				false,
				true);
		
		reproService.create(expedientTipus.getId(), nomRepro, valors);
		
//		return "redirect:/modal/v3/repro/" + expedientTipusId + "/" + definicioProcesId + "/getRepro/" + repro.getId();
		return "v3/expedient/iniciarPasForm";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{expedientTipusId}/{definicioProcesId}/getRepro/{reproId}", method = RequestMethod.GET)
	public String getRepro(
			HttpServletRequest request, 
			@PathVariable Long expedientTipusId,
			@PathVariable Long definicioProcesId,
			@PathVariable Long reproId,
			Model model) {
		definicioProcesToModel(expedientTipusId, definicioProcesId, model);
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
		ReproDto repro = reproService.findById(reproId);
		Map<String,Object> valors = ((Map<String,Object>)JSONValue.parse(repro.getValors()));
		
		Object command = populateCommand(request, expedientTipusId, definicioProcesId, model, valors);
		
		ExpedientTascaDto tasca = obtenirTascaInicial(entorn.getId(), expedientTipusId, definicioProcesId, new HashMap<String, Object>(), request);
		List<TascaDadaDto> dades = tascaService.findDadesPerTascaDto(tasca);
		List<ReproDto> repros = reproService.findReprosByUsuariTipusExpedient(expedientTipus.getId());
		
		if (!model.containsAttribute("command") || model.asMap().get("command") == null) {
			model.addAttribute("command", command);
		}
		
		model.addAttribute("tasca", tasca);
		model.addAttribute("dades", dades);
		model.addAttribute("repros", repros);
		model.addAttribute("entornId", entorn.getId());
		model.addAttribute("expedientTipus", expedientTipus);
		model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
		return "v3/expedient/iniciarPasForm";
	}
	
	@RequestMapping(value = "/borrarRepro/{reproId}", method = RequestMethod.GET)
	public String deleteRepro(
			HttpServletRequest request,
			@PathVariable Long reproId,
			Model model) {
		reproService.deleteById(reproId);
		return "v3/expedient/iniciarPasForm";
	}
	
	private ExpedientTascaDto obtenirTascaInicial(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors, HttpServletRequest request) {
		ExpedientTascaDto tasca = expedientService.getStartTask(entornId, expedientTipusId, definicioProcesId, valors);
		tasca.setId((String) request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(ExpedientIniciController.CLAU_SESSIO_FORM_VALIDAT);
		tasca.setValidada(validat != null);
		return tasca;
	}
	
	private void definicioProcesToModel(Long expedientTipusId, Long definicioProcesId, Model model){
		// Si l'expedient requereix dades inicials redirigeix al pas per demanar aquestes dades
		DefinicioProcesDto definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = dissenyService.getById(definicioProcesId);
		} else {
			definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId);
		}
		model.addAttribute("definicioProces", definicioProces);
	}

	private static final Log logger = LogFactory.getLog(ReproController.class);
}
