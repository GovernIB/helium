/**
 * 
 */
package net.conselldemallorca.helium.api.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * DTO amb informació d'una document de la zonaper.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ZonaperDocumentDto {

	public enum DocumentEventTipus {
		ARXIU,
		REFGD
	}

	protected String nom;
	protected DocumentEventTipus tipus;
	protected String referencia;
	protected String arxiuNom;
	protected byte[] arxiuContingut;

}
