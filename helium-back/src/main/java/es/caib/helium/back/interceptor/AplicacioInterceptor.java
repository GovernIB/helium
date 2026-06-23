/**
 *
 */
package es.caib.helium.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${app.version}")
	private String appVersion;
	@Value("${app.build.timestamp}")
	private String appBuildTimestamp;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) {
		request.setAttribute(
				"requestLocale",
				RequestContextUtils.getLocale(request).getLanguage());
		request.setAttribute(
			"versioNom",
			appVersion);
		request.setAttribute(
			"versioData",
			appBuildTimestamp);
		return true;
	}

}
