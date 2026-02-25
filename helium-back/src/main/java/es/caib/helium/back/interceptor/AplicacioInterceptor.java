/**
 * 
 */
package es.caib.helium.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AplicacioInterceptor implements AsyncHandlerInterceptor {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		request.setAttribute(
				"requestLocale",
				RequestContextUtils.getLocale(request).getLanguage());
		return true;
	}

}
