/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.tramitacio;

/**
 * 
 * @author Limit Tecnologies
 */
public class TramitacioResponse {

	protected ErrorCodiTipus errorCodi;
	protected String errorDescripcio;

	public ErrorCodiTipus getErrorCodi() {
		return errorCodi;
	}
	public void setErrorCodi(ErrorCodiTipus errorCodi) {
		this.errorCodi = errorCodi;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}

}
