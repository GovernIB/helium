/**
 * 
 */
package es.caib.helium.logic.helper;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

/**
 * Helper per a mostrar missatges multiidioma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MessageServiceHelper implements MessageSourceAware {

	private MessageSource messageSource;

	public String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}
	public String getMessage(String key) {
		return getMessage(key, null);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
