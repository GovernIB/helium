/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.TascaFormUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador pel pas del titol de l'inici d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientIniciarPasTitolController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private PermissionService permissionService;
	private PluginService pluginService;

	private Validator validator;



	@Autowired
	public ExpedientIniciarPasTitolController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			PluginService pluginService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.pluginService = pluginService;
		validator = new ExpedientIniciarPasTitolValidator(
				dissenyService,
				expedientService);
	}

	@ModelAttribute("command")
	public ExpedientIniciarPasTitolCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId) {
		Entorn entorn = getEntornActiu(request);
		if (expedientTipusId != null) {
			ExpedientIniciarPasTitolCommand command = new ExpedientIniciarPasTitolCommand();
			command.setAny(Calendar.getInstance().get(Calendar.YEAR));
			command.setExpedientTipusId(expedientTipusId);
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			command.setNumero(
					expedientService.getNumeroExpedientActual(
							entorn.getId(),
							expedientTipusId,
							command.getAny()));
			command.setResponsableCodi(expedientTipus.getResponsableDefecteCodi());
			return command;
		}
		return null;
	}

	/*@ModelAttribute("tascaInicial")
	public TascaDto populateStartTask(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			return expedientService.getStartTask(
	        		entorn.getId(),
	        		expedientTipusId,
	        		definicioProcesId,
	        		null);
		}
		return null;
	}*/

	@RequestMapping(value = "/expedient/iniciarPasTitol", method = RequestMethod.GET)
	public String iniciarPasTitolGet(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus tipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potIniciarExpedientTipus(tipus)) {
				omplirModelPerMostrarFormulari(tipus, model);
				return "expedient/iniciarPasTitol";
			} else {
				missatgeError(request, getMessage("error.permisos.iniciar.tipus.exp"));
				return "redirect:/expedient/iniciar.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedient/iniciarPasTitol", method = RequestMethod.POST)
	public String iniciarPasTitolPost(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "nomesRefrescar", required = false) Boolean nomesRefrescar,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientIniciarPasTitolCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus tipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potIniciarExpedientTipus(tipus)) {
				if (nomesRefrescar != null && nomesRefrescar.booleanValue()) {
					command.setNumero(
							expedientService.getNumeroExpedientActual(
									entorn.getId(),
									expedientTipusId,
									command.getAny()));
					omplirModelPerMostrarFormulari(tipus, model);
					return "expedient/iniciarPasTitol";
				} else if ("submit".equals(submit) || submit.length() == 0) {
					command.setEntornId(entorn.getId());
					validator.validate(command, result);
					if (result.hasErrors()) {
						omplirModelPerMostrarFormulari(tipus, model);
						return "expedient/iniciarPasTitol";
					}
					/*// Si l'expedient requereix dades inicials redirigeix al pas per demanar 
					// aquestes dades
					DefinicioProcesDto definicioProces = null;
					if (definicioProcesId != null)
						definicioProces = dissenyService.getById(definicioProcesId, true);
					else
						definicioProces = dissenyService.findDarreraDefinicioProcesForExpedientTipus(expedientTipusId, true);
					if (definicioProces.isHasStartTask()) {
						request.getSession().setAttribute(ExpedientIniciarController.CLAU_SESSIO_TITOL, command.getTitol());
						request.getSession().setAttribute(ExpedientIniciarController.CLAU_SESSIO_NUMERO, command.getNumero());
						if (definicioProcesId != null)
							return "redirect:/expedient/iniciarPasForm.html?expedientTipusId=" + expedientTipusId + "&definicioProcesId=" + definicioProcesId;
						else
							return "redirect:/expedient/iniciarPasForm.html?expedientTipusId=" + expedientTipusId;
					}
					// Si no requereix dades inicials inicia l'expedient*/
					try {
						ExpedientDto iniciat = iniciarExpedient(
								request,
								entorn.getId(),
								command.getExpedientTipusId(),
								definicioProcesId,
								command.getNumero(),
								command.getTitol(),
								command.getAny());
					    missatgeInfo(
					    		request,
					    		getMessage(
										"info.expedient.iniciat",
										new Object[] {iniciat.getIdentificador()}));
					    ExpedientIniciarController.netejarSessio(request);
					    return "redirect:/expedient/iniciar.html";
					} catch (Exception ex) {
						missatgeError(
								request,
								getMessage("error.iniciar.expedient"),
								(ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage());
						logger.error("No s'ha pogut iniciar l'expedient", ex);
						model.addAttribute("expedientTipus", dissenyService.getExpedientTipusById(command.getExpedientTipusId()));
						return "expedient/iniciarPasTitol";
					}
				} else {
					return "redirect:/expedient/iniciar.html";
				}
			} else {
				missatgeError(request, getMessage("error.permisos.iniciar.tipus.exp"));
				return "redirect:/expedient/iniciar.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	protected class ExpedientIniciarPasTitolValidator implements Validator {
		private DissenyService dissenyService;
		private ExpedientService expedientService;
		public ExpedientIniciarPasTitolValidator(DissenyService dissenyService, ExpedientService expedientService) {
			this.dissenyService = dissenyService;
			this.expedientService = expedientService;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientIniciarPasTitolCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ExpedientIniciarPasTitolCommand command = (ExpedientIniciarPasTitolCommand)target;
			ExpedientTipus tipus = dissenyService.getExpedientTipusById(command.getExpedientTipusId());
			if (tipus == null) {
				errors.reject("error.expedienttipus.desconegut");
			} else {
				boolean teNumero = (tipus.getTeNumero().booleanValue() && tipus.getDemanaNumero().booleanValue());
				if (teNumero && (command.getNumero() == null || command.getNumero().length() == 0))
					errors.rejectValue("numero", "not.blank");
				boolean teTitol = (tipus.getTeTitol().booleanValue() && tipus.getDemanaTitol().booleanValue());
				if (teTitol && (command.getTitol() == null || command.getTitol().length() == 0))
					errors.rejectValue("titol", "not.blank");
				if (teTitol && expedientService.findExpedientAmbEntornTipusITitol(
						command.getEntornId(),
						command.getExpedientTipusId(),
						command.getTitol()) != null) {
					errors.rejectValue("titol", "error.expedient.titolrepetit");
				}
				if (teNumero && expedientService.findExpedientAmbEntornTipusINumero(
						command.getEntornId(),
						command.getExpedientTipusId(),
						command.getNumero()) != null) {
					errors.rejectValue("numero", "error.expedient.numerorepetit");
				}
			}
		}
	}



	private void omplirModelPerMostrarFormulari(
			ExpedientTipus tipus,
			ModelMap model) {
		model.addAttribute(
				"responsable",
				getPersonaAmbCodi(tipus.getResponsableDefecteCodi()));
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

	private boolean potIniciarExpedientTipus(ExpedientTipus expedientTipus) {
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.CREATE}) != null;
	}

	private PersonaDto getPersonaAmbCodi(String codi) {
		if (codi == null)
			return null;
		return pluginService.findPersonaAmbCodi(codi);
	}

	@SuppressWarnings("unchecked")
	private synchronized ExpedientDto iniciarExpedient(
			HttpServletRequest request,
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			String numero,
			String titol,
			Integer any) {
		Map<String, Object> valors = null;
		Map<String, Object> valorsSessio = (Map<String, Object>)request.getSession().getAttribute(
				ExpedientIniciarController.CLAU_SESSIO_FORM_VALORS);
		Object command = request.getSession().getAttribute(
				ExpedientIniciarController.CLAU_SESSIO_FORM_COMMAND);
		if (valorsSessio != null || command != null) {
			valors = new HashMap<String, Object>();
			if (valorsSessio != null)
				valors.putAll(valorsSessio);
			if (command != null) {
				TascaDto tascaInicial = expedientService.getStartTask(
						entornId,
						expedientTipusId,
						definicioProcesId,
						null);
				List<Camp> camps = new ArrayList<Camp>();
				for (CampTasca campTasca: tascaInicial.getCamps())
					camps.add(campTasca.getCamp());
				valors.putAll(TascaFormUtil.getValorsFromCommand(
						camps,
						command,
						true,
						false));
			}
		}
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
				IniciadorTipus.INTERN,
				null,
				null,
				null,
				null);
	}

	private static final Log logger = LogFactory.getLog(ExpedientIniciarPasTitolController.class);

}
