/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.Date;

/**
 * DTO amb informació d'un termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTerminiDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private boolean duradaPredefinida;
	private int anys;
	private int mesos;
	private int dies;
	private boolean laborable;
	private boolean manual = true;

	private boolean iniciat;
	private Date iniciatDataInici;
	private Date iniciatDataFi;
	private Date iniciatDataAturada;
	private Date iniciatDataCancelacio;
	private Date iniciatDataFiProrroga;
	private Date iniciatDataCompletat;
	private int iniciatDiesAturat;
	private int iniciatAnys;
	private int iniciatMesos;
	private int iniciatDies;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public boolean isDuradaPredefinida() {
		return duradaPredefinida;
	}
	public void setDuradaPredefinida(boolean duradaPredefinida) {
		this.duradaPredefinida = duradaPredefinida;
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
	public boolean isManual() {
		return manual;
	}
	public void setManual(boolean manual) {
		this.manual = manual;
	}
	public boolean isIniciat() {
		return iniciat;
	}
	public void setIniciat(boolean iniciat) {
		this.iniciat = iniciat;
	}
	public Date getIniciatDataInici() {
		return iniciatDataInici;
	}
	public void setIniciatDataInici(Date iniciatDataInici) {
		this.iniciatDataInici = iniciatDataInici;
	}
	public Date getIniciatDataFi() {
		return iniciatDataFi;
	}
	public void setIniciatDataFi(Date iniciatDataFi) {
		this.iniciatDataFi = iniciatDataFi;
	}
	public Date getIniciatDataAturada() {
		return iniciatDataAturada;
	}
	public void setIniciatDataAturada(Date iniciatDataAturada) {
		this.iniciatDataAturada = iniciatDataAturada;
	}
	public Date getIniciatDataCancelacio() {
		return iniciatDataCancelacio;
	}
	public void setIniciatDataCancelacio(Date iniciatDataCancelacio) {
		this.iniciatDataCancelacio = iniciatDataCancelacio;
	}
	public Date getIniciatDataFiProrroga() {
		return iniciatDataFiProrroga;
	}
	public void setIniciatDataFiProrroga(Date iniciatDataFiProrroga) {
		this.iniciatDataFiProrroga = iniciatDataFiProrroga;
	}
	public Date getIniciatDataCompletat() {
		return iniciatDataCompletat;
	}
	public void setIniciatDataCompletat(Date iniciatDataCompletat) {
		this.iniciatDataCompletat = iniciatDataCompletat;
	}
	public int getIniciatDiesAturat() {
		return iniciatDiesAturat;
	}
	public void setIniciatDiesAturat(int iniciatDiesAturat) {
		this.iniciatDiesAturat = iniciatDiesAturat;
	}
	public int getIniciatAnys() {
		return iniciatAnys;
	}
	public void setIniciatAnys(int iniciatAnys) {
		this.iniciatAnys = iniciatAnys;
	}
	public int getIniciatMesos() {
		return iniciatMesos;
	}
	public void setIniciatMesos(int iniciatMesos) {
		this.iniciatMesos = iniciatMesos;
	}
	public int getIniciatDies() {
		return iniciatDies;
	}
	public void setIniciatDies(int iniciatDies) {
		this.iniciatDies = iniciatDies;
	}

	public String getDurada() {
		return toString() + ((dies > 0) ? ((laborable) ? " laborables" : " naturals") : "");
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		boolean plural = false;
		if (anys > 0) {
			sb.append(anys);
			plural = anys > 1;
			sb.append((plural) ? " anys": " any");
			if (mesos > 0 && dies > 0)
				sb.append(", ");
			else if (mesos > 0 || dies > 0)
				sb.append(" i ");
		}
		if (mesos > 0) {
			sb.append(mesos);
			plural = mesos > 1;
			sb.append((plural) ? " mesos": " mes");
			if (dies > 0)
				sb.append(" i ");
		}
		if (dies > 0) {
			sb.append(dies);
			plural = dies > 1;
			sb.append((plural) ? " dies": " dia");
		}
		return sb.toString();
	}
}
