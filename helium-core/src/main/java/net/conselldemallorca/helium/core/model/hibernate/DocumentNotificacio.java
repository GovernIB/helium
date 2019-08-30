/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;

import net.conselldemallorca.helium.integracio.plugins.notificacio.EnviamentEstat;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEnviamentEstatEnumDto;

/**
 * Objecte de domini que representa una notificació electronica de un expedient.
 * Només inclou 1 enviament, i per ara sense annexos
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_document_notificacio",
uniqueConstraints = {
		@UniqueConstraint(columnNames = {
				"expedient_id",
				"document_store_id",
				"enviat_data"})})
public class DocumentNotificacio implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_docnot")
	@TableGenerator(name="gen_docnot", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	
	@Column(name = "usuari_codi")
	private String usuariCodi;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "hel_expedient_docnot_fk")
	private Expedient expedient;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "document_store_id")
	@ForeignKey(name = "hel_document_notif_fk")
	private DocumentStore document;
	
	@ManyToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY)
	@JoinTable(
			name = "hel_document_notificacio_anx",
			joinColumns = {@JoinColumn(name = "document_notificacio_id")},
			inverseJoinColumns = {@JoinColumn(name = "document_store_id")})
	protected List<DocumentStore> annexos = new ArrayList<DocumentStore>();
	
	@Column(name = "emisor")
	private String emisorDir3Codi;
	
	@Column(name = "tipus")
	@Enumerated(EnumType.STRING)
	private EnviamentTipusEnumDto tipus;
	
	@Column(name = "data_programada")
	@Temporal(TemporalType.DATE)
	private Date dataProgramada;
	
	@Column(name = "retard")
	private Integer retard;
	
	@Column(name = "data_caducitat")
	@Temporal(TemporalType.DATE)
	private Date dataCaducitat;
	
	// Titular
	@Column(name = "tit_nom", nullable = false, length = 100)
	private String titularNom;
	
	@Column(name = "tit_llinatge1", nullable = false, length = 100)
	private String titularLlinatge1;
	
	@Column(name = "tit_llinatge2", nullable = false, length = 100)
	private String titularLlinatge2;
	
	@Column(name = "tit_nif", nullable = false, length = 9)
	private String titularNif;
	
	@Column(name = "tit_telefon", nullable = false, length = 16)
	private String titularTelefon;
	
	@Column(name = "tit_email", nullable = false, length = 100)
	private String titularEmail;
	
	// Destinatari
	@Column(name = "dest_nom", nullable = false, length = 100)
	private String destinatariNom;
	
	@Column(name = "dest_llinatge1", nullable = false, length = 100)
	private String destinatariLlinatge1;
	
	@Column(name = "dest_llinatge2", nullable = false, length = 100)
	private String destinatariLlinatge2;
	
	@Column(name = "dest_nif", nullable = false, length = 9)
	private String destinatariNif;
	
	@Column(name = "dest_telefon", nullable = false, length = 16)
	private String destinatariTelefon;
	
	@Column(name = "dest_email", nullable = false, length = 100)
	private String destinatariEmail;
	
	@Column(name = "idioma", nullable = false, length = 32)
	private String seuIdioma;
	
	@Column(name = "avis_titol", nullable = false, length = 256)
	private String seuAvisTitol;
	
	@Column(name = "avis_text", nullable = false, length = 1024)
	private String seuAvisText;
	
	@Column(name = "avis_text_mobil", nullable = false, length = 256)
	private String seuAvisTextMobil;
	
	@Column(name = "ofici_titol", nullable = false, length = 256)
	private String seuOficiTitol;
	
	@Column(name = "ofici_text", nullable = false, length = 256)
	private String seuOficiText;
	
	@Column(name = "env_id", length = 100)
	private String enviamentIdentificador;
	
	@Column(name = "env_ref", length = 100)
	private String enviamentReferencia;
	
	@Column(name = "env_dat_estat", length = 20)
	private String enviamentDatatEstat;
	
	@Column(name = "env_dat_data")
	private Date enviamentDatatData;
	
	@Column(name = "env_dat_origen", length = 20)
	private String enviamentDatatOrigen;
	
	@Column(name = "env_cert_data")
	@Temporal(TemporalType.DATE)
	private Date enviamentCertificacioData;
	
	@Column(name = "env_cert_origen", length = 20)
	private String enviamentCertificacioOrigen;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "certificacio_store_id")
	@ForeignKey(name = "hel_certificacio_notif_fk")
	private DocumentStore enviamentCertificacio;
	
	// Enviament
	
	@Column(name = "estat", nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificacioEnviamentEstatEnumDto estat;
	
	@Column(name = "enviat_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date enviatData;
	
	@Column(name = "processat_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date processatData;
	
	@Column(name = "cancelat_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date cancelatData;
	
	@Column(name = "error")
	protected boolean error;
	
	@Column(name = "error_descripcio", length = ERROR_DESC_TAMANY)
	protected String errorDescripcio;
	
	@Column(name = "intent_num")
	private Integer numIntents;
	
	@Column(name = "intent_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date intentData;
	
	@Column(name = "intent_proxim_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date intentProximData;
	
	@Column(name = "enviament_tipus", length = 30)
	private String enviamentTipus;
	
	@Column(name = "concepte", nullable = false, length = 50)
	private String concepte;
	
	@Column(name = "descripcio", nullable = false)
	private String descripcio;
	
//	@Column(name = "codi_procediment", nullable = false, length = 6)
//	private String notificacioCodiProcediment;
//	
//	@Column(name = "pais_codi", nullable = false, length = 3)
//	private String entregaPostalPaisCodi;
//	
//	@Column(name = "provincia_codi", nullable = false, length = 2)
//	private String entregaPostalProvinciaCodi;
//	
//	@Column(name = "municipi_codi", nullable = false, length = 5)
//	private String entregaPostalMunicipiCodi;
//	
//	@Column(name = "adresa", nullable = false, length = 50)
//	private String entregaPostalAdresa;
//	
//	@Column(name = "exp_serie_documental", nullable = false, length = 10)
//	private String seuExpedientSerieDocumental;
//	
//	@Column(name = "exp_unitat_organitzativa", nullable = false, length = 10)
//	private String seuExpedientUnitatOrganitzativa;
//	
//	@Column(name = "exp_identificador_eni", nullable = false, length = 52)
//	private String seuExpedientIdentificadorEni;
//	
//	@Column(name = "exp_titol", nullable = false, length = 256)
//	private String seuExpedientTitol;
//	
//	@Column(name = "reg_oficina", nullable = false, length = 256)
//	private String seuRegistreOficina;
//	
//	@Column(name = "reg_llibre", nullable = false, length = 256)
//	private String seuRegistreLlibre;
	
	public DocumentNotificacio() {
		super();
		inicialitzar();
	}
	
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

	public Expedient getExpedient() {
		return expedient;
	}

	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}

	public DocumentStore getDocument() {
		return document;
	}

	public void setDocument(DocumentStore document) {
		this.document = document;
	}

	public EnviamentTipusEnumDto getTipus() {
		return tipus;
	}

	public void setTipus(EnviamentTipusEnumDto tipus) {
		this.tipus = tipus;
	}

	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}

	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
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

	public NotificacioEnviamentEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(NotificacioEnviamentEstatEnumDto estat) {
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

	public String getEnviamentTipus() {
		return enviamentTipus;
	}

	public void setEnviamentTipus(String enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
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
	
//	public String getNotificacioCodiProcediment() {
//		return notificacioCodiProcediment;
//	}
//
//	public void setNotificacioCodiProcediment(String notificacioCodiProcediment) {
//		this.notificacioCodiProcediment = notificacioCodiProcediment;
//	}
//
//	public String getEntregaPostalPaisCodi() {
//		return entregaPostalPaisCodi;
//	}
//
//	public void setEntregaPostalPaisCodi(String entregaPostalPaisCodi) {
//		this.entregaPostalPaisCodi = entregaPostalPaisCodi;
//	}
//
//	public String getEntregaPostalProvinciaCodi() {
//		return entregaPostalProvinciaCodi;
//	}
//
//	public void setEntregaPostalProvinciaCodi(String entregaPostalProvinciaCodi) {
//		this.entregaPostalProvinciaCodi = entregaPostalProvinciaCodi;
//	}
//
//	public String getEntregaPostalMunicipiCodi() {
//		return entregaPostalMunicipiCodi;
//	}
//
//	public void setEntregaPostalMunicipiCodi(String entregaPostalMunicipiCodi) {
//		this.entregaPostalMunicipiCodi = entregaPostalMunicipiCodi;
//	}
//
//	public String getEntregaPostalAdresa() {
//		return entregaPostalAdresa;
//	}
//
//	public void setEntregaPostalAdresa(String entregaPostalAdresa) {
//		this.entregaPostalAdresa = entregaPostalAdresa;
//	}
//
//	public String getSeuExpedientSerieDocumental() {
//		return seuExpedientSerieDocumental;
//	}
//
//	public void setSeuExpedientSerieDocumental(String seuExpedientSerieDocumental) {
//		this.seuExpedientSerieDocumental = seuExpedientSerieDocumental;
//	}
//
//	public String getSeuExpedientUnitatOrganitzativa() {
//		return seuExpedientUnitatOrganitzativa;
//	}
//
//	public void setSeuExpedientUnitatOrganitzativa(String seuExpedientUnitatOrganitzativa) {
//		this.seuExpedientUnitatOrganitzativa = seuExpedientUnitatOrganitzativa;
//	}
//
//	public String getSeuExpedientIdentificadorEni() {
//		return seuExpedientIdentificadorEni;
//	}
//
//	public void setSeuExpedientIdentificadorEni(String seuExpedientIdentificadorEni) {
//		this.seuExpedientIdentificadorEni = seuExpedientIdentificadorEni;
//	}
//
//	public String getSeuExpedientTitol() {
//		return seuExpedientTitol;
//	}
//
//	public void setSeuExpedientTitol(String seuExpedientTitol) {
//		this.seuExpedientTitol = seuExpedientTitol;
//	}
//
//	public String getSeuRegistreOficina() {
//		return seuRegistreOficina;
//	}
//
//	public void setSeuRegistreOficina(String seuRegistreOficina) {
//		this.seuRegistreOficina = seuRegistreOficina;
//	}
//
//	public String getSeuRegistreLlibre() {
//		return seuRegistreLlibre;
//	}
//
//	public void setSeuRegistreLlibre(String seuRegistreLlibre) {
//		this.seuRegistreLlibre = seuRegistreLlibre;
//	}

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

	public String getEnviamentDatatEstat() {
		return enviamentDatatEstat;
	}

	public void setEnviamentDatatEstat(String enviamentDatatEstat) {
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

	public DocumentStore getEnviamentCertificacio() {
		return enviamentCertificacio;
	}

	public void setEnviamentCertificacio(DocumentStore enviamentCertificacio) {
		this.enviamentCertificacio = enviamentCertificacio;
	}
	
	public List<DocumentStore> getAnnexos() {
		return annexos;
	}

	public void setAnnexos(List<DocumentStore> annexos) {
		this.annexos = annexos;
	}

	public void updateEnviat(
			Date enviatData,
			String enviamentIdentificador,
			String enviamentReferencia) {
		this.enviamentIdentificador = enviamentIdentificador;
		this.enviamentReferencia = enviamentReferencia;
		this.estat = NotificacioEnviamentEstatEnumDto.ENVIADA;
		this.enviatData = enviatData;
		this.error = false;
		this.errorDescripcio = null;
		this.numIntents = 0;
		this.intentData = null;
		this.intentProximData = null;
	}
	
	public void updateEnviat(
			Date enviatData) {
		this.estat = NotificacioEnviamentEstatEnumDto.ENVIADA;
		this.enviatData = enviatData;
		this.error = false;
		this.errorDescripcio = null;
		this.numIntents = 0;
		this.intentData = null;
		this.intentProximData = null;
	}
	public void updateEnviatError(
			String errorDescripcio,
			Date intentProximData) {
		this.estat = NotificacioEnviamentEstatEnumDto.PENDENT;
		this.error = true;
		this.errorDescripcio = StringUtils.abbreviate(errorDescripcio, ERROR_DESC_TAMANY);
		this.enviatData = null;
		this.numIntents = numIntents++;
		this.intentData = new Date();
		this.intentProximData = intentProximData;
	}

	public void updateProcessat(
			boolean processat,
			Date processatData) {
		this.estat = (processat) ? NotificacioEnviamentEstatEnumDto.PROCESSADA : NotificacioEnviamentEstatEnumDto.REBUTJADA;
		this.processatData = processatData;
		this.error = false;
		this.errorDescripcio = null;
		this.numIntents = 0;
		this.intentData = null;
		this.intentProximData = null;
	}
	public void updateProcessatError(
			String errorDescripcio,
			Date intentProximData) {
		this.estat = NotificacioEnviamentEstatEnumDto.ENVIADA;
		this.error = true;
		this.errorDescripcio = StringUtils.abbreviate(errorDescripcio, ERROR_DESC_TAMANY);
		this.processatData = null;
		this.numIntents = numIntents++;
		this.intentData = new Date();
		this.intentProximData = intentProximData;
	}

	public void updateCancelat(
			Date cancelatData) {
		this.estat = NotificacioEnviamentEstatEnumDto.CANCELADA;
		this.cancelatData = cancelatData;
		this.error = false;
		this.errorDescripcio = null;
		this.numIntents = 0;
		this.intentData = null;
		this.intentProximData = null;
	}

	protected void inicialitzar() {
		this.estat = NotificacioEnviamentEstatEnumDto.PENDENT;
		this.enviatData = null;
		this.processatData = null;
		this.cancelatData = null;
		this.error = false;
		this.errorDescripcio = null;
		this.numIntents = 0;
		this.intentData = null;
		this.intentProximData = null;
	}
	
	public void updateEnviamentEstat(
			EnviamentEstat enviamentDatatEstat,
			Date enviamentDatatData,
			String enviamentDatatOrigen,
			Date enviamentCertificacioData,
			String enviamentCertificacioOrigen,
			DocumentStore enviamentCertificacio) {
		this.enviamentDatatEstat = enviamentDatatEstat.name();
		this.enviamentDatatData = enviamentDatatData;
		this.enviamentDatatOrigen = enviamentDatatOrigen;
		this.enviamentCertificacioData = enviamentCertificacioData;
		this.enviamentCertificacioOrigen = enviamentCertificacioOrigen;
		this.enviamentCertificacio = enviamentCertificacio;
		switch (enviamentDatatEstat) {
		case LLEGIDA:
		case NOTIFICADA:
			updateProcessat(true, enviamentDatatData);
			break;
		case EXPIRADA:
		case REBUTJADA:
			updateProcessat(false, enviamentDatatData);
			break;
		case NOTIB_ENVIADA:
			updateEnviat(enviamentDatatData);
			break;
		default:
			break;
		}
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final int ERROR_DESC_TAMANY = 2048;
	private static final long serialVersionUID = 1L;
}
