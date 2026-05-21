package es.caib.helium.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import es.caib.helium.commons.utils.GlobalProperties;

/**
 * Interceptor per guardar a la sessió les dades de la persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class GlobalPropertiesInterceptor implements HandlerInterceptor {

	public static final String VARIABLE_REQUEST_GLOBAL_PROPERTIES = "globalProperties";

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null) {
			request.setAttribute(
					VARIABLE_REQUEST_GLOBAL_PROPERTIES,
					GlobalProperties.getInstance());
		}
		return true;
	}

}
