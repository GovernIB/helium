/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;



/**
 * DTO amb informació d'un document de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientDocumentDto extends DocumentDto {

	private String error;

	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	private static final long serialVersionUID = -4307890997577367155L;

}
