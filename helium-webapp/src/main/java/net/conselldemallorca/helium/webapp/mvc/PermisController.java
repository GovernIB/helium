package net.conselldemallorca.helium.webapp.mvc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Permis;
import net.conselldemallorca.helium.core.model.service.PermisService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
 * Controlador per a la gestió de rols.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Controller
@RequestMapping("/rol/*.html")
public class PermisController extends BaseController {

	private PermisService permisService;
	private Validator annotationValidator;
	private Validator additionalValidator;

	private String codi = "";

	@Autowired
	public PermisController(
			PermisService permissionService) {
		this.permisService = permissionService;
		additionalValidator = new PermisValidator(permisService);
	}

	@ModelAttribute("command")
	public PermisCommand populateCommand(@RequestParam(value = "codi", required = false) String codi) {
		if ((codi != null) && (!codi.equals(""))) {
			Permis permis = permisService.getPermisByCodi(codi);
			if (permis != null) {
				PermisCommand command = new PermisCommand();
				command.setCodi(permis.getCodi());
				command.setDescripcio(permis.getDescripcio());
				return command;
			} else {
				return new PermisCommand();
			}
		}
		return new PermisCommand();
	}

	@RequestMapping(value = "llistat")
	public String llistat(
			HttpServletRequest request,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			ModelMap model) {
		int pagina = (page != null) ? new Integer(page).intValue() : 1;
		int firstRow = (pagina - 1) * getObjectsPerPage(objectsPerPage);
		boolean isAsc = "asc".equals(dir);
		model.addAttribute(
				"llistat",
				newPaginatedList(
						pagina,
						sort,
						isAsc,
						getObjectsPerPage(objectsPerPage),
						permisService.countPermisosAll(),
						permisService.findPermisosPagedAndOrderedAll(
								sort,
								isAsc,
								firstRow,
								getObjectsPerPage(objectsPerPage))));
		return "rol/llistat";
	}

	@RequestMapping(value = "form", method = RequestMethod.GET)
	public String formGet() {
		return "rol/form";
	}

	@RequestMapping(value = "form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@RequestParam(value = "id", required = false) String id,
			@ModelAttribute("command") PermisCommand command,
			BindingResult result,
			SessionStatus status) {
		if ("submit".equals(submit) || (submit.length() == 0)) {
			codi = id;
			annotationValidator.validate(command, result);
			additionalValidator.validate(command, result);
			if (result.hasErrors()) {
	        	return "rol/form";
	        }
	        try {
	        	if ((command.getCodi() == null) || (command.getCodi().equals(""))) {
	        		permisService.createPermis(
	        				command.getCodi(),
	        				command.getDescripcio());
	        	} else {
	        		permisService.updatePermis(
	        				command.getCodi(),
	        				command.getDescripcio());
	        	}
	        	missatgeInfo(request, getMessage("info.rol.guardat") );
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar el registre", ex);
	        	return "rol/form";
	        }
	        return "redirect:/rol/llistat.html";
		} else {
			return "redirect:/rol/llistat.html";
		}
	}

	@RequestMapping(value = "delete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "codi", required = true) String codi) {
		if ((!codi.equalsIgnoreCase("HEL_ADMIN")) && (!codi.equalsIgnoreCase("HEL_USER"))) {
			Permis permis = permisService.getPermisByCodi(codi);
			if (permis.getUsuaris().size() > 0) {
				missatgeError(request, getMessage("error.exist.usuaris.rol"));
			} else {
				permisService.deletePermis(codi);
				missatgeInfo(request, getMessage("info.rol.esborrat") );
			}
		} else {
			missatgeError(request, getMessage("error.esborrar.rols.admin.usuari") );
		}
		return "redirect:/rol/llistat.html";
	}

	@RequestMapping(value = "test")
	public String test(
			HttpServletRequest request,
			ModelMap model) {
		List<String> roles = new ArrayList<String>();
		if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("HEL_ADMIN"))
			roles.add("HEL_ADMIN");
		if (request.isUserInRole("ROLE_USER") || request.isUserInRole("HEL_USER"))
			roles.add("HEL_USER");
		List<Permis> permisos = permisService.findAll();
		for (Permis permis: permisos) {
			if (request.isUserInRole(permis.getCodi())) {
				roles.add(permis.getCodi());
			}
		}
		model.addAttribute("roles", roles);
		return "rol/test";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				boolean.class,
				new CustomBooleanEditor(false));
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Permis.class,
				new PermisTypeEditor());
	}

	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}
	
	private class PermisValidator implements Validator {
		private PermisService permisService;
		
		public PermisValidator(PermisService permisService) {
			this.permisService = permisService;
		}
		
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(PermisCommand.class);
		}
		
		public void validate(Object target, Errors errors) {
			PermisCommand command = (PermisCommand)target;
			
			// El codi de rol no pot estar repetit
			Permis repetit = permisService.getPermisByCodi(command.getCodi());
			if ((repetit != null) && (!codi.equalsIgnoreCase(repetit.getCodi()))) {
				errors.rejectValue("codi", "error.permis.codi.repetit");
			}
		}
	}

	private static final Log logger = LogFactory.getLog(PersonaController.class);

}
