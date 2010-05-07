/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.model.service.PermissionService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Controlador per la gestió d'enumeracions
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class EnumeracioController extends BaseController {

	private DissenyService dissenyService;
	private Validator annotationValidator;



	@Autowired
	public EnumeracioController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService  = dissenyService;
	}

	@ModelAttribute("command")
	public Enumeracio populateCommand(
			HttpServletRequest request,
			@RequestParam(value = "id", required = false) Long id) {
		if (id != null)
			return dissenyService.getEnumeracioById(id);
		return new Enumeracio();
	}

	@RequestMapping(value = "/enumeracio/llistat", method = RequestMethod.GET)
	public String llistat(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("llistat", dissenyService.findEnumeracionsAmbEntorn(entorn.getId()));
			return "enumeracio/llistat";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/enumeracio/form", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			return "enumeracio/form";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/enumeracio/form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Enumeracio command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit) || submit.length() == 0) {
				command.setEntorn(entorn);
				annotationValidator.validate(command, result);
		        if (result.hasErrors()) {
		        	return "enumeracio/form";
		        }
		        try {
		        	if (command.getId() == null)
		        		dissenyService.createEnumeracio(command);
		        	else
		        		dissenyService.updateEnumeracio(command);
		        	missatgeInfo(request, "L'enumeració s'ha guardat correctament");
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar el registre", ex);
		        	return "enumeracio/form";
		        }
			}
			return "redirect:/enumeracio/llistat.html";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "/enumeracio/delete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			try {
				dissenyService.deleteEnumeracio(id);
				missatgeInfo(request, "L'enumeració s'ha esborrat correctament");
			} catch (Exception ex) {
	        	missatgeError(request, "No s'ha pogut esborrar l'enumeració", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar l'enumeració", ex);
	        }
			return "redirect:/enumeracio/llistat.html";
		} else {
			missatgeError(request, "No hi ha cap entorn seleccionat");
			return "redirect:/index.html";
		}
	}

	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private static final Log logger = LogFactory.getLog(EnumeracioController.class);

}
