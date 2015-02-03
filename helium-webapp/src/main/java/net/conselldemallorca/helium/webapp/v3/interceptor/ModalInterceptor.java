/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.webapp.v3.helper.ModalHelper;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per a redirigir les peticions a finestres modals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ModalInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		boolean resposta = ModalHelper.comprovarModalInterceptor(request, response);
		return resposta;
	}

}
