/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.Carrec;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.service.OrganitzacioService;
import net.conselldemallorca.helium.webapp.mvc.util.BaseController;
import net.conselldemallorca.helium.webapp.mvc.util.PaginatedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.properties.SortOrderEnum;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Controlador per la gestió de carrecs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/carrec/*.html")
public class CarrecController extends BaseController {

	private OrganitzacioService organitzacioService;
	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public CarrecController(
			OrganitzacioService organitzacioService) {
		this.organitzacioService = organitzacioService;
		this.additionalValidator = new CarrecValidator(organitzacioService);
	}

	@ModelAttribute("command")
	public Carrec populateCommand(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			Carrec command = organitzacioService.getCarrecById(id);;
			return command;
		}
		return new Carrec();
	}

	@RequestMapping(value = "llistat")
	public String llistat(
			HttpServletRequest request,
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			@RequestParam(value = "objectsPerPage", required = false) String objectsPerPage,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			int pagina = (page != null) ? new Integer(page).intValue() : 1;
			int firstRow = (pagina - 1) * getObjectsPerPage(objectsPerPage);
			boolean isAsc = "asc".equals(dir);
			PaginatedList paginatedList = new PaginatedList();
			paginatedList.setFullListSize(organitzacioService.countCarrecAmbEntorn(entorn.getId()));
			paginatedList.setObjectsPerPage(getObjectsPerPage(objectsPerPage));
			paginatedList.setPageNumber(pagina);
			paginatedList.setSortCriterion(sort);
			paginatedList.setSortDirection((isAsc) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING);
			paginatedList.setList(organitzacioService.findCarrecPagedAndOrderedAmbEntorn(
					entorn.getId(),
					sort,
					isAsc,
					firstRow,
					getObjectsPerPage(objectsPerPage)));
			model.addAttribute("llistat", paginatedList);
			return "carrec/llistat";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "form", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			return "carrec/form";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Carrec command,
			BindingResult result,
			SessionStatus status) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit) || submit.length() == 0) {
				command.setEntorn(entorn);
				annotationValidator.validate(command, result);
				additionalValidator.validate(command, result);
		        if (result.hasErrors()) {
		        	return "carrec/form";
		        }
		        try {
		        	if (command.getId() == null)
		        		organitzacioService.createCarrec(command);
		        	else
		        		organitzacioService.updateCarrec(command);
		        	missatgeInfo(request, getMessage("info.carrec.guardat") );
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio") , ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar el registre", ex);
		        	return "carrec/form";
		        }
		        return "redirect:/carrec/llistat.html";
			} else {
				return "redirect:/carrec/llistat.html";
			}
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}

	@RequestMapping(value = "delete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) Long id) {
		organitzacioService.deleteCarrec(id);
		missatgeInfo(request, getMessage("info.carrec.esborrat") );
		return "redirect:/carrec/llistat.html";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Area.class,
				new AreaTypeEditor(organitzacioService));
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private class CarrecValidator implements Validator {
		private OrganitzacioService organitzacioService;
		public CarrecValidator(OrganitzacioService organitzacioService) {
			this.organitzacioService = organitzacioService;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Carrec.class);
		}
		public void validate(Object target, Errors errors) {
			Carrec command = (Carrec)target;
			Carrec repetit = organitzacioService.findCarrecAmbEntornICodi(
					command.getEntorn().getId(),
					command.getCodi());
			if (repetit != null && !repetit.getId().equals(command.getId())) {
				errors.rejectValue("codi", "error.carrec.codi.repetit");
			}
		}
	}

	private static final Log logger = LogFactory.getLog(CarrecController.class);

}
