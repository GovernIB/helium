/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura.afirma;

/**
 * Resposta base per a les peticions a @Firma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseResponse {

	protected static final String ESTAT_OK = "OK";
	protected static final String ESTAT_ERROR = "ERROR";

	protected String estat;
	protected String errorCodi;
	protected String errorDescripcio;
	protected String errorExcepcio;



	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public String getErrorCodi() {
		return errorCodi;
	}
	public void setErrorCodi(String errorCodi) {
		this.errorCodi = errorCodi;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public String getErrorExcepcio() {
		return errorExcepcio;
	}
	public void setErrorExcepcio(String errorExcepcio) {
		this.errorExcepcio = errorExcepcio;
	}

	public boolean isEstatOk() {
		return ESTAT_OK.equals(estat);
	}
	public boolean isEstatError() {
		return ESTAT_ERROR.equals(estat);
	}

}
