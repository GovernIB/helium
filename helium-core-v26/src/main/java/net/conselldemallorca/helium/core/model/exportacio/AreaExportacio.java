/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'una àrea per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AreaExportacio implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;

	private String tipus;
	private String pare;



	public AreaExportacio(
			String codi,
			String nom,
			String tipus) {
		this.codi = codi;
		this.nom = nom;
		this.tipus = tipus;
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
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getPare() {
		return pare;
	}
	public void setPare(String pare) {
		this.pare = pare;
	}



	private static final long serialVersionUID = 1L;

}
