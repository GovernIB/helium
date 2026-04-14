package es.caib.helium.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import es.caib.helium.back.helper.AjaxHelper;

@Component
public class AjaxInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		boolean resposta = AjaxHelper.comprovarAjaxInterceptor(request, response);
		return resposta;
	}
}
