/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;

/**
 * Controlador base que implementa funcionalitats comunes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseController implements MessageSourceAware {
	MessageSource messageSource;

	protected String modalUrlTancar() {
		return "v3/utils/modalTancar";
	}

	public String getMessage(HttpServletRequest request, String key, Object[] args) {
		String message = messageSource.getMessage(key, args, "???" + key + "???", new RequestContext(request).getLocale());
		return message;
	}

	public String getMessage(HttpServletRequest request, String key) {
		return getMessage(request, key, null);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
