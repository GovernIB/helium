/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.ForeignKey;

/**
 * Objecte de domini que representa una notificació electronica de un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_expedient_notificacio")
public class Nofiticacio implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_notificacio")
	@TableGenerator(name="gen_notificacio", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "hel_expedient_notif_fk")
	protected Expedient expedient;
//	@Column(name = "estat", nullable = false)
//	@Enumerated(EnumType.STRING)
//	protected DocumentEnviamentEstatEnumDto estat;
//	@Column(name = "assumpte", length = 256, nullable = false)
//	protected String assumpte;
//	@Column(name = "data_enviament", nullable = false)
//	@Temporal(TemporalType.TIMESTAMP)
//	protected Date dataEnviament;
//	@Column(name = "observacions", length = 256)
//	protected String observacions;
//	@ManyToOne(optional = false, fetch = FetchType.EAGER)
//	@JoinColumn(name = "document_store_id")
//	@ForeignKey(name = "hel_document_notif_fk")
//	protected DocumentStore document;
//	@ManyToMany(
//			cascade = CascadeType.ALL,
//			fetch = FetchType.LAZY)
//	@JoinTable(
//			name = "hel_document_notif_doc",
//			joinColumns = {@JoinColumn(name = "document_notif_id")},
//			inverseJoinColumns = {@JoinColumn(name = "document_id")})
//	protected List<DocumentStore> annexos = new ArrayList<DocumentStore>();
//	@Column(name = "tipus")
//	@Enumerated(EnumType.STRING)
//	private DocumentNotificacioTipusEnumDto tipus;
//	@Column(name = "data_recepcio")
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date dataRecepcio;
//	@Column(name = "registre_num", length = 100)
//	private String registreNumero;
//	@Column(name = "inter_doctip")
//	private InteressatDocumentTipusEnumDto interessatDocumentTipus;
//	@Column(name = "inter_docnum", length = 17)
//	private String interessatDocumentNum;
//	@Column(name = "inter_nom", length = 30)
//	private String interessatNom;
//	@Column(name = "inter_lling1", length = 30)
//	private String interessatLlinatge1;
//	@Column(name = "inter_lling2", length = 30)
//	private String interessatLlinatge2;
//	@Column(name = "inter_paicod", length = 4)
//	private String interessatPaisCodi;
//	@Column(name = "inter_prvcod", length = 2)
//	private String interessatProvinciaCodi;
//	@Column(name = "inter_muncod", length = 5)
//	private String interessatMunicipiCodi;
//	@Column(name = "inter_email", length = 160)
//	private String interessatEmail;
//	@Column(name = "inter_repres")
//	private boolean interessatRepresentant;
//	@Column(name = "unitat_adm", length = 9)
//	private String unitatAdministrativa;
//	@Column(name = "organ_codi", length = 9)
//	private String organCodi;
//	@Column(name = "oficina_codi", length = 4)
//	private String oficinaCodi;
//	@Column(name = "avis_titol", length = 256)
//	private String avisTitol;
//	@Column(name = "avis_text", length = 1024)
//	private String avisText;
//	@Column(name = "avis_textsms", length = 200)
//	private String avisTextSms;
//	@Column(name = "ofici_titol", length = 256)
//	private String oficiTitol;
//	@Column(name = "ofici_text", length = 1024)
//	private String oficiText;
//	@Column(name = "idioma", length = 2)
//	@Enumerated(EnumType.STRING)
//	private InteressatIdiomaEnumDto idioma;
//	@Column(name = "enviam_data")
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date enviamentData;
//	@Column(name = "enviam_count")
//	private Integer enviamentCount;
//	@Column(name = "enviam_error")
//	private boolean enviamentError;
//	@Column(name = "enviam_error_desc", length = 2048)
//	private String enviamentErrorDescripcio;
//	@Column(name = "proces_data")
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date processamentData;
//	@Column(name = "proces_count")
//	private Integer processamentCount;
//	@Column(name = "proces_error")
//	private boolean processamentError;
//	@Column(name = "proces_error_desc", length = 2048)
//	private String processamentErrorDescripcio;
//	@Version
//	private long version = 0;
//	
//	
//
//	
	public Long getId() {
		return id;
	}




	public void setId(Long id) {
		this.id = id;
	}




	public Expedient getExpedient() {
		return expedient;
	}




	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}


//
//
//	public DocumentEnviamentEstatEnumDto getEstat() {
//		return estat;
//	}
//
//
//
//
//	public void setEstat(DocumentEnviamentEstatEnumDto estat) {
//		this.estat = estat;
//	}
//
//
//
//
//	public String getAssumpte() {
//		return assumpte;
//	}
//
//
//
//
//	public void setAssumpte(String assumpte) {
//		this.assumpte = assumpte;
//	}
//
//
//
//
//	public Date getDataEnviament() {
//		return dataEnviament;
//	}
//
//
//
//
//	public void setDataEnviament(Date dataEnviament) {
//		this.dataEnviament = dataEnviament;
//	}
//
//
//
//
//	public String getObservacions() {
//		return observacions;
//	}
//
//
//
//
//	public void setObservacions(String observacions) {
//		this.observacions = observacions;
//	}
//
//
//
//
//	public DocumentStore getDocument() {
//		return document;
//	}
//
//
//
//
//	public void setDocument(DocumentStore document) {
//		this.document = document;
//	}
//
//
//
//
//	public List<DocumentStore> getAnnexos() {
//		return annexos;
//	}
//
//
//
//
//	public void setAnnexos(List<DocumentStore> annexos) {
//		this.annexos = annexos;
//	}
//
//
//
//
//	public DocumentNotificacioTipusEnumDto getTipus() {
//		return tipus;
//	}
//
//
//
//
//	public void setTipus(DocumentNotificacioTipusEnumDto tipus) {
//		this.tipus = tipus;
//	}
//
//
//
//
//	public Date getDataRecepcio() {
//		return dataRecepcio;
//	}
//
//
//
//
//	public void setDataRecepcio(Date dataRecepcio) {
//		this.dataRecepcio = dataRecepcio;
//	}
//
//
//
//
//	public String getRegistreNumero() {
//		return registreNumero;
//	}
//
//
//
//
//	public void setRegistreNumero(String registreNumero) {
//		this.registreNumero = registreNumero;
//	}
//
//
//
//
//	public InteressatDocumentTipusEnumDto getInteressatDocumentTipus() {
//		return interessatDocumentTipus;
//	}
//
//
//
//
//	public void setInteressatDocumentTipus(InteressatDocumentTipusEnumDto interessatDocumentTipus) {
//		this.interessatDocumentTipus = interessatDocumentTipus;
//	}
//
//
//
//
//	public String getInteressatDocumentNum() {
//		return interessatDocumentNum;
//	}
//
//
//
//
//	public void setInteressatDocumentNum(String interessatDocumentNum) {
//		this.interessatDocumentNum = interessatDocumentNum;
//	}
//
//
//
//
//	public String getInteressatNom() {
//		return interessatNom;
//	}
//
//
//
//
//	public void setInteressatNom(String interessatNom) {
//		this.interessatNom = interessatNom;
//	}
//
//
//
//
//	public String getInteressatLlinatge1() {
//		return interessatLlinatge1;
//	}
//
//
//
//
//	public void setInteressatLlinatge1(String interessatLlinatge1) {
//		this.interessatLlinatge1 = interessatLlinatge1;
//	}
//
//
//
//
//	public String getInteressatLlinatge2() {
//		return interessatLlinatge2;
//	}
//
//
//
//
//	public void setInteressatLlinatge2(String interessatLlinatge2) {
//		this.interessatLlinatge2 = interessatLlinatge2;
//	}
//
//
//
//
//	public String getInteressatPaisCodi() {
//		return interessatPaisCodi;
//	}
//
//
//
//
//	public void setInteressatPaisCodi(String interessatPaisCodi) {
//		this.interessatPaisCodi = interessatPaisCodi;
//	}
//
//
//
//
//	public String getInteressatProvinciaCodi() {
//		return interessatProvinciaCodi;
//	}
//
//
//
//
//	public void setInteressatProvinciaCodi(String interessatProvinciaCodi) {
//		this.interessatProvinciaCodi = interessatProvinciaCodi;
//	}
//
//
//
//
//	public String getInteressatMunicipiCodi() {
//		return interessatMunicipiCodi;
//	}
//
//
//
//
//	public void setInteressatMunicipiCodi(String interessatMunicipiCodi) {
//		this.interessatMunicipiCodi = interessatMunicipiCodi;
//	}
//
//
//
//
//	public String getInteressatEmail() {
//		return interessatEmail;
//	}
//
//
//
//
//	public void setInteressatEmail(String interessatEmail) {
//		this.interessatEmail = interessatEmail;
//	}
//
//
//
//
//	public boolean isInteressatRepresentant() {
//		return interessatRepresentant;
//	}
//
//
//
//
//	public void setInteressatRepresentant(boolean interessatRepresentant) {
//		this.interessatRepresentant = interessatRepresentant;
//	}
//
//
//
//
//	public String getUnitatAdministrativa() {
//		return unitatAdministrativa;
//	}
//
//
//
//
//	public void setUnitatAdministrativa(String unitatAdministrativa) {
//		this.unitatAdministrativa = unitatAdministrativa;
//	}
//
//
//
//
//	public String getOrganCodi() {
//		return organCodi;
//	}
//
//
//
//
//	public void setOrganCodi(String organCodi) {
//		this.organCodi = organCodi;
//	}
//
//
//
//
//	public String getOficinaCodi() {
//		return oficinaCodi;
//	}
//
//
//
//
//	public void setOficinaCodi(String oficinaCodi) {
//		this.oficinaCodi = oficinaCodi;
//	}
//
//
//
//
//	public String getAvisTitol() {
//		return avisTitol;
//	}
//
//
//
//
//	public void setAvisTitol(String avisTitol) {
//		this.avisTitol = avisTitol;
//	}
//
//
//
//
//	public String getAvisText() {
//		return avisText;
//	}
//
//
//
//
//	public void setAvisText(String avisText) {
//		this.avisText = avisText;
//	}
//
//
//
//
//	public String getAvisTextSms() {
//		return avisTextSms;
//	}
//
//
//
//
//	public void setAvisTextSms(String avisTextSms) {
//		this.avisTextSms = avisTextSms;
//	}
//
//
//
//
//	public String getOficiTitol() {
//		return oficiTitol;
//	}
//
//
//
//
//	public void setOficiTitol(String oficiTitol) {
//		this.oficiTitol = oficiTitol;
//	}
//
//
//
//
//	public String getOficiText() {
//		return oficiText;
//	}
//
//
//
//
//	public void setOficiText(String oficiText) {
//		this.oficiText = oficiText;
//	}
//
//
//
//
//	public InteressatIdiomaEnumDto getIdioma() {
//		return idioma;
//	}
//
//
//
//
//	public void setIdioma(InteressatIdiomaEnumDto idioma) {
//		this.idioma = idioma;
//	}
//
//
//
//
//	public Date getEnviamentData() {
//		return enviamentData;
//	}
//
//
//
//
//	public void setEnviamentData(Date enviamentData) {
//		this.enviamentData = enviamentData;
//	}
//
//
//
//
//	public Integer getEnviamentCount() {
//		return enviamentCount;
//	}
//
//
//
//
//	public void setEnviamentCount(Integer enviamentCount) {
//		this.enviamentCount = enviamentCount;
//	}
//
//
//
//
//	public boolean isEnviamentError() {
//		return enviamentError;
//	}
//
//
//
//
//	public void setEnviamentError(boolean enviamentError) {
//		this.enviamentError = enviamentError;
//	}
//
//
//
//
//	public String getEnviamentErrorDescripcio() {
//		return enviamentErrorDescripcio;
//	}
//
//
//
//
//	public void setEnviamentErrorDescripcio(String enviamentErrorDescripcio) {
//		this.enviamentErrorDescripcio = enviamentErrorDescripcio;
//	}
//
//
//
//
//	public Date getProcessamentData() {
//		return processamentData;
//	}
//
//
//
//
//	public void setProcessamentData(Date processamentData) {
//		this.processamentData = processamentData;
//	}
//
//
//
//
//	public Integer getProcessamentCount() {
//		return processamentCount;
//	}
//
//
//
//
//	public void setProcessamentCount(Integer processamentCount) {
//		this.processamentCount = processamentCount;
//	}
//
//
//
//
//	public boolean isProcessamentError() {
//		return processamentError;
//	}
//
//
//
//
//	public void setProcessamentError(boolean processamentError) {
//		this.processamentError = processamentError;
//	}
//
//
//
//
//	public String getProcessamentErrorDescripcio() {
//		return processamentErrorDescripcio;
//	}
//
//
//
//
//	public void setProcessamentErrorDescripcio(String processamentErrorDescripcio) {
//		this.processamentErrorDescripcio = processamentErrorDescripcio;
//	}




	private static final long serialVersionUID = 1L;
}
