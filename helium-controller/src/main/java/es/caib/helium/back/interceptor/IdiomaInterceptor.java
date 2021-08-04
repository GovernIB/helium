/**
 * 
 */
package es.caib.helium.back.interceptor;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.helium.logic.intf.service.AdminService;
import lombok.RequiredArgsConstructor;

/**
 * Interceptor per a canviar l'idioma depenent d'un paràmetre
 * del request.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Component
public class IdiomaInterceptor implements AsyncHandlerInterceptor {

	public static final String DEFAULT_PARAM_NAME = "lang";

	private final AdminService adminService;


	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
		if (request.getUserPrincipal() != null) {
			String codiIdioma = request.getParameter(DEFAULT_PARAM_NAME);
			
			if (codiIdioma != null) {
				adminService.setIdiomaPref(
						request.getUserPrincipal().getName(),
						codiIdioma);
				LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
				if (localeResolver == null)
					throw new IllegalStateException  ("No LocaleResolver found: not in a DispatcherServlet request?");
				LocaleEditor localeEditor = new LocaleEditor();
				localeEditor.setAsText(codiIdioma);
				localeResolver.setLocale(request, response, (Locale)localeEditor.getValue());
			}
		}
		return true;
	}

}
