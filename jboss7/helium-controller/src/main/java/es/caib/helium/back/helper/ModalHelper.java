/**
 * 
 */
package es.caib.helium.back.helper;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Helper per a finestres modals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ModalHelper {

	private static final String PREFIX_MODAL = "/modal";
	private static final String REQUEST_ATTRIBUTE_MODAL = "ModalHelper.Modal";
	private static final String SESSION_ATTRIBUTE_REQUESTPATHSMAP = "ModalHelper.RequestPathsMap";

	public static final String ACCIO_MODAL_TANCAR = PREFIX_MODAL + "/tancar";

	public static boolean isModal(HttpServletRequest request) {
		return request.getAttribute(REQUEST_ATTRIBUTE_MODAL) != null;
	}
	public static boolean comprovarModalInterceptor(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (isRequestPathModal(request)) {
			String pathSensePrefix = getPathSensePrefix(request);
			Set<String> requestPathsMap = getRequestPathsMap(request);
			requestPathsMap.add(pathSensePrefix);
			RequestDispatcher dispatcher = request.getRequestDispatcher(pathSensePrefix);
		    dispatcher.forward(request, response);
		    return false;
		} else {
			Set<String> requestPathsMap = getRequestPathsMap(request);
			String pathComprovacio = request.getServletPath();
			if (requestPathsMap.contains(pathComprovacio)) {
				requestPathsMap.remove(pathComprovacio);
				marcarModal(request);
			}
			return true;
		}
	}

	private static boolean isRequestPathModal(
			HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return
				servletPath.startsWith(PREFIX_MODAL) &&
				!servletPath.startsWith(ACCIO_MODAL_TANCAR);
	}
	private static String getPathSensePrefix(
			HttpServletRequest request) {
		return request.getServletPath().substring(PREFIX_MODAL.length());
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
	private static void marcarModal(HttpServletRequest request) {
		request.setAttribute(
				REQUEST_ATTRIBUTE_MODAL,
				Boolean.valueOf(true));
	}

}
