/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.Date;

/**
 * Classe per retornar la informació d'un expedient als handlers.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientInfo {

	public enum IniciadorTipus {
		INTERN,
		SISTRA}

	private String titol;
	private String numero;
	private String numeroDefault;
	private Date dataInici;
	private Date dataFi;
	private String comentari;
	private String infoAturat;
	private IniciadorTipus iniciadorTipus;
	private String iniciadorCodi;
	private String responsableCodi;

	private String estatCodi;
	private String expedientTipusCodi;
	private String entornCodi;



	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getNumeroDefault() {
		return numeroDefault;
	}
	public void setNumeroDefault(String numeroDefault) {
		this.numeroDefault = numeroDefault;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}
	public String getInfoAturat() {
		return infoAturat;
	}
	public void setInfoAturat(String infoAturat) {
		this.infoAturat = infoAturat;
	}
	public IniciadorTipus getIniciadorTipus() {
		return iniciadorTipus;
	}
	public void setIniciadorTipus(IniciadorTipus iniciadorTipus) {
		this.iniciadorTipus = iniciadorTipus;
	}
	public String getIniciadorCodi() {
		return iniciadorCodi;
	}
	public void setIniciadorCodi(String iniciadorCodi) {
		this.iniciadorCodi = iniciadorCodi;
	}
	public String getResponsableCodi() {
		return responsableCodi;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}
	public String getEstatCodi() {
		return estatCodi;
	}
	public void setEstatCodi(String estatCodi) {
		this.estatCodi = estatCodi;
	}
	public String getExpedientTipusCodi() {
		return expedientTipusCodi;
	}
	public void setExpedientTipusCodi(String expedientTipusCodi) {
		this.expedientTipusCodi = expedientTipusCodi;
	}
	public String getEntornCodi() {
		return entornCodi;
	}
	public void setEntornCodi(String entornCodi) {
		this.entornCodi = entornCodi;
	}

}
