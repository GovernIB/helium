package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Date;

public class PeticioPinbalDto implements Serializable {

	private static final long serialVersionUID = -1115466212214125984L;
	
	private Long id;
	private EntornDto entorn;
	private ExpedientTipusDto tipus;
	private ExpedientDto expedient;
	private DocumentStoreDto document;
	private String procediment;
	private String usuari;
	private Date dataPeticio;
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
	public EntornDto getEntorn() {
		return entorn;
	}
	public void setEntorn(EntornDto entorn) {
		this.entorn = entorn;
	}
	public ExpedientTipusDto getTipus() {
		return tipus;
	}
	public void setTipus(ExpedientTipusDto tipus) {
		this.tipus = tipus;
	}
	public ExpedientDto getExpedient() {
		return expedient;
	}
	public void setExpedient(ExpedientDto expedient) {
		this.expedient = expedient;
	}
	public DocumentStoreDto getDocument() {
		return document;
	}
	public void setDocument(DocumentStoreDto document) {
		this.document = document;
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
	public Date getDataPeticio() {
		return dataPeticio;
	}
	public void setDataPeticio(Date dataPeticio) {
		this.dataPeticio = dataPeticio;
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
