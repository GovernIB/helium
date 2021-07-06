package es.caib.helium.client.integracio.tramitacio.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespostaJustificantRecepcio {
	
	public static final String ERROR_CODI_OK = "OK";
	public static final String ERROR_CODI_ERROR = "ERROR";

	private String errorCodi;
	private String errorDescripcio;

	private Date data;

	public boolean isOk() {
		return ERROR_CODI_OK.equals(errorCodi);
	}
	public boolean isError() {
		return ERROR_CODI_ERROR.equals(errorCodi);
	}
}
