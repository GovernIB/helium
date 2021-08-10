package net.conselldemallorca.helium.jbpm3.integracio;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HeliumJbpmException extends RuntimeException {

	private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

	public HeliumJbpmException(String message) {
		super(message);
	}

	public HeliumJbpmException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

}
