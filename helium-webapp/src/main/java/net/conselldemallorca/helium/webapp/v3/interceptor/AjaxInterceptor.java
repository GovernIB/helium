/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.webapp.v3.helper.AjaxHelper;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per a redirigir les peticions fetes amb AJAX.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AjaxInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		boolean resposta = AjaxHelper.comprovarAjaxInterceptor(request, response);
		//System.out.println(">>> AJAX: " + request.getRequestURI() + ", " + AjaxHelper.isAjax(request));
		return resposta;
	}

}
