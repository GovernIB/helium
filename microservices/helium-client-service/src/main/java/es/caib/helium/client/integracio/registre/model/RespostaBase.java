package es.caib.helium.client.integracio.registre.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespostaBase {

	public static final String ERROR_CODI_OK = "OK";
	public static final String ERROR_CODI_ERROR = "ERROR";
	
	private String errorCodi;
	private String errorDescripcio;
}