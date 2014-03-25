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

	/*private String varCodi;
	private Date dataCreacio;
	private Date dataModificacio;
	private Date dataDocument;

	private String documentCodi;
	private String documentNom;

	private boolean adjunt = false;
	private String adjuntId;
	private String adjuntTitol;

	private boolean signat = false;
	private Long signaturaPortasignaturesId;
	private String signaturaUrlVerificacio;

	private boolean registrat = false;
	private String registreNumero;
	private Date registreData;
	private String registreOficinaCodi;
	private String registreOficinaNom;
	private boolean registreEntrada = true;*/

	private String error;

	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	private static final long serialVersionUID = -4307890997577367155L;

}
