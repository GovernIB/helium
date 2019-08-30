/**
 * 
 */
package net.conselldemallorca.helium.webapp.interceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;

/**
 * Interceptor per guardar al context d'aplicació la versió actual
 * de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class VersioInterceptor extends HandlerInterceptorAdapter {

	public static final String APP_SCOPE_VAR_NOM = "versioNom";
	public static final String APP_SCOPE_VAR_ERROR = "versioError";

	@Autowired
	private AplicacioService aplicacioService;

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws ServletException {
		String versioNom = (String)request.getSession().getServletContext().getAttribute(APP_SCOPE_VAR_NOM);
		if (versioNom == null) {
			request.getSession().getServletContext().setAttribute(
					APP_SCOPE_VAR_NOM,
					aplicacioService.getVersioActual());
			/*request.getSession().getServletContext().setAttribute(
					APP_SCOPE_VAR_ERROR,
					updateService.getErrorUpdate());*/
		}
		return true;
	}

}
