/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.PermissionService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;

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
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class EnumeracioValorsController extends BaseController {

	private DissenyService dissenyService;
	private Validator annotationValidator;

	@Autowired
	public EnumeracioValorsController(
			DissenyService dissenyService,
			PermissionService permissionService) {
		this.dissenyService  = dissenyService;
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
	
	@RequestMapping(value = "/enumeracio/valors", method = RequestMethod.GET)
	public String formValorsGet(
			HttpServletRequest request,
			ModelMap model) {
		String enumeracio = request.getParameter("enumeracio");
		if ((enumeracio != null) && !(enumeracio.equals(""))) {
			model.addAttribute("llistat", dissenyService.findEnumeracioValorsAmbEnumeracio(Long.parseLong(enumeracio)));
			model.addAttribute("enumeracio", enumeracio);
			return "enumeracio/valors";
		} else {
			missatgeError(request, getMessage("error.no.enumeracio.selec") );
			return "redirect:/enumeracio/llistat.html";
		}
	}
	
	@RequestMapping(value = "/enumeracio/valors", method = RequestMethod.POST)
	public String formValorsPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") EnumeracioValorsCommand command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		String enumeracioId = request.getParameter("enumeracio");
		if ((enumeracioId != null) && !(enumeracioId.equals(""))) {
			if ("submit".equals(submit) || submit.length() == 0) {
				Enumeracio enumeracio = dissenyService.getEnumeracioById(Long.parseLong(enumeracioId));
				command.setEnumeracioId(new Long(enumeracioId));
				annotationValidator.validate(command, result);
				new EnumeracioValorsValidator(dissenyService).validate(command, result);
		        if (result.hasErrors()) {
		        	model.addAttribute("llistat", dissenyService.findEnumeracioValorsAmbEnumeracio(Long.parseLong(enumeracioId)));
		        	model.addAttribute("enumeracio", enumeracioId);
		        	return "enumeracio/valors";
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
		        	model.addAttribute("enumeracio", enumeracioId);
		        	return "enumeracio/valors";
		        }
		        model.addAttribute("enumeracio", enumeracioId);
				return "redirect:/enumeracio/valors.html";
			} else {
				return "redirect:/enumeracio/llistat.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.enumeracio.selec") );
			return "redirect:/index.html";
		}
	}
	
	@RequestMapping(value = "/enumeracio/deleteValors")
	public String deleteValorsAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		String enumeracioId = "";
		try {
			EnumeracioValors enumeracioValors = dissenyService.getEnumeracioValorsById(id);
			enumeracioId = "" + enumeracioValors.getEnumeracio().getId();
			dissenyService.deleteEnumeracioValors(id);
			missatgeInfo(request, getMessage("info.enum.esborrat") );
		} catch (Exception ex) {
        	missatgeError(request, getMessage("error.esborrar.enum"), ex.getLocalizedMessage());
        	logger.error("No s'ha pogut esborrar l'enumeració", ex);
        }
		return "redirect:/enumeracio/valors.html?enumeracio=" + enumeracioId;
	}

	@RequestMapping(value = "/enumeracio/valorsPujar")
	public String pujarValor(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			EnumeracioValors enumeracioValors = dissenyService.getEnumeracioValorsById(id);
			try {
				dissenyService.goUpEnumeracioValor(id);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.ordre.enumeracio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut canviar l'ordre del valor de l'enumeració", ex);
	        }
			return "redirect:/enumeracio/valors.html?enumeracio=" + enumeracioValors.getEnumeracio().getId();
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "/enumeracio/valorsBaixar")
	public String baixarValor(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			EnumeracioValors enumeracioValors = dissenyService.getEnumeracioValorsById(id);
			try {
				dissenyService.goDownEnumeracioValor(id);
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.ordre.enumeracio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut canviar l'ordre del valor de l'enumeració", ex);
	        }
			return "redirect:/enumeracio/valors.html?enumeracio=" + enumeracioValors.getEnumeracio().getId();
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}

	private static final Log logger = LogFactory.getLog(EnumeracioValorsController.class);
}
