/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
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
 * Controlador per la gestió de la integració entre el tipus
 * d'expedient i Sistra
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTipusSistraController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;



	@Autowired
	public ExpedientTipusSistraController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public ExpedientTipusSistraCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		ExpedientTipusSistraCommand command = new ExpedientTipusSistraCommand();
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			command.setExpedientTipusId(expedientTipusId);
			if (expedientTipus.getSistraTramitCodi() != null) {
				command.setCodiTramit(expedientTipus.getSistraTramitCodi());
				command.setInfoMapeigCamps(expedientTipus.getSistraTramitMapeigCamps());
				command.setInfoMapeigDocuments(expedientTipus.getSistraTramitMapeigDocuments());
				command.setInfoMapeigAdjunts(expedientTipus.getSistraTramitMapeigAdjunts());
				command.setActiu(true);
				
			} else {
				command.setActiu(false);
			}
		}
		return command;
	}
	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		return dissenyService.getExpedientTipusById(expedientTipusId);
	}

	@RequestMapping(value = "/expedientTipus/sistra", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				return "expedientTipus/sistra";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedientTipus/sistra", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@ModelAttribute("command") ExpedientTipusSistraCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					new ExpedientTipusSistraValidator(dissenyService).validate(command, result);
			        if (result.hasErrors()) {
			        	return "expedientTipus/sistra";
			        }
			        try {
			        	if (command.isActiu()) {
			        		expedientTipus.setSistraTramitCodi(command.getCodiTramit());
			        		expedientTipus.setSistraTramitMapeigCamps(command.getInfoMapeigCamps());
			        		expedientTipus.setSistraTramitMapeigDocuments(command.getInfoMapeigDocuments());
			        		expedientTipus.setSistraTramitMapeigAdjunts(command.getInfoMapeigAdjunts());
			        	} else {
			        		expedientTipus.setSistraTramitCodi(null);
			        		expedientTipus.setSistraTramitMapeigCamps(null);
			        		expedientTipus.setSistraTramitMapeigDocuments(null);
			        		expedientTipus.setSistraTramitMapeigAdjunts(null);
			        	}
		        		dissenyService.updateExpedientTipus(expedientTipus);
			        	missatgeInfo(request, getMessage("info.informacio.guardat"));
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        }
				}
				return "redirect:/expedientTipus/sistra.html?expedientTipusId=" + expedientTipusId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	private class ExpedientTipusSistraValidator implements Validator {
		private DissenyService dissenyService;
		public ExpedientTipusSistraValidator(DissenyService dissenyService) {
			this.dissenyService = dissenyService;
		}
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(ExpedientTipusSistraCommand.class);
		}
		public void validate(Object target, Errors errors) {
			ExpedientTipusSistraCommand command = (ExpedientTipusSistraCommand)target;
			if (command.isActiu()) {
				ValidationUtils.rejectIfEmpty(errors, "codiTramit", "not.blank");
				if (command.getCodiTramit() != null) {
					List<ExpedientTipus> repetits = dissenyService.findExpedientTipusAmbSistraTramitCodi(command.getCodiTramit());
					if (repetits.size() > 0) {
						if (repetits.get(0).getId().longValue() != command.getExpedientTipusId().longValue())
							errors.rejectValue("codiTramit", "error.expedienttipus.sistra.repetit");
					}
				}
				if (command.getInfoMapeigCamps() != null && command.getInfoMapeigCamps().length() > 2048) {
					errors.rejectValue("infoMapeigCamps", "max.length");
				}
				if (command.getInfoMapeigDocuments() != null && command.getInfoMapeigDocuments().length() > 2048) {
					errors.rejectValue("infoMapeigDocuments", "max.length");
				}
				if (command.getInfoMapeigAdjunts() != null && command.getInfoMapeigAdjunts().length() > 2048) {
					errors.rejectValue("infoMapeigAdjunts", "max.length");
				}
			}
		}
	}

	private boolean potDissenyarExpedientTipus(Entorn entorn, ExpedientTipus expedientTipus) {
		if (potDissenyarEntorn(entorn))
			return true;
		return permissionService.filterAllowed(
				expedientTipus,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}
	private boolean potDissenyarEntorn(Entorn entorn) {
		return permissionService.filterAllowed(
				entorn,
				Entorn.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.DESIGN}) != null;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusSistraController.class);

}
