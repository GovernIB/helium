package es.caib.helium.client.integracio.custodia.model;

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
