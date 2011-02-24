/**
 * 
 */
package net.conselldemallorca.helium.model.hibernate;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
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

	private Estat estat;
	@NotNull
	private ExpedientTipus tipus;
	@NotNull
	private Entorn entorn;

	private Set<Expedient> relacionsOrigen = new HashSet<Expedient>();
	private Set<Expedient> relacionsDesti = new HashSet<Expedient>();

	private Set<Alerta> alertes = new HashSet<Alerta>();



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
	public void setAlertes(Set<Alerta> alertes) {
		this.alertes = alertes;
	}
	public void addAlerta(Alerta alerta) {
		getAlertes().add(alerta);
	}
	public void removeAlerta(Alerta alerta) {
		getAlertes().remove(alerta);
	}

	@Transient
	public String getNumeroIdentificador() {
		if (tipus.getTeNumero().booleanValue())
			return getNumero();
		return this.getNumeroDefault();
	}
	@Transient
	public String getIdentificador() {
		if (tipus.getTeNumero().booleanValue() && tipus.getTeTitol().booleanValue())
			return "[" + getNumero() + "] " + getTitol();
		else if (tipus.getTeNumero().booleanValue() && !tipus.getTeTitol().booleanValue())
			return getNumero();
		else if (!tipus.getTeNumero().booleanValue() && tipus.getTeTitol().booleanValue())
			return getTitol();
		return this.getNumeroDefault();
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
