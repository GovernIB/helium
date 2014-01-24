/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.Date;

import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;



/**
 * DTO amb informació d'un document enviat al portasignatures
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesDto {

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
	private Date dataProcesPrimer;
	private Date dataProcesDarrer;
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
	public Date getDataProcesPrimer() {
		return dataProcesPrimer;
	}
	public void setDataProcesPrimer(Date dataProcesPrimer) {
		this.dataProcesPrimer = dataProcesPrimer;
	}
	public Date getDataProcesDarrer() {
		return dataProcesDarrer;
	}
	public void setDataProcesDarrer(Date dataProcesDarrer) {
		this.dataProcesDarrer = dataProcesDarrer;
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
		return	Portasignatures.TipusEstat.PENDENT.toString().equals(estat) ||
				Portasignatures.TipusEstat.SIGNAT.toString().equals(estat) ||
				Portasignatures.TipusEstat.REBUTJAT.toString().equals(estat) ||
				Portasignatures.TipusEstat.ERROR.toString().equals(estat);
	}

}
