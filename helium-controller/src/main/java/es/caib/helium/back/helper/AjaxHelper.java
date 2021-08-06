/**
 * 
 */
package es.caib.helium.back.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Utilitat per a marcar peticions AJAX.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AjaxHelper {

	private static final String ESQUEMA_PREFIX = "/heliumback";
	private static final String URI_PREFIX_AJAX = ESQUEMA_PREFIX + "/ajax";
	private static final String REQUEST_ATTRIBUTE_AJAX = "AjaxHelper.Ajax";
	private static final String SESSION_ATTRIBUTE_URIMAP = "AjaxHelper.UriMap";

	public static boolean isAjax(HttpServletRequest request) {
		return request.getAttribute(REQUEST_ATTRIBUTE_AJAX) != null;
	}
	public static boolean comprovarAjaxInterceptor(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (isRequestUriAjax(request)) {
			String uriSensePrefix = getUriSensePrefix(request);
			Set<String> uriMap = getUriMap(request);
			uriMap.add(uriSensePrefix);
			RequestDispatcher dispatcher = request.getRequestDispatcher(uriSensePrefix);
		    dispatcher.forward(request, response);
		    return false;
		} else {
			Set<String> uriMap = getUriMap(request);
			String uriComprovacio = request.getRequestURI().substring(ESQUEMA_PREFIX.length());
			if (uriMap.contains(uriComprovacio)) {
				uriMap.remove(uriComprovacio);
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



	private static boolean isRequestUriAjax(
			HttpServletRequest request) {
		return request.getRequestURI().contains(URI_PREFIX_AJAX);
	}
	private static String getUriSensePrefix(
			HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.substring(uri.indexOf(URI_PREFIX_AJAX) + URI_PREFIX_AJAX.length());
	}
	private static Set<String> getUriMap(
			HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Set<String> uriMap = (Set<String>)request.getSession().getAttribute(SESSION_ATTRIBUTE_URIMAP);
		if (uriMap == null) {
			uriMap = new HashSet<String>();
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_URIMAP,
					uriMap);
		}
		return uriMap;
	}
	private static void marcarAjax(HttpServletRequest request) {
		request.setAttribute(
				REQUEST_ATTRIBUTE_AJAX,
				new Boolean(true));
	}

	/** Classe genèrica per respostes ajax.
	 */
	public static class AjaxResponse {
		
		private boolean error = false;
		private String missatge = "";
		private Map<String, Object> dades = new HashMap<String, Object>();
		
		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
		public String getMissatge() {
			return missatge;
		}
		public void setMissatge(String missatge) {
			this.missatge = missatge;
		}
		public Map<String, Object> getDades() {
			return dades;
		}
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
				String errMsg;
				this.errorsGlobals = new ArrayList<AjaxFormError>();
				for (ObjectError objectError: bindingResult.getGlobalErrors()) {
					errMsg = objectError.getDefaultMessage() != null ?
								objectError.getDefaultMessage()
								: MessageHelper.getInstance().getMessage(
										objectError.getCode(),
										objectError.getArguments());
					errorsGlobals.add(
							new AjaxFormError(
									objectError.getObjectName(),
									errMsg));
				}
				this.errorsCamps = new ArrayList<AjaxFormError>();
				for (FieldError fieldError: bindingResult.getFieldErrors()) {
					errMsg = fieldError.getDefaultMessage() != null ?
							fieldError.getDefaultMessage()
							: MessageHelper.getInstance().getMessage(
									fieldError.getCode(),
									fieldError.getArguments());
					errorsCamps.add(
							new AjaxFormError(
									fieldError.getField(),
									errMsg));
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
