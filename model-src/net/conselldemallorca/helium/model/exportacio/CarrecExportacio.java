/**
 * 
 */
package net.conselldemallorca.helium.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'un càrrec per exportar
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CarrecExportacio implements Serializable {

	private String codi;
	private String nomHome;
	private String nomDona;
	private String tractamentHome;
	private String tractamentDona;
	private String descripcio;
	private String areaCodi;



	public CarrecExportacio(
			String codi,
			String nomHome,
			String nomDona,
			String tractamentHome,
			String tractamentDona) {
		this.codi = codi;
		this.nomHome = nomHome;
		this.nomDona = nomDona;
		this.tractamentHome = tractamentHome;
		this.tractamentDona = tractamentDona;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNomHome() {
		return nomHome;
	}
	public void setNomHome(String nomHome) {
		this.nomHome = nomHome;
	}
	public String getNomDona() {
		return nomDona;
	}
	public void setNomDona(String nomDona) {
		this.nomDona = nomDona;
	}
	public String getTractamentHome() {
		return tractamentHome;
	}
	public void setTractamentHome(String tractamentHome) {
		this.tractamentHome = tractamentHome;
	}
	public String getTractamentDona() {
		return tractamentDona;
	}
	public void setTractamentDona(String tractamentDona) {
		this.tractamentDona = tractamentDona;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getAreaCodi() {
		return areaCodi;
	}
	public void setAreaCodi(String areaCodi) {
		this.areaCodi = areaCodi;
	}



	private static final long serialVersionUID = 1L;

}
