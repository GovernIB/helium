/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * DTO amb informació d'una notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioDto implements Serializable {
	
	
	private Long id;
	private ExpedientDto expedient;
	private DocumentEnviamentEstatEnumDto estat;
	private String assumpte;
	private Date dataEnviament;
	private String observacions;
	
	private DocumentNotificacioTipusEnumDto tipus;
	private Date dataRecepcio;
	private String registreNumero;
	private InteressatDocumentTipusEnumDto interessatDocumentTipus;
	private String interessatDocumentNum;
	private String interessatNom;
	private String interessatLlinatge1;
	private String interessatLlinatge2;
	private String interessatPaisCodi;
	private String interessatProvinciaCodi;
	private String interessatMunicipiCodi;
	private String interessatEmail;
	private boolean interessatRepresentant;
	private String unitatAdministrativa;
	private String organCodi;
	private String oficinaCodi;
	private String avisTitol;
	private String avisText;
	private String avisTextSms;
	private String oficiTitol;
	private String oficiText;
	private InteressatIdiomaEnumDto idioma;
	private Date enviamentData;
	private Integer enviamentCount;
	private boolean enviamentError;
	private String enviamentErrorDescripcio;
	private Date processamentData;
	private Integer processamentCount;
	private boolean processamentError;
	private String processamentErrorDescripcio;
	private Long rdsCodi;
	private String rdsClau;
	private DocumentNotificacioDto document;
	private List<DocumentNotificacioDto> annexos = new ArrayList<DocumentNotificacioDto>();
	private String error;
	
	private static final long serialVersionUID = 1715501096089688125L;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ExpedientDto getExpedient() {
		return expedient;
	}

	public void setExpedient(ExpedientDto expedient) {
		this.expedient = expedient;
	}

	public DocumentEnviamentEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(DocumentEnviamentEstatEnumDto estat) {
		this.estat = estat;
	}

	public String getAssumpte() {
		return assumpte;
	}

	public void setAssumpte(String assumpte) {
		this.assumpte = assumpte;
	}

	public Date getDataEnviament() {
		return dataEnviament;
	}

	public void setDataEnviament(Date dataEnviament) {
		this.dataEnviament = dataEnviament;
	}

	public String getObservacions() {
		return observacions;
	}

	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}

	public DocumentNotificacioTipusEnumDto getTipus() {
		return tipus;
	}

	public void setTipus(DocumentNotificacioTipusEnumDto tipus) {
		this.tipus = tipus;
	}

	public Date getDataRecepcio() {
		return dataRecepcio;
	}

	public void setDataRecepcio(Date dataRecepcio) {
		this.dataRecepcio = dataRecepcio;
	}

	public String getRegistreNumero() {
		return registreNumero;
	}

	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}

	public InteressatDocumentTipusEnumDto getInteressatDocumentTipus() {
		return interessatDocumentTipus;
	}

	public void setInteressatDocumentTipus(InteressatDocumentTipusEnumDto interessatDocumentTipus) {
		this.interessatDocumentTipus = interessatDocumentTipus;
	}

	public String getInteressatDocumentNum() {
		return interessatDocumentNum;
	}

	public void setInteressatDocumentNum(String interessatDocumentNum) {
		this.interessatDocumentNum = interessatDocumentNum;
	}

	public String getInteressatNom() {
		return interessatNom;
	}

	public void setInteressatNom(String interessatNom) {
		this.interessatNom = interessatNom;
	}

	public String getInteressatLlinatge1() {
		return interessatLlinatge1;
	}

	public void setInteressatLlinatge1(String interessatLlinatge1) {
		this.interessatLlinatge1 = interessatLlinatge1;
	}

	public String getInteressatLlinatge2() {
		return interessatLlinatge2;
	}

	public void setInteressatLlinatge2(String interessatLlinatge2) {
		this.interessatLlinatge2 = interessatLlinatge2;
	}

	public String getInteressatPaisCodi() {
		return interessatPaisCodi;
	}

	public void setInteressatPaisCodi(String interessatPaisCodi) {
		this.interessatPaisCodi = interessatPaisCodi;
	}

	public String getInteressatProvinciaCodi() {
		return interessatProvinciaCodi;
	}

	public void setInteressatProvinciaCodi(String interessatProvinciaCodi) {
		this.interessatProvinciaCodi = interessatProvinciaCodi;
	}

	public String getInteressatMunicipiCodi() {
		return interessatMunicipiCodi;
	}

	public void setInteressatMunicipiCodi(String interessatMunicipiCodi) {
		this.interessatMunicipiCodi = interessatMunicipiCodi;
	}

	public String getInteressatEmail() {
		return interessatEmail;
	}

	public void setInteressatEmail(String interessatEmail) {
		this.interessatEmail = interessatEmail;
	}

	public boolean isInteressatRepresentant() {
		return interessatRepresentant;
	}

	public void setInteressatRepresentant(boolean interessatRepresentant) {
		this.interessatRepresentant = interessatRepresentant;
	}

	public String getUnitatAdministrativa() {
		return unitatAdministrativa;
	}

	public void setUnitatAdministrativa(String unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}

	public String getOrganCodi() {
		return organCodi;
	}

	public void setOrganCodi(String organCodi) {
		this.organCodi = organCodi;
	}

	public String getOficinaCodi() {
		return oficinaCodi;
	}

	public void setOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}

	public String getAvisTitol() {
		return avisTitol;
	}

	public void setAvisTitol(String avisTitol) {
		this.avisTitol = avisTitol;
	}

	public String getAvisText() {
		return avisText;
	}

	public void setAvisText(String avisText) {
		this.avisText = avisText;
	}

	public String getAvisTextSms() {
		return avisTextSms;
	}

	public void setAvisTextSms(String avisTextSms) {
		this.avisTextSms = avisTextSms;
	}

	public String getOficiTitol() {
		return oficiTitol;
	}

	public void setOficiTitol(String oficiTitol) {
		this.oficiTitol = oficiTitol;
	}

	public String getOficiText() {
		return oficiText;
	}

	public void setOficiText(String oficiText) {
		this.oficiText = oficiText;
	}

	public InteressatIdiomaEnumDto getIdioma() {
		return idioma;
	}

	public void setIdioma(InteressatIdiomaEnumDto idioma) {
		this.idioma = idioma;
	}

	public Date getEnviamentData() {
		return enviamentData;
	}

	public void setEnviamentData(Date enviamentData) {
		this.enviamentData = enviamentData;
	}

	public Integer getEnviamentCount() {
		return enviamentCount;
	}

	public void setEnviamentCount(Integer enviamentCount) {
		this.enviamentCount = enviamentCount;
	}

	public boolean isEnviamentError() {
		return enviamentError;
	}

	public void setEnviamentError(boolean enviamentError) {
		this.enviamentError = enviamentError;
	}

	public String getEnviamentErrorDescripcio() {
		return enviamentErrorDescripcio;
	}

	public void setEnviamentErrorDescripcio(String enviamentErrorDescripcio) {
		this.enviamentErrorDescripcio = enviamentErrorDescripcio;
	}

	public Date getProcessamentData() {
		return processamentData;
	}

	public void setProcessamentData(Date processamentData) {
		this.processamentData = processamentData;
	}

	public Integer getProcessamentCount() {
		return processamentCount;
	}

	public void setProcessamentCount(Integer processamentCount) {
		this.processamentCount = processamentCount;
	}

	public boolean isProcessamentError() {
		return processamentError;
	}

	public void setProcessamentError(boolean processamentError) {
		this.processamentError = processamentError;
	}

	public String getProcessamentErrorDescripcio() {
		return processamentErrorDescripcio;
	}

	public void setProcessamentErrorDescripcio(String processamentErrorDescripcio) {
		this.processamentErrorDescripcio = processamentErrorDescripcio;
	}

	public Long getRdsCodi() {
		return rdsCodi;
	}

	public void setRdsCodi(Long rdsCodi) {
		this.rdsCodi = rdsCodi;
	}

	public String getRdsClau() {
		return rdsClau;
	}

	public void setRdsClau(String rdsClau) {
		this.rdsClau = rdsClau;
	}

	public DocumentNotificacioDto getDocument() {
		return document;
	}

	public void setDocument(DocumentNotificacioDto document) {
		this.document = document;
	}

	public List<DocumentNotificacioDto> getAnnexos() {
		return annexos;
	}

	public void setAnnexos(List<DocumentNotificacioDto> annexos) {
		this.annexos = annexos;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
