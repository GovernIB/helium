/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.service.PluginService;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador pel pas de formulari de l'inici d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v3/expedient")
public class ExpedientInicioPasFormController extends ExpedientTramitacioController {
	
	@Autowired
	private PluginService pluginService;

	@ModelAttribute("command")
	protected Object populateCommand(
			HttpServletRequest request, 
			String id,
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			ModelMap model) {
			if (entornId != null && id != null) {
				Object command = null;
				Object commandSessio = obtenirCommandSessio(request);
				ExpedientTascaDto tascaInicial = obtenirTascaInicial(
						entornId,
						expedientTipusId,
						definicioProcesId,
						null,
						request);
				List<CampDto> camps = new ArrayList<CampDto>();
				for (CampTascaDto campTasca: tascaInicial.getCamps())
					camps.add(campTasca.getCamp());
				Map<String, Object> valorsCommand = new HashMap<String, Object>();
				if (tascaInicial.getVariables() != null)
					valorsCommand.putAll(tascaInicial.getVariables());
				valorsCommand.putAll(obtenirValorsRegistresSessio(request, camps));
				if (commandSessio != null) {
					command = commandSessio;
				} else {
					if (tascaInicial != null) {
						Map<String, Object> campsAddicionals = new HashMap<String, Object>();
						campsAddicionals.put("id", id);
						campsAddicionals.put("entornId", entornId);
						campsAddicionals.put("expedientTipusId", expedientTipusId);
						campsAddicionals.put("definicioProcesId", definicioProcesId);
						@SuppressWarnings("rawtypes")
						Map<String, Class> campsAddicionalsClasses = new HashMap<String, Class>();
						campsAddicionalsClasses.put("id", String.class);
						campsAddicionalsClasses.put("entornId", Long.class);
						campsAddicionalsClasses.put("expedientTipusId", Long.class);
						campsAddicionalsClasses.put("definicioProcesId", Long.class);
						command = TascaFormHelper.getCommandForCamps(
								camps,
								valorsCommand,
								campsAddicionals,
								campsAddicionalsClasses,
								false);
					}
				}
				model.addAttribute("camps", camps);
				model.addAttribute("tasca", tascaInicial);
				guardarValorsSessio(request, valorsCommand);
				return command;
			}
		return null;
	}

	private void guardarValorsSessio(
			HttpServletRequest request,
			Map<String, Object> valors) {
		request.getSession().setAttribute(
				ExpedientInicioController.CLAU_SESSIO_FORM_VALORS,
				valors);
	}
	
	private Map<String, Object> obtenirValorsRegistresSessio(
			HttpServletRequest request,
			List<CampDto> camps) {
		Map<String, Object> valors = new HashMap<String, Object>();
		for (CampDto camp: camps) {
			Object obj = request.getSession().getAttribute(ExpedientInicioController.getClauSessioCampRegistre(camp.getCodi()));
			if (obj != null)
				valors.put(camp.getCodi(), obj);
		}
		return valors;
	}

	private Object obtenirCommandSessio(
			HttpServletRequest request) {
		return request.getSession().getAttribute(
				ExpedientInicioController.CLAU_SESSIO_FORM_COMMAND);
	}
	
	@RequestMapping(value = "/iniciarPasForm", method = RequestMethod.POST)
	public String iniciarPasFormPost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "helAccioCamp", required = false) String accioCamp, 
			@ModelAttribute("command") Object command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {

			model.addAttribute("entornId", entorn.getId());
			ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			model.addAttribute("expedientTipus", expedientTipus);
			model.addAttribute("definicioProcesId", definicioProcesId);
			if (potIniciarExpedientTipus(expedientTipus)) {
				ExpedientTascaDto tasca = (ExpedientTascaDto) model.get("tasca");
				List<CampDto> camps = new ArrayList<CampDto>();
				for (CampTascaDto campTasca : tasca.getCamps()) {
					camps.add(campTasca.getCamp());
				}
				Map<String, Object> valorsCommand = TascaFormHelper.getValorsFromCommand(camps, command, true, false);
				ExpedientTascaDto tascaInicial = obtenirTascaInicial(
						entorn.getId(),
						expedientTipusId,
						definicioProcesId,
						valorsCommand,
						request);
				model.addAttribute("tasca", tascaInicial);
				model.addAttribute("dades", tascaService.findDadesPerTascaDto(tascaInicial));				

				this.validatorValidar = new TascaFormValidatorHelper(
						expedientService,
						obtenirValorsRegistresSessio(request, camps));
				
				validatorValidar.validate(command, result);
				try {
					Validator validator = TascaFormHelper.getBeanValidatorForCommand(camps);
					validator.validate(TascaFormHelper.getCommandForCamps(camps,valorsCommand,null,null,false), result);
				} catch (Exception ex) {
					logger.error("S'han produit errors de validació", ex);
					MissatgesHelper.error(request, getMessage(request, "error.validacio"));
					return "v3/expedient/iniciarPasForm";
				}
				if (result.hasErrors()) {
					model.addAttribute(
		        			"valorsPerSuggest",
		        			TascaFormHelper.getValorsPerSuggest(tascaInicial, command));
					for ( ObjectError res : result.getAllErrors()) {
						String error = (res.getDefaultMessage() == null || res.getDefaultMessage().isEmpty()) ? getMessage(request, "error.validacio") : res.getDefaultMessage();
						MissatgesHelper.error(request, error);
					}
					return "v3/expedient/iniciarPasForm";
				}
				
				// Si l'expedient ha de demanar titol i/o número redirigeix al pas per demanar
				// aquestes dades
				if (expedientTipus.isDemanaNumero() || expedientTipus.isDemanaTitol() || expedientTipus.isSeleccionarAny()) {
					guardarCommandSessio(request, command);
					if (definicioProcesId != null) {
						model.addAttribute("definicioProcesId", definicioProcesId);
					}
					
					omplirModelPerMostrarFormulari(expedientTipus, model);
					model.addAttribute("any", Calendar.getInstance().get(Calendar.YEAR));
					model.addAttribute("numero", expedientService.getNumeroExpedientActual(
							entorn.getId(),
							expedientTipus,
							Calendar.getInstance().get(Calendar.YEAR)));
					model.addAttribute("responsableCodi", expedientTipus.getResponsableDefecteCodi());
					
					return "v3/expedient/iniciarPasTitol";
				} else {
					try {
						Map<String, Object> valors = new HashMap<String, Object>();
						valors.putAll(obtenirValorsSessio(request));
						valors.putAll(TascaFormHelper.getValorsFromCommand(camps, command, true, false));
						ExpedientDto iniciat = iniciarExpedient(
								entorn.getId(),
								expedientTipusId,
								definicioProcesId,
								(String)request.getSession().getAttribute(ExpedientInicioController.CLAU_SESSIO_NUMERO),
								(String)request.getSession().getAttribute(ExpedientInicioController.CLAU_SESSIO_TITOL),
								(Integer)request.getSession().getAttribute(ExpedientInicioController.CLAU_SESSIO_ANY),
								valors);
						MissatgesHelper.info(request, getMessage(request, "info.expedient.iniciat", new Object[] {iniciat.getIdentificador()}));
					    ExpedientInicioController.netejarSessio(request);
					    return "redirect:/v3/expedient/iniciar";
					} catch (Exception ex) {
						MissatgesHelper.error(request, getMessage(request, "error.iniciar.expedient"));
						logger.error("No s'ha pogut iniciar l'expedient", ex);
						return "v3/expedient/iniciarPasForm";
					}
				}
			} else {
			MissatgesHelper.error(request, getMessage(request, "error.permisos.iniciar.tipus.exp") );
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec"));			
		}
		
		return "v3/expedient/iniciar";
	}

	private void omplirModelPerMostrarFormulari(
			ExpedientTipusDto tipus,
			ModelMap model) {
		model.addAttribute(
				"responsable",
				pluginService.findPersonaAmbCodi(tipus.getResponsableDefecteCodi()));
		model.addAttribute(
				"expedientTipus",
				tipus);
		Integer[] anys = new Integer[10];
		int anyActual = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < 10; i++) {
			anys[i] = new Integer(anyActual - i);
		}
		model.addAttribute("anysSeleccionables", anys);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> obtenirValorsSessio(
			HttpServletRequest request) {
		return (Map<String, Object>)request.getSession().getAttribute(
				ExpedientInicioController.CLAU_SESSIO_FORM_VALORS);
	}
	
	private void guardarCommandSessio(
			HttpServletRequest request,
			Object command) {
		request.getSession().setAttribute(
				ExpedientInicioController.CLAU_SESSIO_FORM_COMMAND,
				command);
	}

	private ExpedientTascaDto obtenirTascaInicial(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors,
			HttpServletRequest request) {
		ExpedientTascaDto tasca = expedientService.getStartTask(
				entornId,
				expedientTipusId,
				definicioProcesId,
				valors);
		tasca.setId(
				(String)request.getSession().getAttribute(
						ExpedientInicioController.CLAU_SESSIO_TASKID));
		Object validat = request.getSession().getAttribute(
				ExpedientInicioController.CLAU_SESSIO_FORM_VALIDAT); 
		if (validat != null)
			tasca.setValidada(true);
		return tasca;
	}
	
	private synchronized ExpedientDto iniciarExpedient(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			String numero,
			String titol,
			Integer any,
			Map<String, Object> valors) {
		return expedientService.iniciar(
				entornId,
				null,
				expedientTipusId,
				definicioProcesId,
				any,
				numero,
				titol,
				null,
				null,
				null,
				null,
				false,
				null,
				null,
				null,
				null,
				null,
				null,
				false,
				null,
				null,
				false,
				valors,
				null,
				IniciadorTipusDto.INTERN,
				null,
				null,
				null,
				null);
	}

	private static final Log logger = LogFactory.getLog(ExpedientInicioPasFormController.class);
}
