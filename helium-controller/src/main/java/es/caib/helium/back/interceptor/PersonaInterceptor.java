/**
 * 
 */
package es.caib.helium.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.helium.logic.intf.dto.PersonaDto;
import es.caib.helium.logic.intf.service.AplicacioService;
import lombok.RequiredArgsConstructor;

/**
 * Interceptor per guardar a la sessió les dades de la persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Component
public class PersonaInterceptor implements AsyncHandlerInterceptor {

	public static final String VARIABLE_SESSIO_PERSONA = "dadesPersona";

	private final AplicacioService aplicacioService;

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

}
