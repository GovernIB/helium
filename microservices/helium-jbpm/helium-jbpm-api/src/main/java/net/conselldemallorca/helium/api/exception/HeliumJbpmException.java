package net.conselldemallorca.helium.api.exception;

import lombok.Getter;

@Getter
public class HeliumJbpmException extends Exception {

	private int httpStatus = 500;

	public HeliumJbpmException(String message) {
		super(message);
	}

	public HeliumJbpmException(Exception e) {
		super(e);
	}

	public HeliumJbpmException(int httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

}
