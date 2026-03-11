/**
 * 
 */
package es.caib.helium.commons.dto;

import java.io.Serializable;


/**
 * Objecte que representa una província provinent d'una font externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProvinciaDto implements Serializable {

	private String codi;
	private Long codiComunitat;
	private String nom;

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	
	public Long getCodiComunitat() {
		return codiComunitat;
	}
	public void setCodiComunitat(Long codiComunitat) {
		this.codiComunitat = codiComunitat;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	private static final long serialVersionUID = -5902192920575301790L;

}
