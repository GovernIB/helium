/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

/**
 * Command per a la modificació d'un termini iniciat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTerminiModificarCommand {

	public enum TerminiModificacioTipus {
		DURADA,
		DATA_INICI,
		DATA_FI
	}

	private Long terminiId;
	private String tipus;
	private Date dataInici;
	private int anys;
	private int mesos;
	private int dies;
	private Date dataFi;
	private String nom;

	public Long getTerminiId() {
		return terminiId;
	}
	public void setTerminiId(Long terminiId) {
		this.terminiId = terminiId;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
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
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
}
