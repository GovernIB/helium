package net.conselldemallorca.helium.webapp.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.conselldemallorca.helium.core.helper.RequestWarningHelper;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

public class WarningsInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private RequestWarningHelper requestWarningHelper;
	
	@Override
	public void postHandle(
			HttpServletRequest request, 
			HttpServletResponse response, 
			Object handler, 
			ModelAndView modelAndView) throws Exception {
		for (String warning : requestWarningHelper.getWarnings()) {
			MissatgesHelper.warning(request, warning);
		}
		requestWarningHelper.clear();
	}
}
