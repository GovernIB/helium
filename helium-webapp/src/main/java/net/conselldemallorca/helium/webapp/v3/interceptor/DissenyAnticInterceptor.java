/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.interceptor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.util.privilegedactions.GetMethods;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper;

/**
 * Interceptor per evitar l'accés al disseny antic de la interfície si no s'és
 * administrador d'Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DissenyAnticInterceptor extends HandlerInterceptorAdapter {


	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws ServletException {


		PersonaDto persona = (PersonaDto)request.getSession().getAttribute("dadesPersona");
		// Si l'usuari autenticat no és administrador i intenta accedir a una url que no és /v3 rep una redirecció
		if (persona != null 
				&& !persona.isAdmin() 
				&& !request.getServletPath().contains("/v3")) {
				MissatgesHelper.warning(
						request, 
						"No es pot accedir a la pàgina del disseny antic sense el rol d'administrador.");
				try {
					response.sendRedirect("/helium/v3");
				} catch (IOException e) {
				}
				return false;
		}
		return true;
	}

}
