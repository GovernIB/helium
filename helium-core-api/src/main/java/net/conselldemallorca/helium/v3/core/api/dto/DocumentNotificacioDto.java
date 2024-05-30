/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;
import java.util.List;


/**
 * DTO amb informació d'un document d'una tasca de la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentNotificacioDto {

	private Long id;
	private String usuariCodi;
	private ExpedientDto expedient;
	private DocumentStoreDto document;
	private List<DocumentStoreDto> annexos;
	private String emisorDir3Codi;
	private EnviamentTipusEnumDto tipus;
	private Date dataProgramada;
	private Integer retard;
	private Date dataCaducitat;
	private String titularNom;
	private String titularLlinatge1;
	private String titularLlinatge2;
	private String titularNif;
	private String titularTelefon;
	private String titularEmail;
	private String destinatariNom;
	private String destinatariLlinatge1;
	private String destinatariLlinatge2;
	private String destinatariNif;
	private String destinatariTelefon;
	private String destinatariEmail;
	private String seuIdioma;
	private String seuAvisTitol;
	private String seuAvisText;
	private String seuAvisTextMobil;
	private String seuOficiTitol;
	private String seuOficiText;
	private String enviamentIdentificador;
	private String enviamentReferencia;
	private NotificacioEnviamentEstatEnumDto enviamentDatatEstat;
	private Date enviamentDatatData;
	private String enviamentDatatOrigen;
	private Date enviamentCertificacioData;
	private String enviamentCertificacioOrigen;
	private DocumentStoreDto enviamentCertificacio;
	private NotificacioEstatEnumDto estat;
	private Date enviatData;
	protected Date processatData;
	private Date cancelatData;
	private boolean error;
	private String errorDescripcio;
	private Integer numIntents;
	private Date intentData;
	private Date intentProximData;
	private String concepte;
	private String descripcio;
	private String processInstanceId;
	private Long documentStoreId;
	private String documentNom;
	private String arxiuNom;
	private String arxiuExtensio;
	private Date dataInicial;
	private Date dataFinal;
	private Date dataCreacio;
	private Date dataModificacio;
	private Date dataDocument;
	private Long expedientId;
	private String expedientNumero;
	private String unitatOrganitzativaCodi;
	private String unitatOrganitzativaDescripcio;
	private Date dataEnviament;
	private String entornCodi;
	private String entornNom;
	private ExpedientTipusDto expedientTipus;
	private String expedientTipusCodi;
	private String expedientTipusNom;
	private Long expedientTipusId;
	private String interessat;
	private String nomDocument;
	private Long justificantId;
	private String justificantArxiuNom;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
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
	public List<DocumentStoreDto> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<DocumentStoreDto> annexos) {
		this.annexos = annexos;
	}
	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	
	public EnviamentTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(EnviamentTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public Date getDataProgramada() {
		return dataProgramada;
	}
	public void setDataProgramada(Date dataProgramada) {
		this.dataProgramada = dataProgramada;
	}
	public Integer getRetard() {
		return retard;
	}
	public void setRetard(Integer retard) {
		this.retard = retard;
	}
	public Date getDataCaducitat() {
		return dataCaducitat;
	}
	public void setDataCaducitat(Date dataCaducitat) {
		this.dataCaducitat = dataCaducitat;
	}
	public String getTitularNom() {
		return titularNom;
	}
	public void setTitularNom(String titularNom) {
		this.titularNom = titularNom;
	}
	public String getTitularLlinatge1() {
		return titularLlinatge1;
	}
	public void setTitularLlinatge1(String titularLlinatge1) {
		this.titularLlinatge1 = titularLlinatge1;
	}
	public String getTitularLlinatge2() {
		return titularLlinatge2;
	}
	public void setTitularLlinatge2(String titularLlinatge2) {
		this.titularLlinatge2 = titularLlinatge2;
	}
	public String getTitularNif() {
		return titularNif;
	}
	public void setTitularNif(String titularNif) {
		this.titularNif = titularNif;
	}
	public String getTitularTelefon() {
		return titularTelefon;
	}
	public void setTitularTelefon(String titularTelefon) {
		this.titularTelefon = titularTelefon;
	}
	public String getTitularEmail() {
		return titularEmail;
	}
	public void setTitularEmail(String titularEmail) {
		this.titularEmail = titularEmail;
	}
	public String getDestinatariNom() {
		return destinatariNom;
	}
	public void setDestinatariNom(String destinatariNom) {
		this.destinatariNom = destinatariNom;
	}
	public String getDestinatariLlinatge1() {
		return destinatariLlinatge1;
	}
	public void setDestinatariLlinatge1(String destinatariLlinatge1) {
		this.destinatariLlinatge1 = destinatariLlinatge1;
	}
	public String getDestinatariLlinatge2() {
		return destinatariLlinatge2;
	}
	public void setDestinatariLlinatge2(String destinatariLlinatge2) {
		this.destinatariLlinatge2 = destinatariLlinatge2;
	}
	public String getDestinatariNif() {
		return destinatariNif;
	}
	public void setDestinatariNif(String destinatariNif) {
		this.destinatariNif = destinatariNif;
	}
	public String getDestinatariTelefon() {
		return destinatariTelefon;
	}
	public void setDestinatariTelefon(String destinatariTelefon) {
		this.destinatariTelefon = destinatariTelefon;
	}
	public String getDestinatariEmail() {
		return destinatariEmail;
	}
	public void setDestinatariEmail(String destinatariEmail) {
		this.destinatariEmail = destinatariEmail;
	}
	public String getSeuIdioma() {
		return seuIdioma;
	}
	public void setSeuIdioma(String seuIdioma) {
		this.seuIdioma = seuIdioma;
	}
	public String getSeuAvisTitol() {
		return seuAvisTitol;
	}
	public void setSeuAvisTitol(String seuAvisTitol) {
		this.seuAvisTitol = seuAvisTitol;
	}
	public String getSeuAvisText() {
		return seuAvisText;
	}
	public void setSeuAvisText(String seuAvisText) {
		this.seuAvisText = seuAvisText;
	}
	public String getSeuAvisTextMobil() {
		return seuAvisTextMobil;
	}
	public void setSeuAvisTextMobil(String seuAvisTextMobil) {
		this.seuAvisTextMobil = seuAvisTextMobil;
	}
	public String getSeuOficiTitol() {
		return seuOficiTitol;
	}
	public void setSeuOficiTitol(String seuOficiTitol) {
		this.seuOficiTitol = seuOficiTitol;
	}
	public String getSeuOficiText() {
		return seuOficiText;
	}
	public void setSeuOficiText(String seuOficiText) {
		this.seuOficiText = seuOficiText;
	}
	public String getEnviamentIdentificador() {
		return enviamentIdentificador;
	}
	public void setEnviamentIdentificador(String enviamentIdentificador) {
		this.enviamentIdentificador = enviamentIdentificador;
	}
	public String getEnviamentReferencia() {
		return enviamentReferencia;
	}
	public void setEnviamentReferencia(String enviamentReferencia) {
		this.enviamentReferencia = enviamentReferencia;
	}
	public NotificacioEnviamentEstatEnumDto getEnviamentDatatEstat() {
		return enviamentDatatEstat;
	}
	public void setEnviamentDatatEstat(NotificacioEnviamentEstatEnumDto enviamentDatatEstat) {
		this.enviamentDatatEstat = enviamentDatatEstat;
	}
	public Date getEnviamentDatatData() {
		return enviamentDatatData;
	}
	public void setEnviamentDatatData(Date enviamentDatatData) {
		this.enviamentDatatData = enviamentDatatData;
	}
	public String getEnviamentDatatOrigen() {
		return enviamentDatatOrigen;
	}
	public void setEnviamentDatatOrigen(String enviamentDatatOrigen) {
		this.enviamentDatatOrigen = enviamentDatatOrigen;
	}
	public Date getEnviamentCertificacioData() {
		return enviamentCertificacioData;
	}
	public void setEnviamentCertificacioData(Date enviamentCertificacioData) {
		this.enviamentCertificacioData = enviamentCertificacioData;
	}
	public String getEnviamentCertificacioOrigen() {
		return enviamentCertificacioOrigen;
	}
	public void setEnviamentCertificacioOrigen(String enviamentCertificacioOrigen) {
		this.enviamentCertificacioOrigen = enviamentCertificacioOrigen;
	}
	public DocumentStoreDto getEnviamentCertificacio() {
		return enviamentCertificacio;
	}
	public void setEnviamentCertificacio(DocumentStoreDto enviamentCertificacio) {
		this.enviamentCertificacio = enviamentCertificacio;
	}
	public NotificacioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioEstatEnumDto estat) {
		this.estat = estat;
	}
	public Date getEnviatData() {
		return enviatData;
	}
	public void setEnviatData(Date enviatData) {
		this.enviatData = enviatData;
	}
	public Date getProcessatData() {
		return processatData;
	}
	public void setProcessatData(Date processatData) {
		this.processatData = processatData;
	}
	public Date getCancelatData() {
		return cancelatData;
	}
	public void setCancelatData(Date cancelatData) {
		this.cancelatData = cancelatData;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public Integer getNumIntents() {
		return numIntents;
	}
	public void setNumIntents(Integer numIntents) {
		this.numIntents = numIntents;
	}
	public Date getIntentData() {
		return intentData;
	}
	public void setIntentData(Date intentData) {
		this.intentData = intentData;
	}
	public Date getIntentProximData() {
		return intentProximData;
	}
	public void setIntentProximData(Date intentProximData) {
		this.intentProximData = intentProximData;
	}
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getDocumentNom() {
		return documentNom;
	}
	public void setDocumentNom(String documentNom) {
		this.documentNom = documentNom;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public String getArxiuExtensio() {
		return arxiuExtensio;
	}
	public void setArxiuExtensio(String arxiuExtensio) {
		this.arxiuExtensio = arxiuExtensio;
	}
	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	public Date getDataModificacio() {
		return dataModificacio;
	}
	public void setDataModificacio(Date dataModificacio) {
		this.dataModificacio = dataModificacio;
	}
	public Date getDataDocument() {
		return dataDocument;
	}
	public void setDataDocument(Date dataDocument) {
		this.dataDocument = dataDocument;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getUnitatOrganitzativaCodi() {
		return unitatOrganitzativaCodi;
	}
	public void setUnitatOrganitzativaCodi(String unitatOrganitzativaCodi) {
		this.unitatOrganitzativaCodi = unitatOrganitzativaCodi;
	}
	public String getUnitatOrganitzativaDescripcio() {
		return unitatOrganitzativaDescripcio;
	}
	public void setUnitatOrganitzativaDescripcio(String unitatOrganitzativaDescripcio) {
		this.unitatOrganitzativaDescripcio = unitatOrganitzativaDescripcio;
	}
	public Date getDataEnviament() {
		return dataEnviament;
	}
	public void setDataEnviament(Date dataEnviament) {
		this.dataEnviament = dataEnviament;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}
	public String getInteressat() {
		return interessat;
	}
	public void setInteressat(String interessat) {
		this.interessat = interessat;
	}
	public String getNomDocument() {
		return nomDocument;
	}
	public void setNomDocument(String nomDocument) {
		this.nomDocument = nomDocument;
	}

	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
		this.expedientTipus = expedientTipus;
	}	
	
	public Long getDocumentStoreId() {
		return documentStoreId;
	}
	public void setDocumentStoreId(Long documentStoreId) {
		this.documentStoreId = documentStoreId;
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
	public String getEntornNom() {
		return entornNom;
	}
	public void setEntornNom(String entornNom) {
		this.entornNom = entornNom;
	}
	public String getExpedientTipusNom() {
		return expedientTipusNom;
	}
	public void setExpedientTipusNom(String expedientTipusNom) {
		this.expedientTipusNom = expedientTipusNom;
	}
	
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}
	
	public Long getJustificantId() {
		return justificantId;
	}
	public void setJustificantId(Long justificantId) {
		this.justificantId = justificantId;
	}
	public String getJustificantArxiuNom() {
		return justificantArxiuNom;
	}
	public void setJustificantArxiuNom(String justificantArxiuNom) {
		this.justificantArxiuNom = justificantArxiuNom;
	}
	
	public String getDestinatariFullNomNif() {
		return 	(this.getDestinatariNom() !=null ? this.getDestinatariNom() + " "  : "")+ 
				(this.getDestinatariLlinatge1() !=null ? this.getDestinatariLlinatge1() + " " :  "")+
				(this.getDestinatariLlinatge2() !=null ? this.getDestinatariLlinatge2() : "") +
				(this.getDestinatariNif()!=null ?  (" - " + this.getDestinatariNif()) : "");
	}
	
	public String getInteressatFullNomNif() {
		return 	(this.getTitularNom()!=null ? this.getTitularNom() + " " : "")+ 
				(this.getTitularLlinatge1() != null ? this.getTitularLlinatge1() + " " : "")+
				(this.getTitularLlinatge2() !=null ? this.getTitularLlinatge2() : "") +
				(this.getTitularNif()!=null ?  (" - " + this.getTitularNif()) : "");
	}
	
	public String getOrganEmissorCodiAndNom() {
		return this.unitatOrganitzativaCodi + " - " + this.unitatOrganitzativaDescripcio + "";
	}
	
}
