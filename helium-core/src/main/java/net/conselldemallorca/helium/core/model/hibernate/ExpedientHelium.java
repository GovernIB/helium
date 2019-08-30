/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

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
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Subselect("select "
		+ "(case"
			+ " when (tipus.te_numero = 1 AND tipus.te_titol = 1) then ('['||ex.numero||'] ' || ex.titol) "
			+ " when (tipus.te_numero = 1 AND tipus.te_titol = 0) then ex.numero "
			+ " when (tipus.te_numero = 0 AND tipus.te_titol = 1) then ex.titol "
			+ " ELSE ex.numero_default END) as titol," 
		+ "(case"
			+ " when (tipus.te_numero = 1) then ex.numero "
			+ " ELSE ex.numero_default END) as numero,"
		+ "(case " 
			+ "	WHEN (ex.data_fi is null and es.id is null) then 'Iniciat'" 
			+ " WHEN (ex.data_fi is null and es.id is not null) then es.nom "
			+ " ELSE 'Finalizat' END) as estat_nom, "
		+ "ex.ID AS id,"
		+ "ex.anulat AS anulat,"
		+ "ex.autenticat AS autenticat,"
		+ "ex.avisos_email AS avisos_email,"
		+ "ex.avisos_habilitat AS avisos_habilitat,"
		+ "ex.avisos_mobil AS avisos_mobil,"
		+ "ex.comentari AS comentari,"
		+ "ex.comentariAnulat AS comentariAnulat,"
		+ "ex.data_fi AS data_fi,"
		+ "ex.data_inici AS data_inici,"
		+ "ex.entorn_id AS entorn_id,"
		+ "ex.error_desc AS error_desc,"
		+ "ex.error_full AS error_full,"
		+ "ex.errors_integs AS errors_integs,"
		+ "ex.estat_id AS estat_id,"
		+ "ex.geo_posx AS geo_posx,"
		+ "ex.geo_posy AS geo_posy,"
		+ "ex.geo_referencia AS geo_referencia,"
		+ "ex.grup_codi AS grup_codi,"
		+ "ex.idioma AS idioma,"
		+ "ex.info_aturat AS info_aturat,"
		+ "ex.iniciador_codi AS iniciador_codi,"
		+ "ex.iniciador_tipus AS iniciador_tipus,"
		+ "ex.interessat_nif AS interessat_nif,"
		+ "ex.interessat_nom AS interessat_nom,"
		+ "ex.nottel_habilitat AS nottel_habilitat,"
		+ "ex.numero_default AS numero_default,"
		+ "ex.process_instance_id AS process_instance_id,"
		+ "ex.registre_data AS registre_data,"
		+ "ex.registre_num AS registre_num,"
		+ "ex.representant_nif AS representant_nif,"
		+ "ex.representant_nom AS representant_nom,"
		+ "ex.responsable_codi AS responsable_codi,"
		+ "ex.tipus_id AS tipus_id,"
		+ "ex.tramexp_clau AS tramexp_clau,"
		+ "ex.tramexp_id AS tramexp_id,"
		+ "ex.tramitador_nif AS tramitador_nif,"
		+ "ex.tramitador_nom AS tramitador_nom,"
		+ "ex.unitat_adm AS unitat_adm "
		+ "from hel_expedient ex "
		+ "LEFT JOIN hel_estat es on (ex.estat_id = es.id) "
		+ "LEFT JOIN hel_expedient_tipus tipus on (ex.tipus_id = tipus.id) ")
@Immutable
public class ExpedientHelium {

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
	@NotNull
	private Date dataInici = new Date();
	private Date dataFi;
	@MaxLength(255)
	private String comentari;
	@MaxLength(1024)
	private String infoAturat;
	@NotNull
	private IniciadorTipus iniciadorTipus;
	@NotBlank
	@MaxLength(255)
	private String nomEstat;
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

	private boolean errorsIntegracions;

	private Estat estat;
	@NotNull
	private ExpedientTipus tipus;
	@NotNull
	private Entorn entorn;

	private Set<ExpedientHelium> relacionsOrigen = new HashSet<ExpedientHelium>();
	private Set<ExpedientHelium> relacionsDesti = new HashSet<ExpedientHelium>();
	private Set<Alerta> alertes = new HashSet<Alerta>();
	private List<ExpedientLog> logs = new ArrayList<ExpedientLog>();
	private List<Portasignatures> portasignatures = new ArrayList<Portasignatures>();



	public ExpedientHelium() {}
	public ExpedientHelium(IniciadorTipus iniciadorTipus, String iniciadorCodi, ExpedientTipus tipus, Entorn entorn, String processInstanceId) {
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
	@Column(name="estat_nom", length=255)
	public String getNomEstat() {
		return nomEstat;
	}
	public void setNomEstat(String nomEstat) {
		this.nomEstat = nomEstat;
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
	public Set<ExpedientHelium> getRelacionsOrigen() {
		return this.relacionsOrigen;
	}
	public void setRelacionsOrigen(Set<ExpedientHelium> relacionsOrigen) {
		this.relacionsOrigen = relacionsOrigen;
	}
	public void addRelacioOrigen(ExpedientHelium relacionat) {
		getRelacionsOrigen().add(relacionat);
	}
	public void removeRelacioOrigen(ExpedientHelium relacionat) {
		getRelacionsOrigen().remove(relacionat);
	}

	@ManyToMany(mappedBy="relacionsOrigen")
	public Set<ExpedientHelium> getRelacionsDesti() {
		return this.relacionsDesti;
	}
	public void setRelacionsDesti(Set<ExpedientHelium> relacionsDesti) {
		this.relacionsDesti = relacionsDesti;
	}
	public void addRelacioDesti(ExpedientHelium relacionat) {
		getRelacionsDesti().add(relacionat);
	}
	public void removeRelacioDesti(ExpedientHelium relacionat) {
		getRelacionsDesti().remove(relacionat);
	}

	@OneToMany(mappedBy="expedient", cascade=CascadeType.REMOVE)
	public Set<Alerta> getAlertes() {
		return this.alertes;
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

	@Transient
	public String getNumeroIdentificador() {
		if (tipus.getTeNumero().booleanValue())
			return getNumero();
		return this.getNumeroDefault();
	}
	@Transient
	public String getIdentificadorExp() {
		String identificador = null;
		if (tipus.getTeNumero().booleanValue() && tipus.getTeTitol().booleanValue())
			identificador = "[" + getNumero() + "] " + getTitol();
		else if (tipus.getTeNumero().booleanValue() && !tipus.getTeTitol().booleanValue())
			identificador = getNumero();
		else if (!tipus.getTeNumero().booleanValue() && tipus.getTeTitol().booleanValue())
			identificador = getTitol();
		if (identificador == null)
			return this.getNumeroDefault();
		else
			return identificador;
	}
	@Transient
	public String getIdentificadorOrdenacio() {
		if (!tipus.getTeNumero().booleanValue() && tipus.getTeTitol().booleanValue()) {
			return getIdentificadorExp();
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici);
			int anyInici = cal.get(Calendar.YEAR);
			return new Integer(anyInici).toString() + new DecimalFormat("0000000000000000000").format(id);
		}
	}
	@Transient
	public String getIdentificadorLimitat() {
		if (getIdentificadorExp() != null && getIdentificadorExp().length() > 100)
			return StringEscapeUtils.escapeHtml(getIdentificadorExp().substring(0, 100) + " (...)");
		else
			return StringEscapeUtils.escapeHtml(getIdentificadorExp());
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

	@Column(name="comentariAnulat", length=1024, nullable=true)
	public String getComentariAnulat() {
		return comentariAnulat;
	}
	public void setComentariAnulat(String comentariAnulat) {
		this.comentariAnulat = comentariAnulat;
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
		ExpedientHelium other = (ExpedientHelium) obj;
		if (processInstanceId == null) {
			if (other.processInstanceId != null)
				return false;
		} else if (!processInstanceId.equals(other.processInstanceId))
			return false;
		return true;
	}
}
