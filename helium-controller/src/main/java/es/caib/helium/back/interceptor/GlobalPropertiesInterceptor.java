/**
 * 
 */
package es.caib.helium.back.interceptor;

import es.caib.helium.logic.intf.service.AplicacioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
					aplicacioService.getGlobalProperties());
		}
		return true;
	}

}
