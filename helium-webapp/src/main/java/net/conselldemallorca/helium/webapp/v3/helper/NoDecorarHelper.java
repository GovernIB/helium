/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Helper per a identificar les peticions no decorades.
 * 
 * Hi ha dues opcions: no decorada i sense capsalera ni peu
 * La no decorada vol dir que no passa per Sitemesh.
 * Sense capsalera ni peu vol dir que la plantilla de Sitemesh
 * no inclourà capsalera ni peu. En canvi si que inclourà, per
 * exemple, els css i javascripts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NoDecorarHelper {

	private static final String[] PREFIXOS_RECURSOS_ESTATICS = new String[] {
		"/helium/css",
		"/helium/font",
		"/helium/img",
		"/helium/js"
	};

	private static final String PREFIX_NO_DECORAR = "/helium/nodecorar";
	private static final String ATRIBUT_NO_DECORAR = "NoDecorarHelper.noDecorar";
	private static final String ATRIBUT_NO_CAPSALERAPEU = "NoDecorarHelper.noCapsaleraPeu";

	public static boolean isRequestSenseDecoracio(
			HttpServletRequest request) {
		if (request.getSession().getAttribute(ATRIBUT_NO_DECORAR) != null) {
			if (!isUriRecursEstatic(request)) {
				request.getSession().removeAttribute(ATRIBUT_NO_DECORAR);
				request.setAttribute(ATRIBUT_NO_DECORAR, new Boolean(true));
			}
			return request.getRequestURI().startsWith(PREFIX_NO_DECORAR);
		} else if (request.getAttribute(ATRIBUT_NO_DECORAR) != null) {
			return true;
		} else {
			return request.getRequestURI().startsWith(PREFIX_NO_DECORAR);
		}
	}
	public static void redirigirUriSenseDecoracio(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		RequestDispatcher dispatcher = request.getRequestDispatcher(
				getUriSenseDecoracio(request));
		request.getSession().setAttribute(ATRIBUT_NO_DECORAR, new Boolean(true));
	    dispatcher.forward(request, response);
	}

	public static void marcarNoCapsaleraNiPeu(
			HttpServletRequest request) {
		request.setAttribute(ATRIBUT_NO_CAPSALERAPEU, new Boolean(true));
	}
	public static boolean isNoCapsaleraNiPeu(
			HttpServletRequest request) {
		return request.getAttribute(ATRIBUT_NO_CAPSALERAPEU) != null;
	}



	private static String getUriSenseDecoracio(
			HttpServletRequest request) {
		return request.getRequestURI().substring(PREFIX_NO_DECORAR.length());
	}

	private static boolean isUriRecursEstatic(
			HttpServletRequest request) {
		String uri = request.getRequestURI();
		boolean estatic = false;
		for (String prefix: PREFIXOS_RECURSOS_ESTATICS) {
			if (uri.startsWith(prefix)) {
				estatic = true;
				break;
			}
		}
		return estatic;
	}

}
