package es.caib.helium.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import es.caib.helium.commons.config.BaseConfig;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.logic.intf.service.AplicacioService;

/**
 * Interceptor per guardar a la sessió les dades de la persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PersonaInterceptor implements HandlerInterceptor {
	public static final String VARIABLE_SESSIO_PERSONA = "dadesPersona";

	@Autowired
	private AplicacioService aplicacioService;

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		
		HttpSession session = request.getSession();
		PersonaDto persona = (PersonaDto)session.getAttribute(VARIABLE_SESSIO_PERSONA);
		if(persona != null) return true;
		
		if (request.getUserPrincipal() != null) {
			persona = aplicacioService.findPersonaAmbCodi(request.getUserPrincipal().getName());
			persona.setAdmin(request.isUserInRole(BaseConfig.ROLE_ADMIN));
			session.setAttribute(VARIABLE_SESSIO_PERSONA, persona);
		}
		return true;
	}
}
