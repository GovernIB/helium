/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;

/**
 * Objecte de domini que representa un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_expedient")
@org.hibernate.annotations.Table(
		appliesTo = "hel_expedient",
		indexes = {
				@Index(name = "hel_expedient_estat_i", columnNames = {"estat_id"}),
				@Index(name = "hel_expedient_entorn_i", columnNames = {"entorn_id"}),
				@Index(name = "hel_expedient_tipus_i", columnNames = {"tipus_id"})})
public class Expedient implements Serializable, GenericEntity<Long> {

	private static final String SEPARADOR_SISTRA = "#";

	public enum IniciadorTipus {
		INTERN,
		SISTRA}

	private Long id;
	@NotBlank
	@MaxLength(255)
	private String processInstanceId;
	@MaxLength(255)
	private String titol;
	@MaxLength(64)
	private String numero;
	@MaxLength(64)
	private String numeroDefault;
	private UnitatOrganitzativa unitatOrganitzativa;
	@NotNull
	private Date dataInici = new Date();
	private Date dataFi;
	@MaxLength(255)
	private String comentari;
	@MaxLength(1024)
	private String infoAturat;
	@NotNull
	private IniciadorTipus iniciadorTipus;
	@MaxLength(64)
	private String iniciadorCodi;
	@MaxLength(64)
	private String responsableCodi;
	private boolean anulat;
	private String grupCodi;
	private String comentariAnulat;

	private Double geoPosX;
	private Double geoPosY;
	@MaxLength(64)
	private String geoReferencia;

	//TODO4: comprovar que s'informa bé com pels tràmits de sistra 1. Bucar per títol BTE
	@MaxLength(64)
	private String registreNumero;
	
	private Date registreData;
	private Long unitatAdministrativa;
	private String idioma;
	private boolean autenticat;
	@MaxLength(16)
	protected String tramitadorNif;
	@MaxLength(255)
	protected String tramitadorNom;
	@MaxLength(16)
	protected String interessatNif;
	@MaxLength(255)
	protected String interessatNom;
	@MaxLength(16)
	protected String representantNif;
	@MaxLength(255)
	protected String representantNom;
	private boolean avisosHabilitats = false;
	@MaxLength(255)
	private String avisosEmail;
	@MaxLength(255)
	private String avisosMobil;
	private boolean notificacioTelematicaHabilitada = false;
	@MaxLength(255)
	private String tramitExpedientIdentificador;
	@MaxLength(255)
	private String tramitExpedientClau;
	
	@MaxLength(255)
	private String errorDesc;
	private String errorFull;
	private String errorArxiu;

	private boolean errorsIntegracions;
	
	List<Interessat> interessats;

	List<Notificacio> notificacions;

	private Estat estat;
	@NotNull
	private ExpedientTipus tipus;
	@NotNull
	private Entorn entorn;

	private Set<Expedient> relacionsOrigen = new HashSet<Expedient>();
	private Set<Expedient> relacionsDesti = new HashSet<Expedient>();
	private Set<Alerta> alertes = new HashSet<Alerta>();
	private List<ExpedientLog> logs = new ArrayList<ExpedientLog>();
	private List<Portasignatures> portasignatures = new ArrayList<Portasignatures>();

	private boolean ambRetroaccio;
	private Date reindexarData;
	private boolean reindexarError;

	private boolean ntiActiu;
	@MaxLength(256)
	private String ntiVersion;
	@MaxLength(52)
	private String ntiIdentificador;
	@MaxLength(256)
	private String ntiOrgano;
	@MaxLength(16)
	private String ntiSerieDocumental;
	@MaxLength(44)
	private String ntiClasificacion;
	private NtiTipoFirmaEnumDto ntiTipoFirma;
	@MaxLength(256)
	private String ntiCsv;
	@MaxLength(256)
	private String ntiDefinicionGenCsv;
	private boolean arxiuActiu;
	@MaxLength(32)
	private String arxiuUuid;

	public Expedient() {}
	public Expedient(IniciadorTipus iniciadorTipus, String iniciadorCodi, ExpedientTipus tipus, Entorn entorn, String processInstanceId) {
		this.iniciadorTipus = iniciadorTipus;
		this.iniciadorCodi = iniciadorCodi;
		this.tipus = tipus;
		this.entorn = entorn;
		this.processInstanceId = processInstanceId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_expedient")
	@TableGenerator(name="gen_expedient", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	

	@Column(name="process_instance_id", length=255, nullable=false, unique=true)
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name="titol", length=255, nullable=true)
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}

	@Column(name="numero", length=64, nullable=true)
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}

	@Column(name="numero_default", length=64)
	public String getNumeroDefault() {
		return numeroDefault;
	}
	public void setNumeroDefault(String numeroDefault) {
		this.numeroDefault = numeroDefault;
	}
	@ManyToOne(optional=true)
	@JoinColumn(name="unitat_organitzativa_id")
	@ForeignKey(name="hel_unit_org_expedient_fk")
	public UnitatOrganitzativa getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}
	public void setUnitatOrganitzativa(UnitatOrganitzativa unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa;
	}
	@Column(name="data_inici", nullable=false)
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}

	@Column(name="data_fi", nullable=true)
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	@Column(name="comentari", length=255, nullable=true)
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}

	@Column(name="info_aturat", length=1024, nullable=true)
	public String getInfoAturat() {
		return infoAturat;
	}
	public void setInfoAturat(String infoAturat) {
		this.infoAturat = infoAturat;
	}

	@Column(name="iniciador_tipus")
	public IniciadorTipus getIniciadorTipus() {
		return iniciadorTipus;
	}
	public void setIniciadorTipus(IniciadorTipus iniciadorTipus) {
		this.iniciadorTipus = iniciadorTipus;
	}

	@Column(name="iniciador_codi", length=64)
	public String getIniciadorCodi() {
		return iniciadorCodi;
	}
	public void setIniciadorCodi(String iniciadorCodi) {
		this.iniciadorCodi = iniciadorCodi;
	}

	@Column(name="responsable_codi", length=64)
	public String getResponsableCodi() {
		return responsableCodi;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}

	@Column(name="anulat")
	public boolean isAnulat() {
		return anulat;
	}
	public void setAnulat(boolean anulat) {
		this.anulat = anulat;
	}

	@Column(name="grup_codi", length=64)
	public String getGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(String grupCodi) {
		this.grupCodi = grupCodi;
	}

	@Column(name="geo_posx")
	public Double getGeoPosX() {
		return geoPosX;
	}
	public void setGeoPosX(Double geoPosX) {
		this.geoPosX = geoPosX;
	}

	@Column(name="geo_posy")
	public Double getGeoPosY() {
		return geoPosY;
	}
	public void setGeoPosY(Double geoPosY) {
		this.geoPosY = geoPosY;
	}

	@Column(name="geo_referencia", length=64)
	public String getGeoReferencia() {
		return geoReferencia;
	}
	public void setGeoReferencia(String geoReferencia) {
		this.geoReferencia = geoReferencia;
	}

	@Column(name="registre_num", length=64)
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}

	@Column(name="registre_data")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}

	@Column(name="unitat_adm")
	public Long getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public void setUnitatAdministrativa(Long unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}

	@Column(name="idioma", length=8)
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	@Column(name="autenticat")
	public boolean isAutenticat() {
		return autenticat;
	}
	public void setAutenticat(boolean autenticat) {
		this.autenticat = autenticat;
	}

	@Column(name="tramitador_nif", length=16)
	public String getTramitadorNif() {
		return tramitadorNif;
	}
	public void setTramitadorNif(String tramitadorNif) {
		this.tramitadorNif = tramitadorNif;
	}

	@Column(name="tramitador_nom", length=255)
	public String getTramitadorNom() {
		return tramitadorNom;
	}
	public void setTramitadorNom(String tramitadorNom) {
		this.tramitadorNom = tramitadorNom;
	}

	@Column(name="interessat_nif", length=16)
	public String getInteressatNif() {
		return interessatNif;
	}
	public void setInteressatNif(String interessatNif) {
		this.interessatNif = interessatNif;
	}

	@Column(name="interessat_nom", length=255)
	public String getInteressatNom() {
		return interessatNom;
	}
	public void setInteressatNom(String interessatNom) {
		this.interessatNom = interessatNom;
	}

	@Column(name="representant_nif", length=16)
	public String getRepresentantNif() {
		return representantNif;
	}
	public void setRepresentantNif(String representantNif) {
		this.representantNif = representantNif;
	}

	@Column(name="representant_nom", length=255)
	public String getRepresentantNom() {
		return representantNom;
	}
	public void setRepresentantNom(String representantNom) {
		this.representantNom = representantNom;
	}

	@Column(name="avisos_habilitat")
	public boolean isAvisosHabilitats() {
		return avisosHabilitats;
	}
	public void setAvisosHabilitats(boolean avisosHabilitats) {
		this.avisosHabilitats = avisosHabilitats;
	}

	@Column(name="avisos_email", length=255)
	public String getAvisosEmail() {
		return avisosEmail;
	}
	public void setAvisosEmail(String avisosEmail) {
		this.avisosEmail = avisosEmail;
	}

	@Column(name="avisos_mobil", length=255)
	public String getAvisosMobil() {
		return avisosMobil;
	}
	public void setAvisosMobil(String avisosMobil) {
		this.avisosMobil = avisosMobil;
	}

	@Column(name="nottel_habilitat")
	public boolean isNotificacioTelematicaHabilitada() {
		return notificacioTelematicaHabilitada;
	}
	public void setNotificacioTelematicaHabilitada(
			boolean notificacioTelematicaHabilitada) {
		this.notificacioTelematicaHabilitada = notificacioTelematicaHabilitada;
	}

	@Column(name="tramexp_id", length=255)
	public String getTramitExpedientIdentificador() {
		return tramitExpedientIdentificador;
	}
	public void setTramitExpedientIdentificador(String tramitExpedientIdentificador) {
		this.tramitExpedientIdentificador = tramitExpedientIdentificador;
	}

	@Column(name="tramexp_clau", length=255)
	public String getTramitExpedientClau() {
		return tramitExpedientClau;
	}
	public void setTramitExpedientClau(String tramitExpedientClau) {
		this.tramitExpedientClau = tramitExpedientClau;
	}

	@Column(name="errors_integs")
	public boolean isErrorsIntegracions() {
		return errorsIntegracions;
	}
	public void setErrorsIntegracions(boolean errorsIntegracions) {
		this.errorsIntegracions = errorsIntegracions;
	}

	@Column(name="error_desc")
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	
	@Lob
	@Column(name="error_full")
	public String getErrorFull() {
		return errorFull;
	}
	public void setErrorFull(String errorFull) {
		this.errorFull = errorFull;
	}
	
	@ManyToOne(optional=true)
	@JoinColumn(name="estat_id")
	@ForeignKey(name="hel_estat_expedient_fk")
	public Estat getEstat() {
		return estat;
	}
	public void setEstat(Estat estat) {
		this.estat = estat;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="tipus_id")
	@ForeignKey(name="hel_exptipus_expedient_fk")
	public ExpedientTipus getTipus() {
		return tipus;
	}
	public void setTipus(ExpedientTipus tipus) {
		this.tipus = tipus;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="entorn_id")
	@ForeignKey(name="hel_entorn_expedient_fk")
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@ManyToMany()
	@JoinTable(
			name="hel_expedient_rels",
			joinColumns=@JoinColumn(name="origen_id", referencedColumnName="id"),
			inverseJoinColumns=@JoinColumn(name="desti_id", referencedColumnName="id")
	)
	@ForeignKey(name="hel_origen_exprel_fk", inverseName="hel_desti_exprel_fk")
	public Set<Expedient> getRelacionsOrigen() {
		return this.relacionsOrigen;
	}
	public void setRelacionsOrigen(Set<Expedient> relacionsOrigen) {
		this.relacionsOrigen = relacionsOrigen;
	}
	public void addRelacioOrigen(Expedient relacionat) {
		getRelacionsOrigen().add(relacionat);
	}
	public void removeRelacioOrigen(Expedient relacionat) {
		getRelacionsOrigen().remove(relacionat);
	}

	@ManyToMany(mappedBy="relacionsOrigen")
	public Set<Expedient> getRelacionsDesti() {
		return this.relacionsDesti;
	}
	public void setRelacionsDesti(Set<Expedient> relacionsDesti) {
		this.relacionsDesti = relacionsDesti;
	}
	public void addRelacioDesti(Expedient relacionat) {
		getRelacionsDesti().add(relacionat);
	}
	public void removeRelacioDesti(Expedient relacionat) {
		getRelacionsDesti().remove(relacionat);
	}

	@OneToMany(mappedBy="expedient", cascade=CascadeType.REMOVE)
	public Set<Alerta> getAlertes() {
		return this.alertes;
	}
	@OneToMany(mappedBy="expedient", cascade=CascadeType.REMOVE, orphanRemoval=true)
	public List<Interessat> getInteressats() {
		return interessats;
	}
	public void setInteressats(List<Interessat> interessats) {
		this.interessats = interessats;
	}
	public String getInteressatsNifs(String separador) {
		String resultat = null;
		if (this.interessats!=null && this.interessats.size()>0) {
			resultat = "";
			for (Interessat i: this.interessats) {
				resultat = resultat+i.getNif()+separador;
			}
			resultat = resultat.trim();
		}
		return resultat;
	}
	@OneToMany(mappedBy="expedient", cascade=CascadeType.REMOVE)
	public List<Notificacio> getNotificacions() {
		return notificacions;
	}
	public void setNotificacions(List<Notificacio> notificacions) {
		this.notificacions = notificacions;
	}
	public void setAlertes(Set<Alerta> alertes) {
		this.alertes = alertes;
	}
	public void addAlerta(Alerta alerta) {
		getAlertes().add(alerta);
	}
	public void removeAlerta(Alerta alerta) {
		getAlertes().remove(alerta);
	}

	@OneToMany(mappedBy="expedient", cascade={CascadeType.ALL})
	@OrderBy("data asc, id asc")
	public List<ExpedientLog> getLogs() {
		return logs;
	}
	public void setLogs(List<ExpedientLog> logs) {
		this.logs = logs;
	}
	public void addLogs(ExpedientLog logs) {
		getLogs().add(logs);
	}
	public void removeLogs(ExpedientLog logs) {
		getLogs().remove(logs);
	}

	@OneToMany(mappedBy="expedient", cascade={CascadeType.ALL})
	@OrderBy("dataEnviat asc")
	public List<Portasignatures> getPortasignatures() {
		return portasignatures;
	}
	public void setPortasignatures(List<Portasignatures> portasignatures) {
		this.portasignatures = portasignatures;
	}
	public void addPortasignatures(Portasignatures portasignatures) {
		getPortasignatures().add(portasignatures);
	}
	public void removePortasignatures(Portasignatures portasignatures) {
		getPortasignatures().remove(portasignatures);
	}

	@Column(name="amb_retroaccio")
	public boolean isAmbRetroaccio() {
		return ambRetroaccio;
	}
	public void setAmbRetroaccio(boolean ambRetroaccio) {
		this.ambRetroaccio = ambRetroaccio;
	}
	
	@Column(name="reindexar_data", nullable=true)
	public Date getReindexarData() {
		return reindexarData;
	}
	public void setReindexarData(Date reindexarData) {
		this.reindexarData = reindexarData;
	}
	
	@Column(name="reindexar_error")
	public boolean isReindexarError() {
		return reindexarError;
	}
	public void setReindexarError(boolean reindexarError) {
		this.reindexarError = reindexarError;
	}
	
	@Transient
	public String getNumeroIdentificador() {
		if (tipus.getTeNumero().booleanValue())
			return getNumero();
		return this.getNumeroDefault();
	}
	@Transient
	public String getIdentificador() {
		String identificador = null;
		if (tipus.getTeNumero().booleanValue() && tipus.getTeTitol().booleanValue())
			identificador = "[" + getNumero() + "] " + getTitol();
		else if (tipus.getTeNumero().booleanValue() && !tipus.getTeTitol().booleanValue())
			identificador = getNumero();
		else if (!tipus.getTeNumero().booleanValue() && tipus.getTeTitol().booleanValue())
			identificador = getTitol();
		if (identificador == null || "[null] null".equals(identificador))
			return this.getNumeroDefault();
		else
			return identificador;
	}
	@Transient
	public String getIdentificadorOrdenacio() {
		if (!tipus.getTeNumero().booleanValue() && tipus.getTeTitol().booleanValue()) {
			return getIdentificador();
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici);
			int anyInici = cal.get(Calendar.YEAR);
			return new Integer(anyInici).toString() + new DecimalFormat("0000000000000000000").format(id);
		}
	}
	@Transient
	public String getIdentificadorLimitat() {
		if (getIdentificador() != null && getIdentificador().length() > 100)
			return StringEscapeUtils.escapeHtml(getIdentificador().substring(0, 100) + " (...)");
		else
			return StringEscapeUtils.escapeHtml(getIdentificador());
	}

	@Transient
	public String getNumeroEntradaSistra() {
		if (iniciadorTipus.equals(IniciadorTipus.SISTRA)) {
			return iniciadorCodi.split(SEPARADOR_SISTRA)[0];
		}
		return null;
	}
	@Transient
	public String getClaveAccesoSistra() {
		if (iniciadorTipus.equals(IniciadorTipus.SISTRA)) {
			return iniciadorCodi.split(SEPARADOR_SISTRA)[1];
		}
		return null;
	}
	@Transient
	public static String crearIniciadorCodiPerSistra(String numeroEntrada, String claveAcceso) {
		return numeroEntrada + SEPARADOR_SISTRA + claveAcceso;
	}
	@Transient
	public boolean isAturat() {
		return infoAturat != null;
	}

	public String getComentariAnulat() {
		return comentariAnulat;
	}
	public void setComentariAnulat(String comentariAnulat) {
		this.comentariAnulat = comentariAnulat;
	}

	@Column(name="nti_actiu", nullable=false)
	public boolean isNtiActiu() {
		return ntiActiu;
	}
	public void setNtiActiu(boolean ntiActiu) {
		this.ntiActiu = ntiActiu;
	}

	@Column(name="nti_version", length=256)
	public String getNtiVersion() {
		return ntiVersion;
	}
	public void setNtiVersion(String ntiVersion) {
		this.ntiVersion = ntiVersion;
	}

	@Column(name="nti_identificador", length=52)
	public String getNtiIdentificador() {
		return ntiIdentificador;
	}
	public void setNtiIdentificador(String ntiIdentificador) {
		this.ntiIdentificador = ntiIdentificador;
	}

	@Column(name="nti_organo", length=256)
	public String getNtiOrgano() {
		return ntiOrgano;
	}
	public void setNtiOrgano(String ntiOrgano) {
		this.ntiOrgano = ntiOrgano;
	}

	@Column(name="nti_seriedocumental", length=16)
	public String getNtiSerieDocumental() {
		return ntiSerieDocumental;
	}
	public void setNtiSerieDocumental(String ntiSerieDocumental) {
		this.ntiSerieDocumental = ntiSerieDocumental;
	}

	@Column(name="nti_clasificacion", length=44)
	public String getNtiClasificacion() {
		return ntiClasificacion;
	}
	public void setNtiClasificacion(String ntiClasificacion) {
		this.ntiClasificacion = ntiClasificacion;
	}

	@Column(name="nti_tipo_firma")
	public NtiTipoFirmaEnumDto getNtiTipoFirma() {
		return ntiTipoFirma;
	}
	public void setNtiTipoFirma(NtiTipoFirmaEnumDto ntiTipoFirma) {
		this.ntiTipoFirma = ntiTipoFirma;
	}

	@Column(name="nti_csv", length=256)
	public String getNtiCsv() {
		return ntiCsv;
	}
	public void setNtiCsv(String ntiCsv) {
		this.ntiCsv = ntiCsv;
	}

	@Column(name="nti_def_gen_csv", length=256)
	public String getNtiDefinicionGenCsv() {
		return ntiDefinicionGenCsv;
	}
	public void setNtiDefinicionGenCsv(String ntiDefinicionGenCsv) {
		this.ntiDefinicionGenCsv = ntiDefinicionGenCsv;
	}

	@Column(name="arxiu_actiu")
	public boolean isArxiuActiu() {
		return arxiuActiu;
	}
	public void setArxiuActiu(boolean arxiuActiu) {
		this.arxiuActiu = arxiuActiu;
	}

	@Column(name="arxiu_uuid", length=36)
	public String getArxiuUuid() {
		return arxiuUuid;
	}
	public void setArxiuUuid(String arxiuUuid) {
		this.arxiuUuid = arxiuUuid;
	}
	@Column(name="error_arxiu", length=512)
	public String getErrorArxiu() {
		return errorArxiu;
	}
	public void setErrorArxiu(String errorArxiu) {
		if (this.errorArxiu==null || errorArxiu==null) {
			this.errorArxiu = errorArxiu;	
		} else {
			this.errorArxiu = "- "+this.errorArxiu + "<br/>" + errorArxiu;
		}
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((processInstanceId == null) ? 0 : processInstanceId
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Expedient other = (Expedient) obj;
		if (processInstanceId == null) {
			if (other.processInstanceId != null)
				return false;
		} else if (!processInstanceId.equals(other.processInstanceId))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
