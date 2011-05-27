/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.jbpm3.integracio.ValidationException;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la reassignació de tasques dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTascaReassignarController extends BaseController {

	private ExpedientService expedientService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientTascaReassignarController(
			ExpedientService expedientService,
			PermissionService permissionService) {
		this.expedientService = expedientService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public ExpedientTascaReassignarCommand populateCommand(
			@RequestParam(value = "taskId", required = true) String taskId) {
		ExpedientTascaReassignarCommand command = new ExpedientTascaReassignarCommand();
		command.setTaskId(taskId);
		return command;
	}

	@RequestMapping(value = "/expedient/tascaReassignar", method = RequestMethod.GET)
	public String tascaReassignarGet(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "taskId", required = true) String taskId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				atributsModel(
	        			entorn,
	        			expedient,
	        			id,
	        			taskId,
	        			model);
				return "expedient/tascaReassignar";
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedient/tascaReassignar", method = RequestMethod.POST)
	public String tascaReassignarPost(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") ExpedientTascaReassignarCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(id);
			if (potModificarExpedient(expedient)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					new TascaReassignarValidator().validate(command, result);
			        if (result.hasErrors()) {
			        	atributsModel(
			        			entorn,
			        			expedient,
			        			id,
			        			command.getTaskId(),
			        			model);
			        	return "expedient/tascaReassignar";
			        }
					try {
						expedientService.reassignarTasca(
								entorn.getId(),
								command.getTaskId(),
								command.getExpression());
						missatgeInfo(request, getMessage("info.tasca.reassignada", new Object[] { command.getTaskId() } ) );
					} catch (Exception ex) {
						if (ex.getCause() != null && ex.getCause() instanceof ValidationException) {
							missatgeError(
				        			request,
				        			ex.getCause().getMessage());
						} else {
							missatgeError(
				        			request,
				        			getMessage("error.reassignar.tasca", new Object[] { command.getTaskId() } ),
				        			(ex.getCause() != null) ? ex.getCause().getMessage() : ex.getMessage());
						}
			        	logger.error("No s'ha pogut reassignar la tasca " + command.getTaskId(), ex);
					}
				}
				return "redirect:/expedient/tasques.html?id=" + id;
			} else {
				missatgeError(request, getMessage("error.permisos.modificar.expedient"));
				return "redirect:/expedient/consulta.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
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
	private class TascaReassignarValidator implements Validator {
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientTascaReassignarCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "expression", "not.blank");
		}
	}
	private void atributsModel(
			Entorn entorn,
			ExpedientDto expedient,
			String id,
			String taskId,
			ModelMap model) {
		model.addAttribute(
				"expedient",
				expedient);
		model.addAttribute(
				"arbreProcessos",
				expedientService.getArbreInstanciesProces(id, false));
		model.addAttribute(
				"instanciaProces",
				expedientService.getInstanciaProcesById(id, true));
		for (TascaDto tasca: expedientService.findTasquesPerInstanciaProces(id)) {
			if (tasca.getId().equals(taskId)) {
				model.addAttribute("tasca", tasca);
				break;
			}
		}
	}

	private static final Log logger = LogFactory.getLog(ExpedientTascaReassignarController.class);

}
