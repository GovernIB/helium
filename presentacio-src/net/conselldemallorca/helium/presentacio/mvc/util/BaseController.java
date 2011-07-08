/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.presentacio.mvc.interceptor.EntornInterceptor;

import org.displaytag.properties.SortOrderEnum;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;

/**
 * Controlador base
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class BaseController implements MessageSourceAware {

	protected static final String INFO_MSG_SESSION_KEY = "missatgesInfo";
	protected static final String ERROR_MSG_SESSION_KEY = "missatgesError";
	private static final int DEFAULT_OBJECTS_PER_PAGE = 20;

	private MessageSource messageSource;



	@SuppressWarnings("unchecked")
	protected static void missatgeInfo(
			HttpServletRequest request,
			String text) {
		HttpSession session = request.getSession();
		List<String> missatges = (List<String>)session.getAttribute(INFO_MSG_SESSION_KEY);
		if (missatges == null) {
			missatges = new ArrayList<String>();
			session.setAttribute(INFO_MSG_SESSION_KEY, missatges);
		}
		missatges.add(text);
	}

	protected static void missatgeError(
			HttpServletRequest request,
			String text) {
		missatgeError(request, text, null);
	}
	@SuppressWarnings("unchecked")
	protected static void missatgeError(
			HttpServletRequest request,
			String text,
			String missatgeError) {
		HttpSession session = request.getSession();
		List<String> errors = (List<String>)session.getAttribute(ERROR_MSG_SESSION_KEY);
		if (errors == null) {
			errors = new ArrayList<String>();
			session.setAttribute(ERROR_MSG_SESSION_KEY, errors);
		}
		if (missatgeError != null)
			errors.add(text + ": " + missatgeError);
		else
			errors.add(text);
	}

	protected String getMessage(String key) {
		try {
			return messageSource.getMessage(
					key,
					null,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}

	protected Entorn getEntornActiu(
			HttpServletRequest request) {
		return (Entorn)request.getSession().getAttribute(EntornInterceptor.VARIABLE_SESSIO_ENTORN_ACTUAL);
	}

	@SuppressWarnings("rawtypes")
	protected PaginatedList newPaginatedList(
			int page,
			String sort,
			boolean isAsc,
			int objectsPerPage,
			int fullListSize,
			List values) {
		PaginatedList paginatedList = new PaginatedList();
		paginatedList.setFullListSize(fullListSize);
		paginatedList.setObjectsPerPage(objectsPerPage);
		paginatedList.setPageNumber(page);
		paginatedList.setSortCriterion(sort);
		paginatedList.setSortDirection((isAsc) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING);
		paginatedList.setList(values);
		return paginatedList;
	}
	protected int getObjectsPerPage(String objectsPerPage) {
		if (objectsPerPage == null)
			return DEFAULT_OBJECTS_PER_PAGE;
		try {
			return Integer.parseInt(objectsPerPage);
		} catch (Exception ex) {
			return DEFAULT_OBJECTS_PER_PAGE;
		}
	}



	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
