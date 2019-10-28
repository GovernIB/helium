/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.notificacio;

import java.util.List;

/**
 * Informació retornada per l'alta d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaEnviar {

	
	private boolean error;
	private String errorDescripcio;
	private String identificador;
	private NotificacioEstat estat;
	private List<EnviamentReferencia> referencies;

	
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public NotificacioEstat getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstat estat) {
		this.estat = estat;
	}
	public List<EnviamentReferencia> getReferencies() {
		return referencies;
	}
	public void setReferencies(List<EnviamentReferencia> referencies) {
		this.referencies = referencies;
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
}
