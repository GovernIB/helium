package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

public class PortafirmesIniciFluxRespostaDto implements Serializable {

	private String idTransaccio;
	private String urlRedireccio;
	private boolean error;
	private String errorDescripcio;
	
	public String getIdTransaccio() {
		return idTransaccio;
	}
	public void setIdTransaccio(String idTransaccio) {
		this.idTransaccio = idTransaccio;
	}
	public String getUrlRedireccio() {
		return urlRedireccio;
	}
	public void setUrlRedireccio(String urlRedireccio) {
		this.urlRedireccio = urlRedireccio;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}


	private static final long serialVersionUID = -1279092736557310555L;

}
