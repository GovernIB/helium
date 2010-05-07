/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.util.GlobalProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per guardar les propietats globals per a cada petició
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class GlobalPropertiesInterceptor extends HandlerInterceptorAdapter {

	public static final String VARIABLE_REQUEST_GLOBAL_PROPERTIES = "globalProperties";

	private GlobalProperties globalProperties;



	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null) {
			request.setAttribute(
					VARIABLE_REQUEST_GLOBAL_PROPERTIES,
					globalProperties);
		}
		return true;
	}



	@Autowired
	public void setGlobalProperties(GlobalProperties globalProperties) {
		this.globalProperties = globalProperties;
	}

}
