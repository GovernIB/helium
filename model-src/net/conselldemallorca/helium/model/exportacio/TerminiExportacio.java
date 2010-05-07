/**
 * 
 */
package net.conselldemallorca.helium.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'una enumeracio per exportar
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class TerminiExportacio implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;
	private int anys;
	private int mesos;
	private int dies;
	private boolean laborable;



	public TerminiExportacio(
			String codi,
			String nom,
			String descripcio,
			int anys,
			int mesos,
			int dies,
			boolean laborable) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.anys = anys;
		this.mesos = mesos;
		this.dies = dies;
		this.laborable = laborable;
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
	public int getAnys() {
		return anys;
	}
	public void setAnys(int anys) {
		this.anys = anys;
	}
	public int getMesos() {
		return mesos;
	}
	public void setMesos(int mesos) {
		this.mesos = mesos;
	}
	public int getDies() {
		return dies;
	}
	public void setDies(int dies) {
		this.dies = dies;
	}
	public boolean isLaborable() {
		return laborable;
	}
	public void setLaborable(boolean laborable) {
		this.laborable = laborable;
	}



	private static final long serialVersionUID = 1L;

}
