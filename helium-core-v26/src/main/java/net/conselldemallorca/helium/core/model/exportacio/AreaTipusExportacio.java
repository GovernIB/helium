/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'un tipus d'àrea per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AreaTipusExportacio implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;



	public AreaTipusExportacio(
			String codi,
			String nom) {
		this.codi = codi;
		this.nom = nom;
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



	private static final long serialVersionUID = 1L;

}
