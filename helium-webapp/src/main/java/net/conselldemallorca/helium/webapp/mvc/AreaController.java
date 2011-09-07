/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.conselldemallorca.helium.core.model.hibernate.Area;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;



/**
 * Controlador per la gestió d'àrees
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/area/*.html")
public class AreaController extends BaseController {

	private OrganitzacioService organitzacioService;
	private Validator annotationValidator;
	private Validator additionalValidator;



	@Autowired
	public AreaController(
			OrganitzacioService organitzacioService) {
		this.organitzacioService = organitzacioService;
		this.additionalValidator = new AreaValidator(organitzacioService);
	}

	@ModelAttribute("command")
	public Area populateCommand(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			Area command = organitzacioService.getAreaById(id);;
			return command;
		}
		return new Area();
	}
	@ModelAttribute("tipus")
	public List<AreaTipus> populateTipus(
			HttpServletRequest request) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			return organitzacioService.findAreaTipusAmbEntorn(entorn.getId());
		}
		return null;
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
			paginatedList.setFullListSize(organitzacioService.countAreaAmbEntorn(entorn.getId()));
			paginatedList.setObjectsPerPage(getObjectsPerPage(objectsPerPage));
			paginatedList.setPageNumber(pagina);
			paginatedList.setSortCriterion(sort);
			paginatedList.setSortDirection((isAsc) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING);
			paginatedList.setList(organitzacioService.findAreaPagedAndOrderedAmbEntorn(
					entorn.getId(),
					sort,
					isAsc,
					firstRow,
					getObjectsPerPage(objectsPerPage)));
			model.addAttribute("llistat", paginatedList);
			return "area/llistat";
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
			return "area/form";
		} else {
			missatgeError(request, getMessage("error.no.entorn.selec") );
			return "redirect:/index.html";
		}
	}
	@RequestMapping(value = "form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") Area command,
			BindingResult result,
			SessionStatus status) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			if ("submit".equals(submit) || submit.length() == 0) {
				command.setEntorn(entorn);
				annotationValidator.validate(command, result);
				additionalValidator.validate(command, result);
		        if (result.hasErrors()) {
		        	return "area/form";
		        }
		        try {
		        	if (command.getId() == null)
		        		organitzacioService.createArea(command);
		        	else
		        		organitzacioService.updateArea(command);
		        	missatgeInfo(request, getMessage("info.area.guardat") );
		        	status.setComplete();
		        } catch (Exception ex) {
		        	missatgeError(request, getMessage("error.proces.peticio") , ex.getLocalizedMessage());
		        	logger.error("No s'ha pogut guardar el registre", ex);
		        	return "area/form";
		        }
		        return "redirect:/area/llistat.html";
			} else {
				return "redirect:/area/llistat.html";
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
		try {
			organitzacioService.deleteArea(id);
			missatgeInfo(request, getMessage("info.area.esborrat") );
		} catch (Exception ex) {
        	missatgeError(request, getMessage("error.esborrar.area"), ex.getLocalizedMessage());
        	logger.error("No s'ha pogut esborrar el registre", ex);
        	return "area/form";
        }
		return "redirect:/area/llistat.html";
	}

	@RequestMapping(value = "suggest", method = RequestMethod.GET)
	public String suggestAction(
			HttpServletRequest request,
			@RequestParam(value = "q", required = true) String text,
			ModelMap model) {
		Entorn entorn = getEntornActiu(request);
		if (entorn != null) {
			model.addAttribute("arees", organitzacioService.findAreaLikeNom(entorn.getId(), text));
		}
		return "area/suggest";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				AreaTipus.class,
				new AreaTipusTypeEditor(organitzacioService));
		binder.registerCustomEditor(
				Area.class,
				new AreaTypeEditor(organitzacioService));
	}



	@Resource(name = "annotationValidator")
	public void setAnnotationValidator(Validator annotationValidator) {
		this.annotationValidator = annotationValidator;
	}



	private class AreaValidator implements Validator {
		private OrganitzacioService organitzacioService;
		public AreaValidator(OrganitzacioService organitzacioService) {
			this.organitzacioService = organitzacioService;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public boolean supports(Class clazz) {
			return clazz.isAssignableFrom(Area.class);
		}
		public void validate(Object target, Errors errors) {
			Area command = (Area)target;
			Area repetida = organitzacioService.findAreaAmbEntornICodi(
					command.getEntorn().getId(),
					command.getCodi());
			if (repetida != null && !repetida.getId().equals(command.getId())) {
				errors.rejectValue("codi", "error.area.codi.repetit");
			}
		}
	}

	private static final Log logger = LogFactory.getLog(AreaController.class);

}
