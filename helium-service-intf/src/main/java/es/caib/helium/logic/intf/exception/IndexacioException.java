package es.caib.helium.logic.intf.exception;

import org.apache.commons.lang.exception.ExceptionUtils;



@SuppressWarnings("serial")
public class IndexacioException extends RuntimeException {

	public IndexacioException(String message) {
		super("Error en la indexació. " + message);
	}
	
	public IndexacioException(
			String message,
			Throwable cause) {
		super(message + ". " + ExceptionUtils.getRootCauseMessage(cause), cause);
	}

}
