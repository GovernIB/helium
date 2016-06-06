package net.conselldemallorca.helium.v3.core.api.exception;

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
