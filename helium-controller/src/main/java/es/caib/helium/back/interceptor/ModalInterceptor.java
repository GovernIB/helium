/**
 * 
 */
package es.caib.helium.back.interceptor;

import es.caib.helium.back.helper.ModalHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor per a redirigir les peticions a finestres modals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ModalInterceptor implements AsyncHandlerInterceptor {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		boolean resposta = ModalHelper.comprovarModalInterceptor(request, response);
		return resposta;
	}

}
