/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.signatura;

import java.util.List;

/**
 * Resposta a una petició de validació de signatura
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaValidacioSignatura {

	public static final String ESTAT_OK = "OK";
	public static final String ESTAT_ERROR = "ERROR";

	protected String estat;
	protected String errorCodi;
	protected String errorDescripcio;
	private List<DadesCertificat> dadesCertificat;



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
	public List<DadesCertificat> getDadesCertificat() {
		return dadesCertificat;
	}
	public void setDadesCertificat(List<DadesCertificat> dadesCertificat) {
		this.dadesCertificat = dadesCertificat;
	}

	public boolean isEstatOk() {
		return ESTAT_OK.equals(estat);
	}
	public boolean isEstatError() {
		return ESTAT_ERROR.equals(estat);
	}

}
