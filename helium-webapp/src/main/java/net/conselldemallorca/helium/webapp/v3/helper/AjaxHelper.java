/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utilitat per a marcar peticions AJAX.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AjaxHelper {

	private static final String ESQUEMA_PREFIX = "/helium";
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

	private static boolean isRequestUriAjax(
			HttpServletRequest request) {
		return request.getRequestURI().startsWith(URI_PREFIX_AJAX);
	}
	private static String getUriSensePrefix(
			HttpServletRequest request) {
		return request.getRequestURI().substring(URI_PREFIX_AJAX.length());
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

}
