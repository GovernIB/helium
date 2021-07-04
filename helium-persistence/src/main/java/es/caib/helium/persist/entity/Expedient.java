/**
 * 
 */
package es.caib.helium.persist.entity;

import es.caib.helium.logic.intf.dto.NtiTipoFirmaEnumDto;
import org.apache.commons.text.StringEscapeUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Objecte de domini que representa un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_expedient",
		indexes = {
				@Index(name = "hel_expedient_estat_i", columnList = "estat_id"),
				@Index(name = "hel_expedient_entorn_i", columnList = "entorn_id"),
				@Index(name = "hel_expedient_tipus_i", columnList = "tipus_id")
		}
)
public class Expedient implements Serializable, GenericEntity<Long> {

	private static final String SEPARADOR_SISTRA = "#";

	public enum IniciadorTipus {
		INTERN,
		SISTRA}

	private Long id;
	@NotBlank
	@Size(max = 255)
	private String processInstanceId;
	@Size(max = 255)
	private String titol;
	@Size(max = 64)
	private String numero;
	@Size(max = 64)
	private String numeroDefault;
	@NotNull
	private Date dataInici = new Date();
	private Date dataFi;
	@Size(max = 255)
	private String comentari;
	@Size(max = 1024)
	private String infoAturat;
	@NotNull
	private IniciadorTipus iniciadorTipus;
	@Size(max = 64)
	private String iniciadorCodi;
	@Size(max = 64)
	private String responsableCodi;
	private boolean anulat;
	private String grupCodi;
	private String comentariAnulat;

	private Double geoPosX;
	private Double geoPosY;
	@Size(max = 64)
	private String geoReferencia;

	@Size(max = 64)
	private String registreNumero;
	private Date registreData;
	private Long unitatAdministrativa;
	private String idioma;
	private boolean autenticat;
	@Size(max = 16)
	protected String tramitadorNif;
	@Size(max = 255)
	protected String tramitadorNom;
	@Size(max = 16)
	protected String interessatNif;
	@Size(max = 255)
	protected String interessatNom;
	@Size(max = 16)
	protected String representantNif;
	@Size(max = 255)
	protected String representantNom;
	private boolean avisosHabilitats = false;
	@Size(max = 255)
	private String avisosEmail;
	@Size(max = 255)
	private String avisosMobil;
	private boolean notificacioTelematicaHabilitada = false;
	@Size(max = 255)
	private String tramitExpedientIdentificador;
	@Size(max = 255)
	private String tramitExpedientClau;
	
	@Size(max = 255)
	private String errorDesc;
	private String errorFull;

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
	@Size(max = 256)
	private String ntiVersion;
	@Size(max = 52)
	private String ntiIdentificador;
	@Size(max = 256)
	private String ntiOrgano;
	@Size(max = 16)
	private String ntiSerieDocumental;
	@Size(max = 44)
	private String ntiClasificacion;
	private NtiTipoFirmaEnumDto ntiTipoFirma;
	@Size(max = 256)
	private String ntiCsv;
	@Size(max = 256)
	private String ntiDefinicionGenCsv;
	private boolean arxiuActiu;
	@Size(max = 32)
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
	@JoinColumn(
			name="estat_id",
			foreignKey = @ForeignKey(name="hel_estat_expedient_fk"))
	public Estat getEstat() {
		return estat;
	}
	public void setEstat(Estat estat) {
		this.estat = estat;
	}

	@ManyToOne(optional=false)
	@JoinColumn(
			name="tipus_id",
			foreignKey = @ForeignKey(name="hel_exptipus_expedient_fk"))
	public ExpedientTipus getTipus() {
		return tipus;
	}
	public void setTipus(ExpedientTipus tipus) {
		this.tipus = tipus;
	}

	@ManyToOne(optional=false)
	@JoinColumn(
			name="entorn_id",
			foreignKey = @ForeignKey(name="hel_entorn_expedient_fk"))
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
			inverseJoinColumns=@JoinColumn(name="desti_id", referencedColumnName="id"),
			foreignKey = @ForeignKey(name="hel_origen_exprel_fk"),
			inverseForeignKey = @ForeignKey(name="hel_desti_exprel_fk"))
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
			return StringEscapeUtils.escapeHtml4(getIdentificador().substring(0, 100) + " (...)");
		else
			return StringEscapeUtils.escapeHtml4(getIdentificador());
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
