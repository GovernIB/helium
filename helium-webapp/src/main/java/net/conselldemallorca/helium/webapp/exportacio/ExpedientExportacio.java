
package net.conselldemallorca.helium.webapp.exportacio;

import java.util.Date;

public class ExpedientExportacio{

	private Long id;
	private Long entornId;
	private Long expedientTipusId;
	private String processInstanceId;
	private String numero;
	private String numeroDefault;
	private String titol;
	private Date dataInici;
	private Date dataFi;
	private ExpedientEstatTipusEnum estatTipus;
	private Long estatId;
	private boolean aturat;
	private String infoAturat;
	private boolean anulat;
	private String comentariAnulat;
	private Long alertesTotals;
	private Long alertesPendents;
	private boolean ambErrors;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
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
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
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
	public ExpedientEstatTipusEnum getEstatTipus() {
		return estatTipus;
	}
	public void setEstatTipus(ExpedientEstatTipusEnum estatTipus) {
		this.estatTipus = estatTipus;
	}
	public Long getEstatId() {
		return estatId;
	}
	public void setEstatId(Long estatId) {
		this.estatId = estatId;
	}
	public boolean isAturat() {
		return aturat;
	}
	public void setAturat(boolean aturat) {
		this.aturat = aturat;
	}
	public String getInfoAturat() {
		return infoAturat;
	}
	public void setInfoAturat(String infoAturat) {
		this.infoAturat = infoAturat;
	}
	public boolean isAnulat() {
		return anulat;
	}
	public void setAnulat(boolean anulat) {
		this.anulat = anulat;
	}
	public String getComentariAnulat() {
		return comentariAnulat;
	}
	public void setComentariAnulat(String comentariAnulat) {
		this.comentariAnulat = comentariAnulat;
	}
	public Long getAlertesTotals() {
		return alertesTotals;
	}
	public void setAlertesTotals(Long alertesTotals) {
		this.alertesTotals = alertesTotals;
	}
	public Long getAlertesPendents() {
		return alertesPendents;
	}
	public void setAlertesPendents(Long alertesPendents) {
		this.alertesPendents = alertesPendents;
	}
	public boolean isAmbErrors() {
		return ambErrors;
	}
	public void setAmbErrors(boolean ambErrors) {
		this.ambErrors = ambErrors;
	}
}
