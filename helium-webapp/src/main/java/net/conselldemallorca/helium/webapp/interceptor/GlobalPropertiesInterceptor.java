/**
 * 
 */
package net.conselldemallorca.helium.webapp.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;

/**
 * Interceptor per guardar les propietats globals per a cada petició
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GlobalPropertiesInterceptor extends HandlerInterceptorAdapter {

	public static final String VARIABLE_REQUEST_GLOBAL_PROPERTIES = "globalProperties";

	@Autowired
	private AplicacioService aplicacioService;

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null) {
			request.setAttribute(
					VARIABLE_REQUEST_GLOBAL_PROPERTIES,
					aplicacioService.propertyFindAll()); // GlobalProperties.getInstance());
		}
		return true;
	}

}
