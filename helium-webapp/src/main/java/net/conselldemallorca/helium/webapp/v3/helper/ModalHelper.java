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
 * Utilitat per a finestres modals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ModalHelper {

	private static final String ESQUEMA_PREFIX = "/helium";
	private static final String URI_PREFIX_MODAL = ESQUEMA_PREFIX + "/modal";
	private static final String REQUEST_ATTRIBUTE_MODAL = "ModalHelper.Modal";
	private static final String SESSION_ATTRIBUTE_URIMAP = "ModalHelper.UriMap";

	public static boolean isModal(HttpServletRequest request) {
		return request.getAttribute(REQUEST_ATTRIBUTE_MODAL) != null;
	}
	public static boolean comprovarModalInterceptor(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (isRequestUriModal(request)) {
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
				marcarModal(request);
			}
			return true;
		}
	}

	private static boolean isRequestUriModal(
			HttpServletRequest request) {
		return request.getRequestURI().startsWith(URI_PREFIX_MODAL);
	}
	private static String getUriSensePrefix(
			HttpServletRequest request) {
		return request.getRequestURI().substring(URI_PREFIX_MODAL.length());
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
	private static void marcarModal(HttpServletRequest request) {
		request.setAttribute(
				REQUEST_ATTRIBUTE_MODAL,
				new Boolean(true));
	}

}
