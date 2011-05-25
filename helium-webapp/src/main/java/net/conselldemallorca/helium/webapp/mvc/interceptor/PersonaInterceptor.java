/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.service.PluginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per guardar a la sessió les dades de la persona
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PersonaInterceptor extends HandlerInterceptorAdapter {

	public static final String VARIABLE_SESSIO_PERSONA = "dadesPersona";

	private PluginService pluginService;



	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (request.getUserPrincipal() != null) {
			String usuariCodi = request.getUserPrincipal().getName();
			HttpSession session = request.getSession();
			PersonaDto persona = (PersonaDto)session.getAttribute(VARIABLE_SESSIO_PERSONA);
			if (persona == null) {
				persona = pluginService.findPersonaAmbCodi(usuariCodi);
				session.setAttribute(VARIABLE_SESSIO_PERSONA, persona);
			}
		}
		return true;
	}



	@Autowired
	public void setPluginService(PluginService pluginService) {
		this.pluginService = pluginService;
	}

}
