/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.AreaTipus;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió de tipus d'àrea
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/areaTipus/*.html")
public class AreaTipusController extends BaseController {

	private OrganitzacioService organitzacioService;
	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public AreaTipusController(
			OrganitzacioService organitzacioService) {
		this.organitzacioService = organitzacioService;
		this.additionalValidator = new AreaTipusValidator(organitzacioService);
	}

	@ModelAttribute("command")
	public AreaTipus populateCommand(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			AreaTipus command = organitzacioService.getAreaTipusById(id);;
			return command;
		}
		return new AreaTipus();
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
			paginatedList.setFullListSize(organitzacioService.countAreaTipusAmbEntorn(entorn.getId()));
			paginatedList.setObjectsPerPage(getObjectsPerPage(objectsPerPage));
			paginatedList.setPageNumber(pagina);
			paginatedList.setSortCriterion(sort);
			paginatedList.setSortDirection((isAsc) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING);
			paginatedList.setList(organitzacioService.findAreaTipusPagedAndOrderedAmbEntorn(
					entorn.getId(),
					sort,
					isAsc,
					firstRow,
					getObjectsPerPage(objectsPerPage)));
			model.addAttribute("llistat", paginatedList);
			return "areaTipus/llistat";
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
			return "areaTipus/form";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") AreaTipus command,
			BindingResult result,
			SessionStatus status) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit) || submit.length() == 0) {
				command.setEntorn(entorn);
				annotationValidator.validate(command, result);
				additionalValidator.validate(command, result);
		        if (result.hasErrors()) {
		        	return "areaTipus/form";
		        }
		        try {
		        	if (command.getId() == null)
		        		organitzacioService.createAreaTipus(command);
		        	else
		        		organitzacioService.updateAreaTipus(command);
		        	missatgeInfo(request, getMessage("info.areatipus.guardat") );
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio"), ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar el registre", ex);
		        	return "areaTipus/form";
		        }
		        return "redirect:/areaTipus/llistat.html";
			} else {
				return "redirect:/areaTipus/llistat.html";
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
		if (organitzacioService.findAreaAmbTipus(id).size() > 0) {
			missatgeError(request, getMessage("error.exist.arees.depenen") );
		} else {
			try {
				organitzacioService.deleteAreaTipus(id);
				missatgeInfo(request, getMessage("info.areatipus.esborrat") );
			} catch (Exception ex) {
	        	missatgeError(request, getMessage("error.esborrar.areatipus"), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut esborrar el registre", ex);
	        	return "area/form";
	        }
		}
		return "redirect:/areaTipus/llistat.html";
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private class AreaTipusValidator implements Validator {
		private OrganitzacioService organitzacioService;
		public AreaTipusValidator(OrganitzacioService organitzacioService) {
			this.organitzacioService = organitzacioService;
		}
		@SuppressWarnings("unchecked")
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(AreaTipus.class);
		}
		public void validate(Object target, Errors errors) {
			AreaTipus command = (AreaTipus)target;
			AreaTipus repetida = organitzacioService.findAreaTipusAmbEntornICodi(
					command.getEntorn().getId(),
					command.getCodi());
			if (repetida != null && !repetida.getId().equals(command.getId())) {
				errors.rejectValue("codi", "error.areatipus.codi.repetit");
			}
		}
	}

	private static final Log logger = LogFactory.getLog(AreaTipusController.class);

}
