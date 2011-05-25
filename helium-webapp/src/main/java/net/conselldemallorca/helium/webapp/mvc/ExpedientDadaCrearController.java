/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per a crear dades per als expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientDadaCrearController extends BaseController {

	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private TascaService tascaService;
	private PermissionService permissionService;

	private Validator validator;



	@Autowired
	public ExpedientDadaCrearController(DissenyService dissenyService,
			ExpedientService expedientService,
			TascaService tascaService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.expedientService = expedientService;
		this.tascaService = tascaService;
		this.permissionService = permissionService;
		this.validator = new ExpedientDadaCrearValidator();
	}

	@ModelAttribute("command")
	public Object populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "taskId", required = false) String taskId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDadaCrearCommand command = new ExpedientDadaCrearCommand();
			command.setId(id);
			command.setTaskId(taskId);
			command.setModificar(true);
			return command;
		}
		return null;
	}
	@ModelAttribute("camps")
	public List<Camp> populateCamps(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "taskId", required = false) String taskId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if (taskId == null) {
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(
						id,
						true);
				List<Camp> camps = dissenyService.findCampsAmbDefinicioProcesOrdenatsPerCodi(instanciaProces.getDefinicioProces().getId());
				Iterator<Camp> it = camps.iterator();
				while (it.hasNext()) {
					Camp camp = it.next();
					if (instanciaProces.getVariables() != null && instanciaProces.getVariables().containsKey(camp.getCodi()))
						it.remove();
				}
				return camps;
			} else {
				TascaDto tasca = tascaService.getById(entorn.getId(), taskId);
				List<Camp> camps = dissenyService.findCampsAmbDefinicioProcesOrdenatsPerCodi(tasca.getDefinicioProces().getId());
				Iterator<Camp> it = camps.iterator();
				while (it.hasNext()) {
					Camp camp = it.next();
					if (tasca.getVariables().containsKey(camp.getCodi()))
						it.remove();
				}
				return camps;
			}
		}
		return null;
	}

	@RequestMapping(value = "/expedient/dadaCrear", method = RequestMethod.GET)
	public String dadaCrear(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "taskId", required = false) String taskId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = getExpedient(entorn.getId(), id, taskId);
			model.addAttribute("expedient", expedient);
			return "expedient/dadaCrear";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/dadaCrear", method = RequestMethod.POST)
	public String dadaCrear(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "taskId", required = false) String taskId,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientDadaCrearCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = getExpedient(entorn.getId(), id, taskId);
			if (potModificarExpedient(expedient)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					validator.validate(command, result);
					if (result.hasErrors()) {
						model.addAttribute("expedient", expedient);
						return "expedient/dadaCrear";
					}
					String var = null;
					if (command.getCamp() != null)
						var = command.getCamp().getCodi();
					if (var == null)
						var = command.getVarCodi();
					if (id != null) {
				        expedientService.createVariable(
				        		id,
				        		var,
				        		null);
					} else {
						tascaService.createVariable(
								entorn.getId(),
								taskId,
								var,
								null);
					}
					missatgeInfo(request, "La dada s'ha creat correctament");
					if (command.isModificar()) {
						if (id != null)
							return "redirect:/expedient/dadaModificar.html?id=" + id + "&var=" + var;
						else
							return "redirect:/expedient/dadaModificar.html?taskId=" + taskId + "&var=" + var;
					}
				}
				return "redirect:/expedient/dades.html?id=" + getUrlParamId(entorn.getId(), id, taskId);
			} else {
				missatgeError(request, "No té permisos per modificar aquest expedient");
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Camp.class,
				new CampTypeEditor(dissenyService));
	}

	public class ExpedientDadaCrearValidator implements Validator {
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Object.class);
		}
		public void validate(Object command, Errors errors) {
			ExpedientDadaCrearCommand edcCommand = (ExpedientDadaCrearCommand)command;
			if (edcCommand.getCamp() == null || edcCommand.getCamp().equals("")) {
				if (edcCommand.getVarCodi() == null || edcCommand.getVarCodi().equals("")) {
					errors.rejectValue("varCodi", "not.blank");
				}
			}
		}
	}



	private boolean potModificarExpedient(ExpedientDto expedient) {
		return permissionService.filterAllowed(
				expedient.getTipus(),
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.WRITE}) != null;
	}
	private ExpedientDto getExpedient(Long entornId, String id, String taskId) {
		if (id != null)
			return expedientService.findExpedientAmbProcessInstanceId(id);
		if (taskId != null) {
			TascaDto tasca = tascaService.getById(entornId, taskId);
			return expedientService.findExpedientAmbProcessInstanceId(tasca.getProcessInstanceId());
		}
		return null;
	}
	private String getUrlParamId(Long entornId, String id, String taskId) {
		if (id != null)
			return id;
		if (taskId != null) {
			TascaDto tasca = tascaService.getById(entornId, taskId);
			if (tasca != null)
				return tasca.getProcessInstanceId();
		}
		return null;
	}

}
