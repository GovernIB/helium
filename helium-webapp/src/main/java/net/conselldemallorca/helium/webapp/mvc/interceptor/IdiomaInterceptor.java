/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.interceptor;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.helium.logic.intf.service.AdminService;

/**
 * Interceptor per a canviar l'idioma depenent d'un paràmetre
 * del request.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class IdiomaInterceptor extends HandlerInterceptorAdapter {
	public static final String DEFAULT_PARAM_NAME = "lang";
	private String paramName = DEFAULT_PARAM_NAME;
	
	private AdminService adminService;
	
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
		if (request.getUserPrincipal() != null) {
			String codiIdioma = request.getParameter(this.paramName);
			
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

	@Autowired
	public void setPersonaService(AdminService adminService) {
		this.adminService = adminService;
	}

}
