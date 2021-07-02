/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.helium.logic.util.GlobalProperties;

/**
 * Interceptor per guardar les propietats globals per a cada petició
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GlobalPropertiesInterceptor extends HandlerInterceptorAdapter {

	public static final String VARIABLE_REQUEST_GLOBAL_PROPERTIES = "globalProperties";

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null) {
			request.setAttribute(
					VARIABLE_REQUEST_GLOBAL_PROPERTIES,
					GlobalProperties.getInstance());
		}
		return true;
	}

}
