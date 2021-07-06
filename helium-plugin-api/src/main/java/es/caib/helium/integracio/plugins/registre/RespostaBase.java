/**
 * 
 */
package es.caib.helium.integracio.plugins.registre;


/**
 * Resposta base del plugin de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaBase {

	public static final String ERROR_CODI_OK = "OK";
	public static final String ERROR_CODI_ERROR = "ERROR";

	private String errorCodi;
	private String errorDescripcio;

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

	public boolean isOk() {
		return ERROR_CODI_OK.equals(errorCodi);
	}
	public boolean isError() {
		return ERROR_CODI_ERROR.equals(errorCodi);
	}

}
