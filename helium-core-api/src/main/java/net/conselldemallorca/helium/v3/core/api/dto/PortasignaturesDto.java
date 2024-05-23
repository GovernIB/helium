package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO amb informació d'un document enviat al portasignatures
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortasignaturesDto implements Serializable {

	private static final long serialVersionUID = 2893548848804888891L;
	
	private Long id;
	private Integer documentId;
	private String documentNom;
	private String documentUUID;
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

	private Long expedientId;
	private String expedientNumero;
	private String expedientTitol;
	private Long entornId;
	private String entornCodi;
	private String entornNom;
	private Long tipusExpedientId;
	private String tipusExpedientCodi;
	private String tipusExpedientNom;
	
	/** En la conversió les peticions processades per la transició de rebutjades es marcaven com a 
	 * error en la llista de pendents. S'informa aquest flag per poder tornar a enviar-les des de la gestió de documents.
	 */
	private boolean rebutjadaProcessada = false;
	private boolean reintentarFirma = false;
	private boolean firmaEnProces = false;

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
	public boolean isFirmaEnProces() {
		return	this.estat!=null && ("PENDENT".equals(this.estat) || "REBUTJAT".equals(this.estat));
	}
	public void setFirmaEnProces(boolean firmaEnProces) {
		this.firmaEnProces = firmaEnProces;
	}
	public boolean isReintentarFirma() {
		return	this.rebutjadaProcessada || (this.estat!=null && ("REBUTJAT".equals(this.estat) || "CANCELAT".equals(this.estat)));
	}
	public void setReintentarFirma(boolean reintentarFirma) {
		this.reintentarFirma = reintentarFirma;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}
	public String getExpedientTitol() {
		return expedientTitol;
	}
	public void setExpedientTitol(String expedientTitol) {
		this.expedientTitol = expedientTitol;
	}
	public Long getEntornId() {
		return entornId;
	}
	public void setEntornId(Long entornId) {
		this.entornId = entornId;
	}
	public String getEntornNom() {
		return entornNom;
	}
	public void setEntornNom(String entornNom) {
		this.entornNom = entornNom;
	}
	public Long getTipusExpedientId() {
		return tipusExpedientId;
	}
	public void setTipusExpedientId(Long tipusExpedientId) {
		this.tipusExpedientId = tipusExpedientId;
	}
	public String getTipusExpedientNom() {
		return tipusExpedientNom;
	}
	public void setTipusExpedientNom(String tipusExpedientNom) {
		this.tipusExpedientNom = tipusExpedientNom;
	}
	public String getDocumentNom() {
		return documentNom;
	}
	public void setDocumentNom(String documentNom) {
		this.documentNom = documentNom;
	}
	public String getEntornCodi() {
		return entornCodi;
	}
	public void setEntornCodi(String entornCodi) {
		this.entornCodi = entornCodi;
	}
	public String getTipusExpedientCodi() {
		return tipusExpedientCodi;
	}
	public void setTipusExpedientCodi(String tipusExpedientCodi) {
		this.tipusExpedientCodi = tipusExpedientCodi;
	}
	public boolean isPendent() {
		return	PortafirmesEstatEnum.PENDENT.equals(estat) ||
				PortafirmesEstatEnum.SIGNAT.equals(estat) ||
				PortafirmesEstatEnum.REBUTJAT.equals(estat) ||
				PortafirmesEstatEnum.ERROR.equals(estat);
	}
	public boolean isRebutjadaProcessada() {
		return rebutjadaProcessada;
	}
	public void setRebutjadaProcessada(boolean rebutjadaProcessada) {
		this.rebutjadaProcessada = rebutjadaProcessada;
	}
	public String getExpedientIdentificador() {
		return "[" + expedientNumero + "] " + expedientTitol;
	}
	public String getDocumentUUID() {
		return documentUUID;
	}
	public void setDocumentUUID(String documentUUID) {
		this.documentUUID = documentUUID;
	}
}