/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per a evitar processar les pàgines amb Sitemesh.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NoDecorarInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (NoDecorarHelper.isRequestSenseDecoracio(request)) {
			NoDecorarHelper.redirigirUriSenseDecoracio(request, response);
			return false;
		}
		return true;
	}

}
