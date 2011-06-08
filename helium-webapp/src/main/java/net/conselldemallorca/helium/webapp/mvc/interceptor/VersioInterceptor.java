/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.interceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.conselldemallorca.helium.core.model.service.UpdateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per guardar al context d'aplicació la versió actual
 * de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class VersioInterceptor extends HandlerInterceptorAdapter {

	public static final String APP_SCOPE_VAR_NOM = "versioNom";
	public static final String APP_SCOPE_VAR_ERROR = "versioError";

	private UpdateService updateService;

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws ServletException {
		String versioNom = (String)request.getSession().getServletContext().getAttribute(APP_SCOPE_VAR_NOM);
		if (versioNom == null) {
			request.getSession().getServletContext().setAttribute(
					APP_SCOPE_VAR_NOM,
					updateService.getVersioActual());
			request.getSession().getServletContext().setAttribute(
					APP_SCOPE_VAR_ERROR,
					updateService.getErrorUpdate());
		}
		return true;
	}

	@Autowired
	public void setUpdateService(UpdateService updateService) {
		this.updateService = updateService;
	}

}
