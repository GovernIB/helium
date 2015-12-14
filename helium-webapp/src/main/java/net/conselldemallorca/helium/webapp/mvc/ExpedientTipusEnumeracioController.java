/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
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
public class ExpedientTipusEnumeracioController extends BaseController {

	private DissenyService dissenyService;
	private PermissionService permissionService;
	private Validator annotationValidator;


	@Autowired
	public ExpedientTipusEnumeracioController(
			DissenyService dissenyService,
			ExpedientService expedientService,
			PermissionService permissionService,
			PluginService pluginService) {
		this.dissenyService = dissenyService;
		this.permissionService = permissionService;
	}


	@ModelAttribute("command")
	public Enumeracio populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id) {
		if (id != null)
			return dissenyService.getEnumeracioById(id);
		return new Enumeracio();
	}
	@ModelAttribute("expedientTipus")
	public ExpedientTipus populateExpedientTipus(
			@RequestParam(value = "expedientTipusId", required = false) Long id) {
		if (id != null) {
			return dissenyService.getExpedientTipusById(id);
		}
		return null;
	}

	@RequestMapping(value = "/expedientTipus/enumeracioLlistat")
	public String llistat(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("llistat", dissenyService.findEnumeracionsAmbEntornITipusExp(entorn.getId(),expedientTipusId));
			return "expedientTipus/enumeracioLlistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "/expedientTipus/enumeracioForm", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);			
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				return "expedientTipus/enumeracioForm";
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/expedientTipus/enumeracioForm", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId,
			@ModelAttribute("command") Enumeracio command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				if ("submit".equals(submit) || submit.length() == 0) {
					command.setEntorn(entorn);
					command.setExpedientTipus(expedientTipus);
					annotationValidator.validate(command, result);
					new EnumeracioValidator(dissenyService).validateSenseExpTipus(command, result,expedientTipusId );
			        if (result.hasErrors()) {
			        	return "expedientTipus/enumeracioForm";
			        }
			        try {
			        	if (command.getId() == null)
			        		dissenyService.createEnumeracio(command);
			        	else
			        		dissenyService.updateEnumeracio(command);
			        	missatgeInfo(request, getMessage("info.enum.guardat") );
			        	status.setComplete();
			        } catch (Exception ex) {
			        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
			        	logger.error("No s'ha pogut guardar el registre", ex);
			        	return "expedientTipus/enumeracioForm";
			        }
				}
				return "redirect:/expedientTipus/enumeracioLlistat.html?expedientTipusId=" + expedientTipusId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/expedientTipus/enumeracioEsborrar")
	public String delete(
			HttpServletRequest request,
			@RequestParam(value = "enumeracioId", required = true) Long enumeracioId,
			@RequestParam(value = "expedientTipusId", required = true) Long expedientTipusId) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			ExpedientTipus expedientTipus = dissenyService.getExpedientTipusById(expedientTipusId);
			if (potDissenyarExpedientTipus(entorn, expedientTipus)) {
				try {
					Enumeracio enumeracio = dissenyService.getEnumeracioById(enumeracioId);
					if (enumeracio.getCamps().size() > 0) {
						missatgeError(request, getMessage("error.enumeracio.emprada.camp") );
					} else {
						dissenyService.deleteEnumeracio(enumeracioId);
						missatgeInfo(request, getMessage("info.enum.esborrat") );
					}
				} catch (Exception ex) {
					missatgeError(request, getMessage("error.esborrar.enum"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut esborrar l'enumeració", ex);
				}
				return "redirect:/expedientTipus/enumeracioLlistat.html?expedientTipusId=" + expedientTipusId;
			} else {
				missatgeError(request, getMessage("error.permisos.disseny.tipus.exp"));
				return "redirect:/index.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
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

	private static final Log logger = LogFactory.getLog(ExpedientTipusEnumeracioController.class);

}
