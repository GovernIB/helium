/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.AreaJbpmId;
import net.conselldemallorca.helium.core.model.hibernate.Persona.Sexe;
import net.conselldemallorca.helium.core.model.service.OrganitzacioService;
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
 * Controlador per la gestió de la informació dels grups
 * per a les taules jbpm_id
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class AreaJbpmIdController extends BaseController {

	private OrganitzacioService organitzacioService;
	private Validator annotationValidator;



	@Autowired
	public AreaJbpmIdController(
			OrganitzacioService organitzacioService) {
		this.organitzacioService  = organitzacioService;
	}

	@ModelAttribute("command")
	public AreaJbpmId populateCommand(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "codi", required = false) String codi) {
		if (id != null) {
			return organitzacioService.getAreaJbpmIdById(id);
		} else if (codi != null) {
			AreaJbpmId command = new AreaJbpmId();
			command.setCodi(codi);
			return command;
		}
		return new AreaJbpmId();
	}
	@ModelAttribute("sexes")
	public Sexe[] populateSexes() {
		return Sexe.values();

	}

	@RequestMapping(value = "/area/jbpmConfigurats")
	public String jbpmConfigurats(
			HttpServletRequest request,
			ModelMap model) {
		model.addAttribute(
				"llistat",
				organitzacioService.findAreaJbpmIdAll());
		return "/area/jbpmConfigurats";
	}

	@RequestMapping(value = "/area/jbpmBuits")
	public String jbpmBuits(
			HttpServletRequest request,
			ModelMap model) {
		model.addAttribute(
				"llistat",
				organitzacioService.findAreaJbpmIdSenseAssignar());
		return "/area/jbpmBuits";
	}

	@RequestMapping(value = "/area/jbpmForm", method = RequestMethod.GET)
	public String jbpmFormGet(
			HttpServletRequest request) {
		return "/area/jbpmForm";
	}
	@RequestMapping(value = "/area/jbpmForm", method = RequestMethod.POST)
	public String jbpmFormPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") AreaJbpmId command,
			BindingResult result,
			SessionStatus status) {
		if ("submit".equals(submit) || submit.length() == 0) {
			annotationValidator.validate(command, result);
	        if (result.hasErrors())
	        	return "/area/jbpmForm";
	        try {
	        	if (command.getId() == null)
	        		organitzacioService.createAreaJbpmId(command);
	        	else
	        		organitzacioService.updateAreaJbpmId(command);
	        	missatgeInfo(request, getMessage("info.infoarea.guardat"));
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar la informació de l'area", ex);
	        	return "/area/jbpmForm";
	        }
		}
		return "redirect:/area/jbpmConfigurats.html";
	}

	@RequestMapping(value = "/area/jbpmDelete")
	public String jbpmDelete(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		try {
			organitzacioService.deleteAreaJbpmId(id);
			missatgeInfo(request, getMessage("info.area.esborrat"));
		} catch (Exception ex) {
        	missatgeError(request, getMessage("error.esborrar.infoarea"), ex.getLocalizedMessage());
        	logger.error("No s'ha pogut esborrar la informació de l'àrea", ex);
        }
		return "redirect:/area/jbpmConfigurats.html";
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private static final Log logger = LogFactory.getLog(AreaJbpmIdController.class);

}
