/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'un termini per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TerminiExportacio implements Serializable {

	private String codi;
	private String nom;
	private String descripcio;
	private int anys;
	private int mesos;
	private int dies;
	private boolean laborable;
	private Integer diesPrevisAvis;
	private boolean alertaPrevia;
	private boolean alertaFinal;
	private boolean alertaCompletat;
	private boolean isManual;

	public TerminiExportacio(
			String codi,
			String nom,
			String descripcio,
			int anys,
			int mesos,
			int dies,
			boolean laborable,
			Integer diesPrevisAvis,
			boolean alertaPrevia,
			boolean alertaFinal,
			boolean isManual) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.anys = anys;
		this.mesos = mesos;
		this.dies = dies;
		this.laborable = laborable;
		this.diesPrevisAvis = diesPrevisAvis;
		this.alertaPrevia = alertaPrevia;
		this.alertaFinal = alertaFinal;
		this.isManual = isManual;
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
	public Integer getDiesPrevisAvis() {
		return diesPrevisAvis;
	}
	public void setDiesPrevisAvis(Integer diesPrevisAvis) {
		this.diesPrevisAvis = diesPrevisAvis;
	}
	public boolean isAlertaPrevia() {
		return alertaPrevia;
	}
	public void setAlertaPrevia(boolean alertaPrevia) {
		this.alertaPrevia = alertaPrevia;
	}
	public boolean isAlertaFinal() {
		return alertaFinal;
	}
	public void setAlertaFinal(boolean alertaFinal) {
		this.alertaFinal = alertaFinal;
	}
	public boolean isAlertaCompletat() {
		return alertaCompletat;
	}
	public void setAlertaCompletat(boolean alertaCompletat) {
		this.alertaCompletat = alertaCompletat;
	}
	public boolean isManual() {
		return isManual;
	}
	public void setManual(boolean isManual) {
		this.isManual = isManual;
	}
	
	private static final long serialVersionUID = 1L;
}
