/**
 * 
 */
package es.caib.helium.back.helper;

import java.util.Locale;

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
public class MessageHelper implements MessageSourceAware {

	private MessageSource messageSource;

	public String getMessage(String[] keys, Object[] vars, Locale locale) {
		String msg = "???" + (keys.length > 0 ? keys[keys.length-1] : "") + "???";
		boolean found = false;
		int i = 0;
		while( ! found && i < keys.length) {		
			try {
				msg = messageSource.getMessage(
						keys[i],
						vars,
						locale);
				found = true;
			} catch (NoSuchMessageException ex) {
				i++;
			}
		}
		if( ! found ) {
			String key = keys[keys.length-1]; 
			if (key.startsWith("enum.")){
				msg = key.substring(key.lastIndexOf(".") + 1);
			}			
		}
		return msg;
	}
	public String getMessage(String key, Object[] vars, Locale locale) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					locale);
		} catch (NoSuchMessageException ex) {
			if (key.startsWith("enum.")){
				return key.substring(key.lastIndexOf(".") + 1);
			}
			return "???" + key + "???";
		}
	}
	public String getMessage(String key, Object[] vars) {
		return getMessage(key, vars, null);
	}
	public String getMessage(String key) {
		return getMessage(key, null, null);
	}

	public void setMessageSource(MessageSource messageSource) {
		INSTANCE.messageSource = messageSource;
	}

	
	public static MessageHelper INSTANCE = new MessageHelper();
	
	public static MessageHelper getInstance() {
		return INSTANCE;
	}
}
