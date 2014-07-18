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

	private String valorRetornat;



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
	public String getValorRetornat() {
		return valorRetornat;
	}
	public void setValorRetornat(String valorRetornat) {
		this.valorRetornat = valorRetornat;
	}

}
