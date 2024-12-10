/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.v3.core.api.dto.ExcepcioLogDto;

/**
 * Helper per a gestionar excepcions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExceptionHelper {

	private LinkedList<ExcepcioLogDto> excepcions = new LinkedList<ExcepcioLogDto>();
	
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
	
	public String getRouteCauses(Throwable e) {
		StringBuilder message = new StringBuilder();
		Throwable t = e;
		boolean root;
		do {
			message.append(t.getMessage());
			t = t.getCause();
			root = t == null || t == t.getCause();
			if (!root) {
				message.append(": ");
			}
		} while (!root);
		return message.toString();
	}
	
	public List<ExcepcioLogDto> findAll() {
		int index = 0;
		for (ExcepcioLogDto excepcio: excepcions) {
			excepcio.setIndex(new Long(index++));
		}
		return excepcions;
	}

	public void addExcepcio(String peticio, String params, Throwable exception) {
		while (excepcions.size() >= 20) {
			excepcions.remove(excepcions.size() - 1);
		}
		excepcions.add(
				0,
				new ExcepcioLogDto(peticio, params, exception));
	}

	/** Mètode per obtenir el text de l'error i tot l'stacktrace.
	 * 
	 * @param error
	 * @return
	 */
	public static String getErrorText(Throwable error) {
		StringBuilder sb = new StringBuilder();
		if (error != null) {
			sb.append(error.getLocalizedMessage());
			for (StackTraceElement element : error.getStackTrace()) {
		        sb.append("\nat ");
		        sb.append(element.toString());
		    }
		}
		return sb.toString();
	}
	private static final Log logger = LogFactory.getLog(ExceptionHelper.class);
}
