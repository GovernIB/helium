package net.conselldemallorca.helium.webapp.v3.command;

import java.io.Serializable;
import java.util.Date;

import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;

public class ConsultesPinbalFiltreCommand implements Serializable {
	
	private static final long serialVersionUID = -6121905141410062719L;
	
	private Long id;
	private Long entornId;
	private Long tipusId;
	private Long expedientId;
	private String numeroExpedient;
	private Long documentId;
	private String procediment;
	private String usuari;
	private Date dataPeticioIni;
	private Date dataPeticioFi;
	private boolean asincrona=false;
	private PeticioPinbalEstatEnum estat;
	private String errorMsg;
	private String pinbalId;
	private Date dataPrevista;
	private Date dataDarreraConsulta;
	private String transicioOK;
	private String transicioKO;
	private Date dataProcessamentPrimer;
	private Date dataProcessamentDarrer;
	private String errorProcessament;
	
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
	public Long getTipusId() {
		return tipusId;
	}
	public void setTipusId(Long tipusId) {
		this.tipusId = tipusId;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}	
	public String getNumeroExpedient() {
		return numeroExpedient;
	}
	public void setNumeroExpedient(String numeroExpedient) {
		this.numeroExpedient = numeroExpedient;
	}
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	public String getProcediment() {
		return procediment;
	}
	public void setProcediment(String procediment) {
		this.procediment = procediment;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public Date getDataPeticioIni() {
		return dataPeticioIni;
	}
	public void setDataPeticioIni(Date dataPeticioIni) {
		this.dataPeticioIni = dataPeticioIni;
	}
	public Date getDataPeticioFi() {
		return dataPeticioFi;
	}
	public void setDataPeticioFi(Date dataPeticioFi) {
		this.dataPeticioFi = dataPeticioFi;
	}
	public boolean isAsincrona() {
		return asincrona;
	}
	public void setAsincrona(boolean asincrona) {
		this.asincrona = asincrona;
	}
	public PeticioPinbalEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(PeticioPinbalEstatEnum estat) {
		this.estat = estat;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getPinbalId() {
		return pinbalId;
	}
	public void setPinbalId(String pinbalId) {
		this.pinbalId = pinbalId;
	}
	public Date getDataPrevista() {
		return dataPrevista;
	}
	public void setDataPrevista(Date dataPrevista) {
		this.dataPrevista = dataPrevista;
	}
	public Date getDataDarreraConsulta() {
		return dataDarreraConsulta;
	}
	public void setDataDarreraConsulta(Date dataDarreraConsulta) {
		this.dataDarreraConsulta = dataDarreraConsulta;
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
	public String getErrorProcessament() {
		return errorProcessament;
	}
	public void setErrorProcessament(String errorProcessament) {
		this.errorProcessament = errorProcessament;
	}
	
	
}
