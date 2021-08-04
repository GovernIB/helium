/**
 * 
 */
package es.caib.helium.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.helium.logic.intf.service.AplicacioService;
import lombok.RequiredArgsConstructor;

/**
 * Interceptor per guardar les propietats globals per a cada petició
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Component
public class GlobalPropertiesInterceptor implements AsyncHandlerInterceptor {

	public static final String VARIABLE_REQUEST_GLOBAL_PROPERTIES = "globalProperties";

	private final AplicacioService aplicacioService;

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null) {
			request.setAttribute(
					VARIABLE_REQUEST_GLOBAL_PROPERTIES,
					aplicacioService.getGlobalProperties().findAll());
		}
		return true;
	}

}
