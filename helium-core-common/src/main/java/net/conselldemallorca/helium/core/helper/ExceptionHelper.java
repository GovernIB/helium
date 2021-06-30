/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar excepcions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExceptionHelper {

	public boolean cercarMissatgeDinsCadenaExcepcions(String missatge, Throwable ex) {
		//logger.info(">>> [PSIGN] Cercant missatge dins excepcio (missatge=" + missatge + ", getMessage=" + ex.getMessage() + ")");
		if (ex.getMessage().contains(missatge))
			return true;
		if (ex.getCause() != null)
			return cercarMissatgeDinsCadenaExcepcions(missatge, ex.getCause());
		else
			return false;
	}
	
	public String getMissageFinalCadenaExcepcions(Throwable ex) {
		if (ex.getCause() == null) {
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    ex.printStackTrace(pw);
		    logger.error(sw.toString());
		    return sw.toString();
		} else {
			return getMissageFinalCadenaExcepcions(ex.getCause());
		}
	}

	private static final Log logger = LogFactory.getLog(ExceptionHelper.class);
}
