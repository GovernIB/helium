/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.CarrecJbpmId;
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
 * Controlador per la gestió de la informació dels càrrecs
 * per a les taules jbpm_id
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class CarrecJbpmIdController extends BaseController {

	private OrganitzacioService organitzacioService;
	private Validator annotationValidator;



	@Autowired
	public CarrecJbpmIdController(
			OrganitzacioService organitzacioService) {
		this.organitzacioService  = organitzacioService;
	}

	@ModelAttribute("command")
	public CarrecJbpmId populateCommand(
			@RequestParam(value = "id", required = false) Long id,
			@RequestParam(value = "codi", required = false) String codi) {
		if (id != null) {
			return organitzacioService.getCarrecJbpmIdById(id);
		} else if (codi != null) {
			CarrecJbpmId command = new CarrecJbpmId();
			command.setCodi(codi);
			return command;
		}
		return new CarrecJbpmId();
	}
	@ModelAttribute("sexes")
	public Sexe[] populateSexes() {
		return Sexe.values();

	}

	@RequestMapping(value = "/carrec/jbpmConfigurats")
	public String jbpmConfigurats(
			HttpServletRequest request,
			ModelMap model) {
		model.addAttribute(
				"llistat",
				organitzacioService.findCarrecJbpmIdAll());
		return "/carrec/jbpmConfigurats";
	}

	@RequestMapping(value = "/carrec/jbpmBuits")
	public String jbpmBuits(
			HttpServletRequest request,
			ModelMap model) {
		model.addAttribute(
				"llistat",
				organitzacioService.findCarrecJbpmIdSenseAssignar());
		return "/carrec/jbpmBuits";
	}

	@RequestMapping(value = "/carrec/jbpmForm", method = RequestMethod.GET)
	public String jbpmFormGet(
			HttpServletRequest request) {
		return "/carrec/jbpmForm";
	}
	@RequestMapping(value = "/carrec/jbpmForm", method = RequestMethod.POST)
	public String jbpmFormPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") CarrecJbpmId command,
			BindingResult result,
			SessionStatus status) {
		if ("submit".equals(submit) || submit.length() == 0) {
			annotationValidator.validate(command, result);
	        if (result.hasErrors())
	        	return "/carrec/jbpmForm";
	        try {
	        	if (command.getId() == null)
	        		organitzacioService.createCarrecJbpmId(command);
	        	else
	        		organitzacioService.updateCarrecJbpmId(command);
	        	missatgeInfo(request, getMessage("info.infocarrec.guardat") );
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar la informació del càrrec", ex);
	        	return "/carrec/jbpmForm";
	        }
		}
		return "redirect:/carrec/jbpmConfigurats.html";
	}

	@RequestMapping(value = "/carrec/jbpmDelete")
	public String jbpmDelete(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		try {
			organitzacioService.deleteCarrecJbpmId(id);
			missatgeInfo(request, getMessage("info.infocarrec.esborrat") );
		} catch (Exception ex) {
        	missatgeError(request, getMessage("error.esborrar.infocarrec"), ex.getLocalizedMessage());
        	logger.error("No s'ha pogut esborrar la informació del càrrec", ex);
        }
		return "redirect:/carrec/jbpmConfigurats.html";
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private static final Log logger = LogFactory.getLog(CarrecJbpmIdController.class);

}
