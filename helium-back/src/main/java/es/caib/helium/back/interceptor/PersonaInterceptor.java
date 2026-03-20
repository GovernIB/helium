package es.caib.helium.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import es.caib.helium.commons.dto.PersonaDto;

/**
 * Interceptor per guardar a la sessió les dades de la persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PersonaInterceptor implements HandlerInterceptor {
	public static final String VARIABLE_SESSIO_PERSONA = "dadesPersona";

	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		
		HttpSession session = request.getSession();
		PersonaDto persona = (PersonaDto)session.getAttribute(VARIABLE_SESSIO_PERSONA);
		if(persona != null) return true;
		
		if (request.getUserPrincipal() != null) {
			if(request.getUserPrincipal() instanceof OAuth2AuthenticationToken) {
				OAuth2AuthenticationToken principal = (OAuth2AuthenticationToken) request.getUserPrincipal();
				principal.getAuthorities();
				persona = new PersonaDto(
						principal.getPrincipal().getAttribute("preferred_username"), // codi
						principal.getPrincipal().getAttribute("given_name"), // nom
						principal.getPrincipal().getAttribute("family_name"), // llinatges
						principal.getPrincipal().getAttribute("email"), // email
						null // sexe
						);
				persona.setAdmin(request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("HEL_ADMIN"));
				session.setAttribute(VARIABLE_SESSIO_PERSONA, persona);
			}
		}
		return true;
	}
}
