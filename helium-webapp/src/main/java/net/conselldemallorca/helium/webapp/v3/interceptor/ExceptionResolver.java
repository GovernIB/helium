package net.conselldemallorca.helium.webapp.v3.interceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;

public class ExceptionResolver implements HandlerExceptionResolver {

	@Autowired private AplicacioService aplicacioService;
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		ex.printStackTrace();
		aplicacioService.excepcioSave(request.getRequestURI(), getRequestParameters(request), ex);
		return null;
	}
	
	private String getRequestParameters(HttpServletRequest request) {
		Enumeration<String> params = request.getParameterNames();
		StringBuilder parameters = new StringBuilder();
		while (params.hasMoreElements()) {
			String paramName = params.nextElement();
			String paramValue = request.getParameter(paramName);
			parameters.append(paramName).append("=").append(paramValue);
			if (params.hasMoreElements()) {
				parameters.append(", ");
			}
		}
		return parameters.toString();
	}
}