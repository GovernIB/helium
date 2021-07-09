/**
 * 
 */
package es.caib.helium.back.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per guardar al context d'aplicació la versió actual
 * de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class VersioInterceptor implements AsyncHandlerInterceptor {

	// TODO: passar a un fitxer de properties
	public static final String VERSIO_ACTUAL_STR = "4.0.0";
	public static final String VERSIO_ACTUAL_DATA = "2021.04.30 11:00";

	
	public static final String APP_SCOPE_VAR_NOM = "versioNom";
	public static final String APP_SCOPE_VAR_DATA = "versioData";
	public static final String APP_SCOPE_VAR_ERROR = "versioError";


	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws ServletException {
		String versioNom = (String)request.getSession().getServletContext().getAttribute(APP_SCOPE_VAR_NOM);
		if (versioNom == null) {
			request.getSession().getServletContext().setAttribute(
					APP_SCOPE_VAR_NOM,
					VERSIO_ACTUAL_STR);
			request.getSession().getServletContext().setAttribute(
					APP_SCOPE_VAR_DATA,
					VERSIO_ACTUAL_DATA);
		}
		return true;
	}
}
