/**
 * 
 */
package es.caib.helium.logic.intf.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'una agrupació per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AgrupacioExportacio implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;
	private int ordre;

	public AgrupacioExportacio(
			String codi,
			String nom,
			String descripcio,
			int ordre) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.ordre = ordre;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}



	private static final long serialVersionUID = 1L;

}
