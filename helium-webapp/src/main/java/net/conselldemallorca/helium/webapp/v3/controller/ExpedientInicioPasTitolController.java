/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.PermissionService;
import net.conselldemallorca.helium.v3.core.service.PluginServiceImpl;
import net.conselldemallorca.helium.webapp.v3.command.ExpedientInicioPasTitolCommand;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;
import net.conselldemallorca.helium.webapp.v3.helper.SessionHelper;
import net.conselldemallorca.helium.webapp.v3.helper.TascaFormHelper;

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
@RequestMapping("/v3/expedient")
public class ExpedientInicioPasTitolController extends BaseExpedientController {
	@Resource(name="dissenyServiceV3")
	private DissenyService dissenyService;
	@Resource(name="expedientServiceV3")
	private ExpedientService expedientService;
	@Resource(name="permissionServiceV3")
	private PermissionService permissionService;
	@Resource(name = "pluginServiceV3")
	private PluginServiceImpl pluginService;

	private Validator validator;



	@Autowired
	public ExpedientInicioPasTitolController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			PluginServiceImpl pluginService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.permissionService = permissionService;
		this.pluginService = pluginService;
		validator = new ExpedientInicioPasTitolValidator(
				dissenyService,
				expedientService);
	}

	@ModelAttribute("command")
	public ExpedientInicioPasTitolCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (expedientTipusId != null) {
			ExpedientInicioPasTitolCommand command = new ExpedientInicioPasTitolCommand();
			command.setAny(Calendar.getInstance().get(Calendar.YEAR));
			command.setExpedientTipusId(expedientTipusId);
			ExpedientTipusDto expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			command.setNumero(
					expedientService.getNumeroExpedientActual(
							entorn.getId(),
							expedientTipus,
							command.getAny()));
			command.setResponsableCodi(expedientTipus.getResponsableDefecteCodi());
			return command;
		}
		return null;
	}

	@RequestMapping(value = "/iniciarPasTitol", method = RequestMethod.GET)
	public String iniciarPasTitolGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long tascaId,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientTipusDto tipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potIniciarExpedientTipus(tipus)) {
				omplirModelPerMostrarFormulari(tipus, model);
				return "expedient";
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.iniciar.tipus.exp"));
				return "redirect:/expedient/iniciar";
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
			return "redirect:/expedient/iniciar";
		}
	}

	@RequestMapping(value = "/iniciarPasTitol", method = RequestMethod.POST)
	public String iniciarPasTitolPost(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@RequestParam(value = "definicioProcesId", required = false) Long definicioProcesId,
			@RequestParam(value = "nomesRefrescar", required = false) Boolean nomesRefrescar,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientInicioPasTitolCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		EntornDto entorn = SessionHelper.getSessionManager(request).getEntornActual();
		if (entorn != null) {
			ExpedientTipusDto tipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potIniciarExpedientTipus(tipus)) {
				if (nomesRefrescar != null && nomesRefrescar.booleanValue()) {
					command.setNumero(
							expedientService.getNumeroExpedientActual(
									entorn.getId(),
									tipus,
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
					try {
						ExpedientDto iniciat = iniciarExpedient(
								request,
								entorn.getId(),
								command.getExpedientTipusId(),
								definicioProcesId,
								command.getNumero(),
								command.getTitol(),
								command.getAny());
						MissatgesHelper.error(request, getMessage(request, "info.expedient.iniciat"));
					    ExpedientInicioController.netejarSessio(request);
					    return "redirect:/expedient/iniciar.html";
					} catch (Exception ex) {
						MissatgesHelper.error(request, getMessage(request, "error.iniciar.expedient"));
						logger.error("No s'ha pogut iniciar l'expedient", ex);
						model.addAttribute("expedientTipus", dissenyService.getExpedientTipusById(command.getExpedientTipusId()));
						return "expedient/iniciarPasTitol";
					}
				} else {
					return "redirect:/expedient/iniciar.html";
				}
			} else {
				MissatgesHelper.error(request, getMessage(request, "error.permisos.iniciar.tipus.exp"));
				return "redirect:/expedient/iniciar.html";
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	protected class ExpedientInicioPasTitolValidator implements Validator {
		private DissenyService dissenyService;
		private ExpedientService expedientService;
		public ExpedientInicioPasTitolValidator(DissenyService dissenyService, ExpedientService expedientService) {
			this.dissenyService = dissenyService;
			this.expedientService = expedientService;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientInicioPasTitolCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ExpedientInicioPasTitolCommand command = (ExpedientInicioPasTitolCommand)target;
			ExpedientTipusDto tipus = dissenyService.getExpedientTipusById(command.getExpedientTipusId());
			if (tipus == null) {
				errors.reject("error.expedienttipus.desconegut");
			} else {
				boolean teNumero = (tipus.isTeNumero() && tipus.isDemanaNumero());
				if (teNumero && (command.getNumero() == null || command.getNumero().length() == 0))
					errors.rejectValue("numero", "not.blank");
				boolean teTitol = (tipus.isTeNumero() && tipus.isDemanaTitol());
				if (teTitol && (command.getTitol() == null || command.getTitol().length() == 0))
					errors.rejectValue("titol", "not.blank");
				if (teTitol && expedientService.findExpedientAmbEntornTipusITitol(
						command.getEntornId(),
						command.getExpedientTipusId(),
						command.getTitol()) != null) {
					errors.rejectValue("titol", "error.expedient.titolrepetit");
				}
				if (teNumero && expedientService.findExpedientAmbEntornTipusITitol(
						command.getEntornId(),
						command.getExpedientTipusId(),
						command.getNumero()) != null) {
					errors.rejectValue("numero", "error.expedient.numerorepetit");
				}
			}
		}
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

	private boolean potIniciarExpedientTipus(ExpedientTipusDto expedientTipus) {
		return permissionService.isGrantedAny(
				expedientTipus.getId(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.CREATE});
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
				ExpedientInicioController.CLAU_SESSIO_FORM_VALORS);
		Object command = request.getSession().getAttribute(
				ExpedientInicioController.CLAU_SESSIO_FORM_COMMAND);
		if (valorsSessio != null || command != null) {
			valors = new HashMap<String, Object>();
			if (valorsSessio != null)
				valors.putAll(valorsSessio);
			if (command != null) {
				ExpedientTascaDto tascaInicial = expedientService.getStartTask(
						entornId,
						expedientTipusId,
						definicioProcesId,
						null);
				List<CampDto> camps = new ArrayList<CampDto>();
				for (CampTascaDto campTasca: tascaInicial.getCamps())
					camps.add(campTasca.getCamp());
				valors.putAll(TascaFormHelper.getValorsFromCommand(
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
				IniciadorTipusDto.INTERN,
				null,
				null,
				null,
				null);
	}

	private static final Log logger = LogFactory.getLog(ExpedientInicioPasTitolController.class);

}
