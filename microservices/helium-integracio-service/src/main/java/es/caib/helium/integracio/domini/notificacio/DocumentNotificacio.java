package es.caib.helium.integracio.domini.notificacio;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;

import es.caib.helium.integracio.domini.portafirmes.GenericEntity;
import es.caib.helium.integracio.enums.notificacio.EnviamentEstat;
import es.caib.helium.integracio.enums.notificacio.EnviamentTipus;
import es.caib.helium.integracio.enums.notificacio.NotificacioEnviamentEstatEnumDto;
import es.caib.helium.integracio.enums.notificacio.NotificacioEstat;
import lombok.Data;

/**
 * Objecte de domini que representa una notificació electronica de un expedient.
 * Només inclou 1 enviament, i per ara sense annexos
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Entity
@Table(name="HEL_DOCUMENT_NOTIFICACIO",
uniqueConstraints = {
		@UniqueConstraint(columnNames = {
				"expedient_id",
				"document_store_id",
				"enviat_data"})})
public class DocumentNotificacio implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@TableGenerator(name="gen_docnot", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	
	@Column(name = "usuari_codi")
	private String usuariCodi;
	
	@Column(name = "expedient_id")
	private Long expedientId;
	
	@Column(name = "document_store_id")
	private Long documentStoreId;
	
//	@ManyToMany(
//			cascade = CascadeType.ALL,
//			fetch = FetchType.LAZY)
//	@JoinTable(
//			name = "hel_document_notificacio_anx",
//			joinColumns = {@JoinColumn(name = "document_notificacio_id")},
//			inverseJoinColumns = {@JoinColumn(name = "document_store_id")})
//	protected List<DocumentStore> annexos = new ArrayList<DocumentStore>();
	
	@Column(name = "emisor")
	private String emisorDir3Codi;
	
	@Column(name = "tipus")
	@Enumerated(EnumType.STRING)
	private EnviamentTipus tipus;
	
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
	@Enumerated(EnumType.STRING)
	private NotificacioEnviamentEstatEnumDto enviamentDatatEstat;
	
	@Column(name = "env_dat_data")
	private Date enviamentDatatData;
	
	@Column(name = "env_dat_origen", length = 20)
	private String enviamentDatatOrigen;
	
	@Column(name = "env_cert_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date enviamentCertificacioData;
	
	@Column(name = "env_cert_origen", length = 20)
	private String enviamentCertificacioOrigen;
	
//	@ManyToOne(optional = false, fetch = FetchType.EAGER)
//	@JoinColumn(name = "certificacio_store_id")
//	@ForeignKey(name = "hel_certificacio_notif_fk")
//	private DocumentStore enviamentCertificacio;
	@Column(name = "certificacio_store_id")
	private Long certificacioStoreId;
	
	// Enviament
	
	@Column(name = "estat", nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificacioEstat estat;
	
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
	
	public DocumentNotificacio(DadesNotificacioDto dto) {
		
		super();
		inicialitzar();
		
		usuariCodi = dto.getUsuariCodi();
		tipus = dto.getEnviamentTipus() != null ? dto.getEnviamentTipus() : EnviamentTipus.NOTIFICACIO;
		expedientId = dto.getExpedientId();
		emisorDir3Codi = dto.getEmisorDir3Codi();
		dataProgramada = dto.getEnviamentDataProgramada();
		retard = dto.getRetard();
		dataCaducitat = dto.getCaducitat();
		documentStoreId = dto.getDocumentId();
		
		//TODO: Només 1 enviament. Helium 3.2
		var enviament = dto.getEnviaments().get(0);
		
		//Titular
		var titular = enviament.getTitular();
		titularNif = titular.getNif();
		titularNom = titular.getNom();
		titularLlinatge1 = titular.getLlinatge1();
		titularLlinatge2 = titular.getLlinatge2();
		titularTelefon = titular.getTelefon();
		titularEmail = titular.getEmail();
		
		//Destinatari
		var destinataris = enviament.getDestinataris();
		if (destinataris != null && !destinataris.isEmpty()) {
			//TODO Només un destinatari. Helium 3.2
			var destinatari = destinataris.get(0);
			destinatariNif = destinatari.getNif();
			destinatariNom = destinatari.getNom();
			destinatariLlinatge1 = destinatari.getLlinatge1();
			destinatariLlinatge2 = destinatari.getLlinatge2();
			destinatariTelefon = destinatari.getTelefon();
			destinatariEmail = destinatari.getEmail();
		}
		
		numIntents = 0;
		estat = NotificacioEstat.PENDENT;
		concepte = dto.getConcepte();
		descripcio = dto.getDescripcio();
		enviatData = new Date(); //TODO confirmar que és el moment correcte
	}
	
//	public Expedient getExpedient() {
//		return expedient;
//	}
//
//	public void setExpedient(Expedient expedient) {
//		this.expedient = expedient;
//	}
//
//	public DocumentStore getDocument() {
//		return document;
//	}
//
//	public void setDocument(DocumentStore document) {
//		this.document = document;
//	}

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

//	public DocumentStore getEnviamentCertificacio() {
//		return enviamentCertificacio;
//	}
//
//	public void setEnviamentCertificacio(DocumentStore enviamentCertificacio) {
//		this.enviamentCertificacio = enviamentCertificacio;
//	}
//	
//	public List<DocumentStore> getAnnexos() {
//		return annexos;
//	}
//
//	public void setAnnexos(List<DocumentStore> annexos) {
//		this.annexos = annexos;
//	}

	public void updateEnviat(
			Date enviatData,
			String enviamentIdentificador,
			String enviamentReferencia) {
		this.enviamentIdentificador = enviamentIdentificador;
		this.enviamentReferencia = enviamentReferencia;
		this.estat = NotificacioEstat.ENVIADA;
		this.enviatData = enviatData;
		this.error = false;
		this.errorDescripcio = null;
		this.numIntents = 0;
		this.intentData = null;
		this.intentProximData = null;
	}
	
	public void updateEnviat(
			Date enviatData) {
		this.estat = NotificacioEstat.ENVIADA;
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
		this.estat = NotificacioEstat.PENDENT;
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
		this.estat = (processat) ? NotificacioEstat.PROCESSADA : NotificacioEstat.ENVIADA;
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
		this.estat = NotificacioEstat.ENVIADA;
		this.error = true;
		this.errorDescripcio = StringUtils.abbreviate(errorDescripcio, ERROR_DESC_TAMANY);
		this.processatData = null;
		this.numIntents = numIntents++;
		this.intentData = new Date();
		this.intentProximData = intentProximData;
	}

	public void updateCancelat(
			Date cancelatData) {
		this.estat = NotificacioEstat.FINALITZADA;
		this.cancelatData = cancelatData;
		this.error = false;
		this.errorDescripcio = null;
		this.numIntents = 0;
		this.intentData = null;
		this.intentProximData = null;
	}

	protected void inicialitzar() {
		this.estat = NotificacioEstat.PENDENT;
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
//			DocumentStore enviamentCertificacio,
			Long certificacioStoreId,
			boolean error,
			String errorDescripcio) {
		this.enviamentDatatEstat = NotificacioEnviamentEstatEnumDto.valueOf(enviamentDatatEstat.toString());
		this.enviamentDatatData = enviamentDatatData;
		this.enviamentDatatOrigen = enviamentDatatOrigen;
		this.enviamentCertificacioData = enviamentCertificacioData;
		this.enviamentCertificacioOrigen = enviamentCertificacioOrigen;
//		this.enviamentCertificacio = enviamentCertificacio;
		this.certificacioStoreId = certificacioStoreId;
		this.error = error;
		this.errorDescripcio = errorDescripcio;
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

	private static final int ERROR_DESC_TAMANY = 2048;
	private static final long serialVersionUID = 1L;

}

 