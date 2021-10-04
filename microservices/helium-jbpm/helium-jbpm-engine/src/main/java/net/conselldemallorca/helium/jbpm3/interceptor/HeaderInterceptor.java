/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.conselldemallorca.helium.api.util.ThreadLocalInfo;

/**
 * Interceptor per capturar el token d'autenticació i posar-lo al local thread.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeaderInterceptor extends HandlerInterceptorAdapter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
        
	@Override
	public boolean preHandle(
			HttpServletRequest request, 
			HttpServletResponse response, 
			Object handler) throws Exception {
		ThreadLocalInfo.setAuthorizationToken(request.getHeader(AUTHORIZATION_HEADER));
		return true;
	}

}
