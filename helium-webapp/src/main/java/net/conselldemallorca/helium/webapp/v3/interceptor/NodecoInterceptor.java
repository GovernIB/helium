/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.webapp.v3.helper.NodecoHelper;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per a redirigir les peticions a finestres sense
 * decoració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NodecoInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		return NodecoHelper.comprovarNodecoInterceptor(request, response);
	}
}
