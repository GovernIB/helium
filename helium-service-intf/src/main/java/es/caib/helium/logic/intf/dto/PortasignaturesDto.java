/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.Date;




/**
 * DTO amb informació d'un document enviat al portasignatures
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesDto {
	
	private enum TipusEstat {
		BLOQUEJAT,
		PENDENT,	// El document s'ha enviat però encara no s'ha rebut al callback cap resposta
		SIGNAT,		// S'ha rebut petició al callback indicant que el document ha estat signat
		REBUTJAT,	// S'ha rebut petició al callback indicant que el document ha estat rebujat
		PROCESSAT,	// El document signat o rebujat s'ha processat correctament
		CANCELAT,	// El document s'ha esborrat de l'expedient
		ERROR,		// El document s'ha intentat processar i ha produit un error
		ESBORRAT	// S'ha esborrat l'expedient al qual pertany el document
	}
	

	private Long id;
	private Integer documentId;
	private Long tokenId;
	private Date dataEnviat;
	private String estat;
	private String transicio;
	private Long documentStoreId;
	private String motiuRebuig;
	private String transicioOK;
	private String transicioKO;
	private Date dataProcessamentPrimer;
	private Date dataProcessamentDarrer;
	private Date dataSignatRebutjat;
	private Date dataCustodiaIntent;
	private Date dataCustodiaOk;
	private Date dataSignalIntent;
	private Date dataSignalOk;
	private String processInstanceId;
	private boolean error;
	private String errorProcessant;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}
	public Long getTokenId() {
		return tokenId;
	}
	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}
	public Date getDataEnviat() {
		return dataEnviat;
	}
	public void setDataEnviat(Date dataEnviat) {
		this.dataEnviat = dataEnviat;
	}
	public String getEstat() {
		return estat;
	}
	public void setEstat(String estat) {
		this.estat = estat;
	}
	public String getTransicio() {
		return transicio;
	}
	public void setTransicio(String transicio) {
		this.transicio = transicio;
	}
	public Long getDocumentStoreId() {
		return documentStoreId;
	}
	public void setDocumentStoreId(Long documentStoreId) {
		this.documentStoreId = documentStoreId;
	}
	public String getMotiuRebuig() {
		return motiuRebuig;
	}
	public void setMotiuRebuig(String motiuRebuig) {
		this.motiuRebuig = motiuRebuig;
	}
	public String getTransicioOK() {
		return transicioOK;
	}
	public void setTransicioOK(String transicioOK) {
		this.transicioOK = transicioOK;
	}
	public String getTransicioKO() {
		return transicioKO;
	}
	public void setTransicioKO(String transicioKO) {
		this.transicioKO = transicioKO;
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
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorProcessant() {
		return errorProcessant;
	}
	public void setErrorProcessant(String errorProcessant) {
		this.errorProcessant = errorProcessant;
	}

	public boolean isPendent() {
		return	TipusEstat.PENDENT.equals(estat) ||
				TipusEstat.SIGNAT.equals(estat) ||
				TipusEstat.REBUTJAT.equals(estat) ||
				TipusEstat.ERROR.equals(estat);
	}

}
