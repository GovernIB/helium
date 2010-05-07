/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.model.service.PluginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor per guardar a la sessió les dades de la persona
 * 
 * @author Josep Gayà <josepg@limit.es>
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
			Persona persona = (Persona)session.getAttribute(VARIABLE_SESSIO_PERSONA);
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
