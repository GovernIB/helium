package es.caib.helium.integracio.domini.validacio;

import java.util.List;

import lombok.Data;

@Data
public class RespostaValidacioSignatura {

	public static final String ESTAT_OK = "OK";
	public static final String ESTAT_ERROR = "ERROR";

	protected String estat;
	protected String errorCodi;
	protected String errorDescripcio;
	private List<DadesCertificat> dadesCertificat;
}
