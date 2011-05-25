/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

/**
 * Objecte que emmagatzema les dades d'una resposta de la custòdia documental.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaResponseCaib {

	private boolean error;
	private String errorCodi;
	private String errorDescripcio;



	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
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

}
