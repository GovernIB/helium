package es.caib.helium.integracio.domini.validacio;

import lombok.Data;

@Data
public class BaseResponse {

	protected static final String ESTAT_OK = "OK";
	protected static final String ESTAT_ERROR = "ERROR";

	protected String estat;
	protected String errorCodi;
	protected String errorDescripcio;
	protected String errorExcepcio;
	
	public boolean isEstatOk() {
		return ESTAT_OK.equals(estat);
	}
	public boolean isEstatError() {
		return ESTAT_ERROR.equals(estat);
	}
}
