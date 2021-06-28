/**
 * 
 */
package es.caib.helium.back.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Helper per a marcar peticions AJAX.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AjaxHelper {

	private static final String PREFIX_AJAX = "/ajax";
	private static final String REQUEST_ATTRIBUTE_AJAX = "AjaxHelper.Ajax";
	private static final String SESSION_ATTRIBUTE_REQUESTPATHSMAP = "AjaxHelper.RequestPathsMap";

	public static final String ACCIO_AJAX_OK = PREFIX_AJAX + "/ok";

	public static boolean isAjax(HttpServletRequest request) {
		return request.getAttribute(REQUEST_ATTRIBUTE_AJAX) != null;
	}
	public static boolean comprovarAjaxInterceptor(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (isRequestPathAjax(request)) {
			String uriSensePrefix = getUriSensePrefix(request);
			Set<String> requestPathsMap = getRequestPathsMap(request);
			requestPathsMap.add(uriSensePrefix);
			RequestDispatcher dispatcher = request.getRequestDispatcher(uriSensePrefix);
		    dispatcher.forward(request, response);
		    return false;
		} else {
			Set<String> requestPathsMap = getRequestPathsMap(request);
			String pathComprovacio = request.getServletPath();
			if (requestPathsMap.contains(pathComprovacio)) {
				requestPathsMap.remove(pathComprovacio);
				marcarAjax(request);
			}
			return true;
		}
	}

	public static AjaxFormResponse generarAjaxFormErrors(
			Object objecte,
			BindingResult bindingResult) {
		return new AjaxFormResponse(objecte, bindingResult);
	}
	public static AjaxFormResponse generarAjaxFormOk(
			Object objecte) {
		return new AjaxFormResponse(objecte);
	}
	public static AjaxFormResponse generarAjaxFormOk() {
		return new AjaxFormResponse(null);
	}



	private static boolean isRequestPathAjax(
			HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return
				servletPath.startsWith(PREFIX_AJAX) &&
				!servletPath.startsWith(ACCIO_AJAX_OK);
	}
	private static String getUriSensePrefix(
			HttpServletRequest request) {
		return request.getServletPath().substring(PREFIX_AJAX.length());
	}
	private static Set<String> getRequestPathsMap(
			HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Set<String> requestPathsMap = (Set<String>)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_REQUESTPATHSMAP);
		if (requestPathsMap == null) {
			requestPathsMap = new HashSet<String>();
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_REQUESTPATHSMAP,
					requestPathsMap);
		}
		return requestPathsMap;
	}
	private static void marcarAjax(HttpServletRequest request) {
		request.setAttribute(
				REQUEST_ATTRIBUTE_AJAX,
				Boolean.valueOf(true));
	}

	public static class AjaxFormResponse {
		private Object objecte;
		private AjaxFormEstatEnum estat;
		private List<AjaxFormError> errorsGlobals;
		private List<AjaxFormError> errorsCamps;
		public AjaxFormResponse(Object objecte, BindingResult bindingResult) {
			super();
			this.objecte = objecte;
			if (bindingResult != null) {
				this.errorsGlobals = new ArrayList<AjaxFormError>();
				for (ObjectError objectError: bindingResult.getGlobalErrors()) {
					errorsGlobals.add(
							new AjaxFormError(
									objectError.getObjectName(),
									MessageHelper.getInstance().getMessage(	
											objectError.getCode(),
											objectError.getArguments())));
				}
				this.errorsCamps = new ArrayList<AjaxFormError>();
				for (FieldError fieldError: bindingResult.getFieldErrors()) {
					errorsCamps.add(
							new AjaxFormError(
									fieldError.getField(),
									MessageHelper.getInstance().getMessage(
											fieldError.getCodes(),
											fieldError.getArguments(),
											null)));
				}
				this.estat = AjaxFormEstatEnum.ERROR;
			} else {
				this.estat = AjaxFormEstatEnum.OK;
			}
		}
		public AjaxFormResponse(Object objecte) {
			super();
			this.objecte = objecte;
			this.estat = AjaxFormEstatEnum.OK;
		}
		public Object getObjecte() {
			return objecte;
		}
		public AjaxFormEstatEnum getEstat() {
			return estat;
		}
		public List<AjaxFormError> getErrorsGlobals() {
			return errorsGlobals;
		}
		public List<AjaxFormError> getErrorsCamps() {
			return errorsCamps;
		}
		public boolean isEstatOk() {
			return estat.equals(AjaxFormEstatEnum.OK);
		}
		public boolean isEstatError() {
			return estat.equals(AjaxFormEstatEnum.ERROR);
		}
		public boolean isErrorsGlobals() {
			return errorsGlobals != null;
		}
		public boolean isErrorsCamps() {
			return errorsCamps != null;
		}
	}

	public static class AjaxFormError {
		private String camp;
		private String missatge;
		public AjaxFormError(String camp, String missatge) {
			this.camp = camp;
			this.missatge = missatge;
		}
		public String getCamp() {
			return camp;
		}
		public String getMissatge() {
			return missatge;
		}
	}

	public enum AjaxFormEstatEnum {
		OK,
		ERROR
	}

}
