/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controlador per la gestió de tipus d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ExpedientTipusEnumeracioValorsController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;


	@Autowired
	public ExpedientTipusEnumeracioValorsController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			PluginService pluginService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}

	@ModelAttribute("command")
	public EnumeracioValorsCommand populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id) {
		EnumeracioValorsCommand command = new EnumeracioValorsCommand();
		if (id != null) {
			EnumeracioValors enumeracioValors = dissenyService.getEnumeracioValorsById(id);
			if (enumeracioValors != null) {
				command.setId(enumeracioValors.getId());
				command.setCodi(enumeracioValors.getCodi());
				command.setNom(enumeracioValors.getNom());
				command.setEnumeracioId(enumeracioValors.getEnumeracio().getId());
			}
			return command;
		} else {
			return command;
		}
	}
	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "enumeracio", required = false) Long enumeracioId) {
		if (enumeracioId != null) {
			Enumeracio enumeracio = dissenyService.getEnumeracioById(enumeracioId);
			return enumeracio.getExpedientTipus();
		}
		return null;
	}

	@RequestMapping(value = "/expedientTipus/enumeracioValors", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "enumeracio", required = true) Long enumeracioId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			Enumeracio enumeracio = dissenyService.getEnumeracioById(enumeracioId);
			if (potDissenyarExpedientTipus(entorn, enumeracio.getExpedientTipus())) {
				if ((enumeracioId != null) && !(enumeracioId.equals(""))) {
					model.addAttribute("llistat", dissenyService.findEnumeracioValorsAmbEnumeracio(enumeracioId));
					model.addAttribute("enumeracio", enumeracioId);
					return "expedientTipus/enumeracioValors";
				}else {
					missatgeError(request, getMessage("error.no.enumeracio.selec") );
					return "redirect:/expedientTipus/enumeracioLlistat.html?expedientTipusId=" + enumeracio.getExpedientTipus().getId();
				}
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "/expedientTipus/enumeracioValors", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "enumeracio", required = true) Long enumeracioId,
			@ModelAttribute("command") EnumeracioValorsCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ((enumeracioId != null) && !(enumeracioId.equals(""))) {
				Enumeracio enumeracio = dissenyService.getEnumeracioById(enumeracioId);
				if (potDissenyarExpedientTipus(entorn, enumeracio.getExpedientTipus())) {
					if ("submit".equals(submit) || submit.length() == 0) {
						command.setEnumeracioId(enumeracio.getId());
						annotationValidator.validate(command, result);
						new EnumeracioValorsValidator(dissenyService).validate(command, result);
				        if (result.hasErrors()) {
				        	model.addAttribute("llistat", dissenyService.findEnumeracioValorsAmbEnumeracio(enumeracioId));
				        	model.addAttribute("enumeracio", enumeracioId);
				        	return "expedientTipus/enumeracioValors";
				        }
				        try {
				        	EnumeracioValors enumeracioValors = new EnumeracioValors();
				        	enumeracioValors.setId(command.getId());
				        	enumeracioValors.setCodi(command.getCodi());
				        	enumeracioValors.setNom(command.getNom());
				        	enumeracioValors.setEnumeracio(enumeracio);
				        	
				        	if (command.getId() == null)
				        		dissenyService.createEnumeracioValors(enumeracioValors);
				        	else
				        		dissenyService.updateEnumeracioValors(enumeracioValors);
				        	missatgeInfo(request, getMessage("info.enum.guardat") );
				        	status.setComplete();
				        } catch (Exception ex) {
				        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
				        	logger.error("No s'ha pogut guardar el registre", ex);
				        	return "expedientTipus/enumeracioValors";
				        }
					} 
					model.addAttribute("enumeracio", enumeracioId);
					return "redirect:/expedientTipus/enumeracioValors.html";
				} else {
					missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
					return "redirect:/index.html";
				}			
			} else {
				missatgeError(request, getMessage("error.no.enumeracio.selec") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/enumeracioValorsEsborrar")
	public String delete(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			EnumeracioValors enumeracioValors = dissenyService.getEnumeracioValorsById(id);
			if (enumeracioValors != null){
				if (potDissenyarExpedientTipus(entorn, enumeracioValors.getEnumeracio().getExpedientTipus())) {
					try {
						dissenyService.deleteEnumeracioValors(id);
						missatgeInfo(request, getMessage("info.enum.esborrat") );
					} catch (Exception ex) {
						missatgeError(request, getMessage("error.esborrar.enum"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut esborrar el valor de l'enumeració", ex);
					}
					return "redirect:/expedientTipus/enumeracioValors.html?enumeracio=" + enumeracioValors.getEnumeracio().getId();
				} else {
					missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
					return "redirect:/index.html";
				}
			} else {
				missatgeError(request, getMessage("error.no.enumeracio.selec") );
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
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
	
	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}

	private static final Log logger = LogFactory.getLog(ExpedientTipusEnumeracioValorsController.class);

}
