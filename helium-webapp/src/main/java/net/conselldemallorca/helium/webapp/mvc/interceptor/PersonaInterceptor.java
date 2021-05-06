/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.service.AplicacioService;

/**
 * Interceptor per guardar a la sessió les dades de la persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PersonaInterceptor extends HandlerInterceptorAdapter {

	public static final String VARIABLE_SESSIO_PERSONA = "dadesPersona";

	private AplicacioService aplicacioService;



	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null) {
			String usuariCodi = request.getUserPrincipal().getName();
			HttpSession session = request.getSession();
			PersonaDto persona = (PersonaDto)session.getAttribute(VARIABLE_SESSIO_PERSONA);
			if (persona == null) {
				persona = aplicacioService.findPersonaAmbCodi(usuariCodi);
				persona.setAdmin(request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("HEL_ADMIN"));
				session.setAttribute(VARIABLE_SESSIO_PERSONA, persona);
			}
		}
		return true;
	}



	@Autowired
	public void setAplicacioService(AplicacioService aplicacioService) {
		this.aplicacioService = aplicacioService;
	}

}
