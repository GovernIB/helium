/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.service.EntornService;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió d'entorns
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public class EntornController extends BaseController {

	private EntornService entornService;
	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public EntornController(
			EntornService entornService) {
		this.entornService = entornService;
		additionalValidator = new EntornValidator(entornService);
	}

	@ModelAttribute("command")
	public Entorn populateCommand(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			return entornService.getById(id);
		}
		Entorn entorn = new Entorn();
		entorn.setActiu(true);
		return entorn;
	}

	@RequestMapping(value = "/entorn/llistat")
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
						entornService.countAll(),
						entornService.findPagedAndOrderedAll(
								sort,
								isAsc,
								firstRow,
								getObjectsPerPage(objectsPerPage))));
		return "entorn/llistat";
	}

	@RequestMapping(value = {"/entorn/form", "/noaentorn/form"}, method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			ModelMap model) {
		setIsAdmin(request, model);
		return "entorn/form";
	}
	@RequestMapping(value = {"/entorn/form", "/noaentorn/form"}, method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Entorn command,
			BindingResult result,
			SessionStatus status,
			ModelMap model) {
		if ("submit".equals(submit) || submit.length() == 0) {
			annotationValidator.validate(command, result);
			additionalValidator.validate(command, result);
	        if (result.hasErrors()) {
	        	setIsAdmin(request, model);
	        	return "entorn/form";
	        }
	        Entorn saved = null;
	        try {
	        	if (command.getId() == null)
	        		saved = entornService.create(command);
	        	else
	        		saved = entornService.update(command);
	        	missatgeInfo(request, "L'entorn s'ha guardat correctament");
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, "S'ha produït un error processant la seva petició", ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar el registre", ex);
	        	setIsAdmin(request, model);
	        	return "entorn/form";
	        }
	        if (isAdmin(request))
	        	return "redirect:/entorn/llistat.html";
	        else
	        	return "redirect:/noaentorn/form.html?id=" + saved.getId();
		} else {
			if (isAdmin(request))
				return "redirect:/entorn/llistat.html";
			else
				return "redirect:/noaentorn/form.html?id=" + command.getId();
		}
	}

	@RequestMapping(value = "/entorn/delete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id,
			ModelMap model) {
		entornService.delete(id);
		missatgeInfo(request, "L'entorn s'ha esborrat correctament");
		return "redirect:/entorn/llistat.html";
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	protected class EntornValidator implements Validator {
		private EntornService entornService;
		public EntornValidator(EntornService entornService) {
			this.entornService = entornService;
		}
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Entorn.class);
		}
		public void validate(Object target, Errors errors) {
			Entorn command = (Entorn)target;
			Entorn repetit = entornService.findAmbCodi(command.getCodi());
			if (repetit != null && !repetit.getId().equals(command.getId())) {
				errors.rejectValue("codi", "error.entorn.codi.repetit");
			}
		}
	}



	private boolean isAdmin(HttpServletRequest request) {
		return !request.getRequestURI().contains("/noaentorn/");
	}
	private void setIsAdmin(
			HttpServletRequest request,
			ModelMap model) {
		model.addAttribute("isAdmin", isAdmin(request));
	}

	private static final Log logger = LogFactory.getLog(EntornController.class);

}
