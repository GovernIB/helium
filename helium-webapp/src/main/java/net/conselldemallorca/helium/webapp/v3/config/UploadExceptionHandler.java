package net.conselldemallorca.helium.webapp.v3.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;


public class UploadExceptionHandler implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(
			HttpServletRequest request, 
			HttpServletResponse response, 
			Object handler,
			Exception ex) {
		if(ex instanceof MaxUploadSizeExceededException) {
			MissatgesHelper.error(
					request, 
					"El fitxer excedeix la mida màxima permesa");
		}
		return null;
	}
	
}
