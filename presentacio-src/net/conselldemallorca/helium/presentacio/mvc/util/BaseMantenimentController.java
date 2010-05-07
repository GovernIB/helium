/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc.util;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.properties.SortOrderEnum;
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
 * Controlador de proves
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Controller
public abstract class BaseMantenimentController<T, U extends T, ID extends Serializable> extends BaseController {

	private int objectsPerPage = 20;
	private boolean paginat = true;

	private Validator validator;
	protected Validator validadorAddicional;



	@ModelAttribute("command")
	public T populateCommand(@RequestParam(value = "id", required = false) ID id) {
		if (id != null) {
			T command = initCommandFromService(id);
			return command;
		}
		return newCommand();
	}

	@RequestMapping(value = "llistat")
	public String llistat(
			@RequestParam(value = "page", required = false) String page,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "dir", required = false) String dir,
			ModelMap model) {
		if (paginat) {
			int pagina = (page != null) ? new Integer(page).intValue() : 1;
			int firstRow = (pagina - 1) * objectsPerPage;
			boolean isAsc = "asc".equals(dir);
			PaginatedList paginatedList = new PaginatedList();
			paginatedList.setFullListSize(getCount());
			paginatedList.setObjectsPerPage(objectsPerPage);
			paginatedList.setPageNumber(pagina);
			paginatedList.setSortCriterion(sort);
			paginatedList.setSortDirection((isAsc) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING);
			paginatedList.setList(findPaged(
					sort,
					isAsc,
					firstRow,
					objectsPerPage));
			model.addAttribute("llistat", paginatedList);
		} else {
			model.addAttribute("llistat", find());
		}
		return getPathView(false) + "/llistat";
	}

	@RequestMapping(value = "form", method = RequestMethod.GET)
	public String formGet() {
		return getPathView(false) + "/form";
	}
	@RequestMapping(value = "form", method = RequestMethod.POST)
	public String formPost(
			HttpServletRequest request,
			@RequestParam(value = "submit", required = false) String submit,
			@ModelAttribute("command") U command,
			BindingResult result,
			SessionStatus status) {
		if ("submit".equals(submit) || submit.length() == 0) {
			if (validator != null)
				validator.validate(command, result);
			if (validadorAddicional != null) {
				validadorAddicional.validate(command, result);
			}
	        if (result.hasErrors()) {
	        	return getPathView(false) + "/form";
	        }
	        ID idSaved = null;
	        try {
	        	idSaved = saveOrUpdate(command);
	        	missatgeInfo(request, saveOkMessage());
	        	status.setComplete();
	        } catch (Exception ex) {
	        	missatgeError(request, saveErrorMessage(), ex.getLocalizedMessage());
	        	logger.error("No s'ha pogut guardar el registre", ex);
	        	return getPathView(false) + "/form";
	        }
	        return "redirect:" + getPathView(true) + "/form.html?id=" + idSaved;
		} else if ("cancel".equals(submit)) {
			return "redirect:" + getPathView(true) + "/llistat.html";
		} else {
			return getPathView(false) + "/llistat";
		}
	}

	@RequestMapping(value = "delete")
	public String deleteAction(
			HttpServletRequest request,
			@RequestParam(value = "id", required = true) ID id) {
		delete(id);
		missatgeInfo(request, deleteOkMessage());
		return "redirect:" + getPathView(true) + "/llistat.html";
	}



	public void setPaginat(boolean paginat) {
		this.paginat = paginat;
	}
	public void setObjectsPerPage(int objectsPerPage) {
		this.objectsPerPage = objectsPerPage;
	}
	@Resource(name = "annotationValidator")
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	public void setValidadorAddicional(Validator validadorAddicional) {
		this.validadorAddicional = validadorAddicional;
	}
	
	public String getPathView(boolean isRedirect) {
		return getPathView();
	}



	protected abstract U initCommandFromService(ID id);
	protected abstract List<T> find();
	protected abstract List<T> findPaged(
			String sort,
			boolean asc,
			int firstRow,
			int maxResults);
	protected abstract int getCount();
	protected abstract ID saveOrUpdate(U obj);
	protected abstract void delete(ID id);
	protected abstract U newCommand();

	protected abstract String getPathView();

	protected abstract String saveOkMessage();
	protected abstract String saveErrorMessage();
	protected abstract String deleteOkMessage();
	protected abstract String notFoundMessage();
	protected abstract String defaultErrorMessage();

	private static final Log logger = LogFactory.getLog(BaseMantenimentController.class);

}
