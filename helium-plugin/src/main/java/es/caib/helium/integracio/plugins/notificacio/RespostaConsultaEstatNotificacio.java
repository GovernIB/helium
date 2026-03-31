/**
 * 
 */
package es.caib.helium.integracio.plugins.notificacio;

import java.util.Date;

/**
 * Informació retornada per la consulta de l'estat d'una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaConsultaEstatNotificacio {

	private NotificacioEstat estat;	
    protected boolean error;
    protected Date errorData;
    protected String errorDescripcio;


	public NotificacioEstat getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstat estat) {
		this.estat = estat;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public Date getErrorData() {
		return errorData;
	}
	public void setErrorData(Date errorData) {
		this.errorData = errorData;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
}
