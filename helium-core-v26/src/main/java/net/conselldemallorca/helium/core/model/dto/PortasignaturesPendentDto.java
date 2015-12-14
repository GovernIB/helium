/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.Date;



/**
 * DTO amb informació d'un document pendent al portasignatures
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesPendentDto {

	private Long id;
	private Integer portasignaturesId;
	private DocumentDto document;
	private ExpedientDto expedient;
	private String estat;
	private Date dataEnviat;
	private Date dataCallbackPrimer;
	private Date dataCallbackDarrer;
	private String motiuRebuig;
	private Date dataProcessamentPrimer;
	private Date dataProcessamentDarrer;
	private Date dataSignatRebutjat;
	private Date dataCustodiaIntent;
	private Date dataCustodiaOk;
	private Date dataSignalIntent;
	private Date dataSignalOk;
	private String errorCallback;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getPortasignaturesId() {
		return portasignaturesId;
	}
	public void setPortasignaturesId(Integer portasignaturesId) {
		this.portasignaturesId = portasignaturesId;
	}
	public DocumentDto getDocument() {
		return document;
	}
	public void setDocument(DocumentDto document) {
		this.document = document;
	}
	public ExpedientDto getExpedient() {
		return expedient;
	}
	public void setExpedient(ExpedientDto expedient) {
		this.expedient = expedient;
	}
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public Date getDataEnviat() {
		return dataEnviat;
	}
	public void setDataEnviat(Date dataEnviat) {
		this.dataEnviat = dataEnviat;
	}
	public Date getDataCallbackPrimer() {
		return dataCallbackPrimer;
	}
	public void setDataCallbackPrimer(Date dataCallbackPrimer) {
		this.dataCallbackPrimer = dataCallbackPrimer;
	}
	public Date getDataCallbackDarrer() {
		return dataCallbackDarrer;
	}
	public void setDataCallbackDarrer(Date dataCallbackDarrer) {
		this.dataCallbackDarrer = dataCallbackDarrer;
	}
	public String getMotiuRebuig() {
		return motiuRebuig;
	}
	public void setMotiuRebuig(String motiuRebuig) {
		this.motiuRebuig = motiuRebuig;
	}
	public Date getDataProcessamentPrimer() {
		return dataProcessamentPrimer;
	}
	public void setDataProcessamentPrimer(Date dataProcessamentPrimer) {
		this.dataProcessamentPrimer = dataProcessamentPrimer;
	}
	public Date getDataProcessamentDarrer() {
		return dataProcessamentDarrer;
	}
	public void setDataProcessamentDarrer(Date dataProcessamentDarrer) {
		this.dataProcessamentDarrer = dataProcessamentDarrer;
	}
	public Date getDataSignatRebutjat() {
		return dataSignatRebutjat;
	}
	public void setDataSignatRebutjat(Date dataSignatRebutjat) {
		this.dataSignatRebutjat = dataSignatRebutjat;
	}
	public Date getDataCustodiaIntent() {
		return dataCustodiaIntent;
	}
	public void setDataCustodiaIntent(Date dataCustodiaIntent) {
		this.dataCustodiaIntent = dataCustodiaIntent;
	}
	public Date getDataCustodiaOk() {
		return dataCustodiaOk;
	}
	public void setDataCustodiaOk(Date dataCustodiaOk) {
		this.dataCustodiaOk = dataCustodiaOk;
	}
	public Date getDataSignalIntent() {
		return dataSignalIntent;
	}
	public void setDataSignalIntent(Date dataSignalIntent) {
		this.dataSignalIntent = dataSignalIntent;
	}
	public Date getDataSignalOk() {
		return dataSignalOk;
	}
	public void setDataSignalOk(Date dataSignalOk) {
		this.dataSignalOk = dataSignalOk;
	}
	public String getErrorCallback() {
		return errorCallback;
	}
	public void setErrorCallback(String errorCallback) {
		this.errorCallback = errorCallback;
	}

}
