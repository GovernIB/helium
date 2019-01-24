/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_expedient_tipus",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "entorn_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_expedient_tipus",
		indexes = @Index(name = "hel_exptip_entorn_i", columnNames = {"entorn_id"}))
public class ExpedientTipus implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@MaxLength(255)
	private String jbpmProcessDefinitionKey;
	@NotNull
	private Boolean teNumero;
	@NotNull
	private Boolean teTitol;
	@NotNull
	private Boolean demanaNumero;
	@NotNull
	private Boolean demanaTitol;
	@MaxLength(255)
	private String expressioNumero;
	private long sequencia = 1;
	private long sequenciaDefault = 1;
	private boolean reiniciarCadaAny;
	private int anyActual = 0;
	@MaxLength(64)
	private String responsableDefecteCodi;
	private boolean restringirPerGrup;
	private boolean tramitacioMassiva;
	private boolean seleccionarAny;
	private boolean ambRetroaccio;
	private boolean reindexacioAsincrona;
	/** Indica si el tipus d'expedient té lligada la informació de les variables, agrupacions i documents
	 * directament o a través de la definició de procesos tal i com s'ha fet fins ara.
	 */
	private boolean ambInfoPropia;
	
	private String diesNoLaborables;
	
	// Integració NOTIB
	private Boolean notibActiu;
	@MaxLength(100)
	private String notibSeuUnitatAdministrativa;
	@MaxLength(100)
	private String notibSeuCodiProcediment;
	@MaxLength(100)
	private String notibSeuOficina;
	@MaxLength(100)
	private String notibSeuLlibre;
	@MaxLength(100)
	private String notibSeuOrgan;
	@MaxLength(256)
	private String notibSeuIdioma;
	@MaxLength(256)
	private String notibAvisTitol;
	@MaxLength(1024)
	private String notibAvisText;
	@MaxLength(200)
	private String notibAvisTextSms;
	@MaxLength(256)
	private String notibOficiTitol;
	@MaxLength(1024)
	private String notibOficiText;
		
	// Integració SISTRA
	//  - Notificacions
	private boolean notificacionsActivades;
	@MaxLength(100)
	private String notificacioOrganCodi;
	@MaxLength(100)
	private String notificacioOficinaCodi;
	@MaxLength(100)
	private String notificacioUnitatAdministrativa;
	@MaxLength(100)
	private String notificacioCodiProcediment;
	@MaxLength(256)
	private String notificacioAvisTitol;
	@MaxLength(1024)
	private String notificacioAvisText;
	@MaxLength(200)
	private String notificacioAvisTextSms;
	@MaxLength(256)
	private String notificacioOficiTitol;
	@MaxLength(1024)
	private String notificacioOficiText;

	//  - Tràmits
	@MaxLength(64)
	private String sistraTramitCodi;
	@MaxLength(2048)
	private String sistraTramitMapeigCamps;
	@MaxLength(2048)
	private String sistraTramitMapeigDocuments;
	@MaxLength(2048)
	private String sistraTramitMapeigAdjunts;

	// Integració FORMS
	@MaxLength(255)
	private String formextUrl;
	@MaxLength(255)
	private String formextUsuari;
	@MaxLength(25)
	private String formextContrasenya;

	@NotNull
	private Entorn entorn;

	private List<Estat> estats = new ArrayList<Estat>();
	private List<MapeigSistra> mapeigSistras = new ArrayList<MapeigSistra>();
	private Set<Expedient> expedients = new HashSet<Expedient>();
	private Set<DefinicioProces> definicionsProces = new HashSet<DefinicioProces>();
	private Set<Consulta> consultes = new HashSet<Consulta>();
	private Set<Domini> dominis = new HashSet<Domini>();
	private Set<Enumeracio> enumeracions = new HashSet<Enumeracio>();

	private SortedMap<Integer, SequenciaAny> sequenciaAny = new TreeMap<Integer, SequenciaAny>();
	private SortedMap<Integer, SequenciaDefaultAny> sequenciaDefaultAny = new TreeMap<Integer, SequenciaDefaultAny>();

	private Set<Camp> camps = new HashSet<Camp>();
	private List<CampAgrupacio> agrupacions = new ArrayList<CampAgrupacio>();
	private List<Document> documents = new ArrayList<Document>();
	private List<Termini> terminis = new ArrayList<Termini>();
	private List<Accio> accions = new ArrayList<Accio>();
	
	private boolean ntiActiu;
	@MaxLength(256)
	private String ntiOrgano;
	@MaxLength(44)
	private String ntiClasificacion;
	@MaxLength(16)
	private String ntiSerieDocumental;
	private boolean arxiuActiu;
	
	public ExpedientTipus() {}
	public ExpedientTipus(String codi, String nom, Entorn entorn) {
		this.codi = codi;
		this.nom = nom;
		this.entorn = entorn;
	}
	public ExpedientTipus(String codi, String nom, String jbpmProcessDefinitionKey, Entorn entorn) {
		this.codi = codi;
		this.nom = nom;
		this.jbpmProcessDefinitionKey = jbpmProcessDefinitionKey;
		this.entorn = entorn;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_expedient_tipus")
	@TableGenerator(name="gen_expedient_tipus", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=64, nullable=false)
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@Column(name="nom", length=255, nullable=false)
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	@Column(name="jbpm_pd_key", length=255, nullable=true)
	public String getJbpmProcessDefinitionKey() {
		return jbpmProcessDefinitionKey;
	}
	public void setJbpmProcessDefinitionKey(String jbpmProcessDefinitionKey) {
		this.jbpmProcessDefinitionKey = jbpmProcessDefinitionKey;
	}

	@Column(name="te_numero")
	public Boolean getTeNumero() {
		if (teNumero == null)
			return Boolean.FALSE;
		return teNumero;
	}
	public void setTeNumero(Boolean teNumero) {
		this.teNumero = teNumero;
	}

	@Column(name="te_titol")
	public Boolean getTeTitol() {
		if (teTitol == null)
			return Boolean.FALSE;
		return teTitol;
	}
	public void setTeTitol(Boolean teTitol) {
		this.teTitol = teTitol;
	}

	@Column(name="demana_numero")
	public Boolean getDemanaNumero() {
		if (demanaNumero == null)
			return Boolean.FALSE;
		return demanaNumero;
	}
	public void setDemanaNumero(Boolean demanaNumero) {
		this.demanaNumero = demanaNumero;
	}

	@Column(name="demana_titol")
	public Boolean getDemanaTitol() {
		if (demanaTitol == null)
			return Boolean.FALSE;
		return demanaTitol;
	}
	public void setDemanaTitol(Boolean demanaTitol) {
		this.demanaTitol = demanaTitol;
	}

	@Column(name="expressio_numero", length=255)
	public String getExpressioNumero() {
		return expressioNumero;
	}
	public void setExpressioNumero(String expressioNumero) {
		this.expressioNumero = expressioNumero;
	}

	@Column(name="sequencia")
	public long getSequencia() {
		return sequencia;
	}
	public void setSequencia(long sequencia) {
		this.sequencia = sequencia;
	}

	@Column(name="sequencia_def")
	public long getSequenciaDefault() {
		return sequenciaDefault;
	}
	public void setSequenciaDefault(long sequenciaDefault) {
		this.sequenciaDefault = sequenciaDefault;
	}

	@Column(name="reiniciar_anual")
	public boolean isReiniciarCadaAny() {
		return reiniciarCadaAny;
	}
	public void setReiniciarCadaAny(boolean reiniciarCadaAny) {
		this.reiniciarCadaAny = reiniciarCadaAny;
	}

	@Column(name="any_actual")
	public int getAnyActual() {
		return anyActual;
	}
	public void setAnyActual(int anyActual) {
		this.anyActual = anyActual;
	}

	@Column(name="respdefault_codi", length=64)
	public String getResponsableDefecteCodi() {
		return responsableDefecteCodi;
	}
	public void setResponsableDefecteCodi(String responsableDefecteCodi) {
		this.responsableDefecteCodi = responsableDefecteCodi;
	}

	@Column(name="restringir_grup")
	public boolean isRestringirPerGrup() {
		return restringirPerGrup;
	}
	public void setRestringirPerGrup(boolean restringirPerGrup) {
		this.restringirPerGrup = restringirPerGrup;
	}

	@Column(name="tram_massiva")
	public boolean isTramitacioMassiva() {
		return tramitacioMassiva;
	}
	public void setTramitacioMassiva(boolean tramitacioMassiva) {
		this.tramitacioMassiva = tramitacioMassiva;
	}

	@Column(name="seleccionar_any")
	public boolean isSeleccionarAny() {
		return seleccionarAny;
	}
	public void setSeleccionarAny(boolean seleccionarAny) {
		this.seleccionarAny = seleccionarAny;
	}

	@Column(name="amb_retroaccio")
	public boolean isAmbRetroaccio() {
		return ambRetroaccio;
	}
	public void setAmbRetroaccio(boolean ambRetroaccio) {
		this.ambRetroaccio = ambRetroaccio;
	}

	@Column(name="amb_info_propia")
	public boolean isAmbInfoPropia() {
		return ambInfoPropia;
	}
	public void setAmbInfoPropia(boolean ambInfoPropia) {
		this.ambInfoPropia = ambInfoPropia;
	}

	@Column(name="reindexacio_asincrona")
	public boolean isReindexacioAsincrona() {
		return reindexacioAsincrona;
	}
	public void setReindexacioAsincrona(boolean reindexacioAsincrona) {
		this.reindexacioAsincrona = reindexacioAsincrona;
	}
	@Column(name="dies_no_labs")
	public String getDiesNoLaborables() {
		return diesNoLaborables;
	}
	public void setDiesNoLaborables(String diesNoLaborables) {
		this.diesNoLaborables = diesNoLaborables;
	}
	
	@Column(name="notificacions_activades")
	public boolean isNotificacionsActivades() {
		return notificacionsActivades;
	}
	public void setNotificacionsActivades(boolean notificacionsActivades) {
		this.notificacionsActivades = notificacionsActivades;
	}
	
	@Column(name="notificacio_organcodi", length = 100)
	public String getNotificacioOrganCodi() {
		return notificacioOrganCodi;
	}
	public void setNotificacioOrganCodi(String notificacioOrganCodi) {
		this.notificacioOrganCodi = notificacioOrganCodi;
	}
	
	@Column(name="notificacio_oficinacodi", length = 100)
	public String getNotificacioOficinaCodi() {
		return notificacioOficinaCodi;
	}
	public void setNotificacioOficinaCodi(String notificacioOficinaCodi) {
		this.notificacioOficinaCodi = notificacioOficinaCodi;
	}
	
	@Column(name="notificacio_unitatadmin", length = 100)
	public String getNotificacioUnitatAdministrativa() {
		return notificacioUnitatAdministrativa;
	}
	public void setNotificacioUnitatAdministrativa(String notificacioUnitatAdministrativa) {
		this.notificacioUnitatAdministrativa = notificacioUnitatAdministrativa;
	}
	
	@Column(name="notificacio_codproc", length = 100)
	public String getNotificacioCodiProcediment() {
		return notificacioCodiProcediment;
	}
	public void setNotificacioCodiProcediment(String notificacioCodiProcediment) {
		this.notificacioCodiProcediment = notificacioCodiProcediment;
	}
	
	@Column(name="notificacio_avistitol", length = 256)
	public String getNotificacioAvisTitol() {
		return notificacioAvisTitol;
	}
	public void setNotificacioAvisTitol(String notificacioAvisTitol) {
		this.notificacioAvisTitol = notificacioAvisTitol;
	}
	
	@Column(name="notificacio_avistext", length = 1024)
	public String getNotificacioAvisText() {
		return notificacioAvisText;
	}
	public void setNotificacioAvisText(String notificacioAvisText) {
		this.notificacioAvisText = notificacioAvisText;
	}
	
	@Column(name="notificacio_avistextsms", length = 200)
	public String getNotificacioAvisTextSms() {
		return notificacioAvisTextSms;
	}
	public void setNotificacioAvisTextSms(String notificacioAvisTextSms) {
		this.notificacioAvisTextSms = notificacioAvisTextSms;
	}
	
	@Column(name="notificacio_oficititol", length = 256)
	public String getNotificacioOficiTitol() {
		return notificacioOficiTitol;
	}
	public void setNotificacioOficiTitol(String notificacioOficiTitol) {
		this.notificacioOficiTitol = notificacioOficiTitol;
	}
	
	@Column(name="notificacio_oficitext", length = 1024)
	public String getNotificacioOficiText() {
		return notificacioOficiText;
	}
	public void setNotificacioOficiText(String notificacioOficiText) {
		this.notificacioOficiText = notificacioOficiText;
	}
	
	@Column(name="sistra_codtra", length=64, unique=true)
	public String getSistraTramitCodi() {
		return sistraTramitCodi;
	}
	public void setSistraTramitCodi(String sistraTramitCodi) {
		this.sistraTramitCodi = sistraTramitCodi;
	}

	@Column(name="sistra_mapcamps", length=2048)
	public String getSistraTramitMapeigCamps() {
		return sistraTramitMapeigCamps;
	}
	public void setSistraTramitMapeigCamps(String sistraTramitMapeigCamps) {
		this.sistraTramitMapeigCamps = sistraTramitMapeigCamps;
	}

	@Column(name="sistra_mapdocs", length=2048)
	public String getSistraTramitMapeigDocuments() {
		return sistraTramitMapeigDocuments;
	}
	public void setSistraTramitMapeigDocuments(String sistraTramitMapeigDocuments) {
		this.sistraTramitMapeigDocuments = sistraTramitMapeigDocuments;
	}

	@Column(name="sistra_mapadj", length=2048)
	public String getSistraTramitMapeigAdjunts() {
		return sistraTramitMapeigAdjunts;
	}
	public void setSistraTramitMapeigAdjunts(String sistraTramitMapeigAdjunts) {
		this.sistraTramitMapeigAdjunts = sistraTramitMapeigAdjunts;
	}

	@Column(name="notib_actiu")
	public Boolean getNotibActiu() {
		return notibActiu;
	}
	public void setNotibActiu(Boolean notibActiu) {
		this.notibActiu = notibActiu;
	}
	
	@Column(name="notib_seu_unitatadmin", length = 100)
	public String getNotibSeuUnitatAdministrativa() {
		return notibSeuUnitatAdministrativa;
	}
	public void setNotibSeuUnitatAdministrativa(String notibSeuUnitatAdministrativa) {
		this.notibSeuUnitatAdministrativa = notibSeuUnitatAdministrativa;
	}
	
	@Column(name="notib_seu_codiprocediment", length = 100)
	public String getNotibSeuCodiProcediment() {
		return notibSeuCodiProcediment;
	}
	public void setNotibSeuCodiProcediment(String notibSeuCodiProcediment) {
		this.notibSeuCodiProcediment = notibSeuCodiProcediment;
	}
	
	@Column(name="notib_seu_oficina", length = 100)
	public String getNotibSeuOficina() {
		return notibSeuOficina;
	}
	public void setNotibSeuOficina(String notibSeuOficina) {
		this.notibSeuOficina = notibSeuOficina;
	}
	
	@Column(name="notib_seu_llibre", length = 100)
	public String getNotibSeuLlibre() {
		return notibSeuLlibre;
	}
	public void setNotibSeuLlibre(String notibSeuLlibre) {
		this.notibSeuLlibre = notibSeuLlibre;
	}
	
	@Column(name="notib_seu_organ", length = 100)
	public String getNotibSeuOrgan() {
		return notibSeuOrgan;
	}
	public void setNotibSeuOrgan(String notibSeuOrgan) {
		this.notibSeuOrgan = notibSeuOrgan;
	}
	
	@Column(name="notib_seu_idioma", length = 10)
	public String getNotibSeuIdioma() {
		return notibSeuIdioma;
	}
	public void setNotibSeuIdioma(String notibSeuIdioma) {
		this.notibSeuIdioma = notibSeuIdioma;
	}
	
	@Column(name="notib_avistitol", length = 100)
	public String getNotibAvisTitol() {
		return notibAvisTitol;
	}
	public void setNotibAvisTitol(String notibAvisTitol) {
		this.notibAvisTitol = notibAvisTitol;
	}
	
	@Column(name="notib_avistext", length = 1024)
	public String getNotibAvisText() {
		return notibAvisText;
	}
	public void setNotibAvisText(String notibAvisText) {
		this.notibAvisText = notibAvisText;
	}
	
	@Column(name="notib_avistextsms", length = 200)
	public String getNotibAvisTextSms() {
		return notibAvisTextSms;
	}
	public void setNotibAvisTextSms(String notibAvisTextSms) {
		this.notibAvisTextSms = notibAvisTextSms;
	}
	
	@Column(name="notib_oficititol", length = 256)
	public String getNotibOficiTitol() {
		return notibOficiTitol;
	}
	public void setNotibOficiTitol(String notibOficiTitol) {
		this.notibOficiTitol = notibOficiTitol;
	}
	
	@Column(name="notib_oficitext", length = 1024)
	public String getNotibOficiText() {
		return notibOficiText;
	}
	public void setNotibOficiText(String notibOficiText) {
		this.notibOficiText = notibOficiText;
	}
	
	@Column(name="formext_url", length=255)
	public String getFormextUrl() {
		return formextUrl;
	}
	public void setFormextUrl(String formextUrl) {
		this.formextUrl = formextUrl;
	}

	@Column(name="formext_usuari", length=255)
	public String getFormextUsuari() {
		return formextUsuari;
	}
	public void setFormextUsuari(String formextUsuari) {
		this.formextUsuari = formextUsuari;
	}

	@Column(name="formext_contrasenya", length=255)
	public String getFormextContrasenya() {
		return formextContrasenya;
	}
	public void setFormextContrasenya(String formextContrasenya) {
		this.formextContrasenya = formextContrasenya;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="entorn_id")
	@ForeignKey(name="hel_entorn_exptipus_fk")
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@OneToMany(mappedBy="expedientTipus", fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@OrderBy("ordre asc")
	@Fetch(value = FetchMode.SUBSELECT)
	public List<Estat> getEstats() {
		return this.estats;
	}
	public void setEstats(List<Estat> estats) {
		this.estats = estats;
	}
	public void addEstat(Estat estat) {
		getEstats().add(estat);
	}
	public void removeEstat(Estat estat) {
		getEstats().remove(estat);
	}

	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL})
	@OrderBy("codiHelium asc")
	public List<MapeigSistra> getMapeigSistras() {
		return this.mapeigSistras;
	}
	public void setMapeigSistras(List<MapeigSistra> mapeigSistras) {
		this.mapeigSistras = mapeigSistras;
	}
	public void addMapeigSistras(MapeigSistra mapeigSistra) {
		getMapeigSistras().add(mapeigSistra);
	}
	public void removeMapeigSistras(MapeigSistra mapeigSistra) {
		getMapeigSistras().remove(mapeigSistra);
	}

	@OneToMany(mappedBy="tipus")
	public Set<Expedient> getExpedients() {
		return this.expedients;
	}
	public void setExpedients(Set<Expedient> expedients) {
		this.expedients = expedients;
	}
	public void addExpedient(Expedient expedient) {
		getExpedients().add(expedient);
	}
	public void removeExpedient(Expedient expedient) {
		getExpedients().remove(expedient);
	}

	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL})
	public Set<DefinicioProces> getDefinicionsProces() {
		return this.definicionsProces;
	}
	public void setDefinicionsProces(Set<DefinicioProces> definicionsProces) {
		this.definicionsProces = definicionsProces;
	}
	public void addDefinicioProces(DefinicioProces definicioProces) {
		getDefinicionsProces().add(definicioProces);
	}
	public void removeDefinicioProces(DefinicioProces definicioProces) {
		getDefinicionsProces().remove(definicioProces);
	}

	@OneToMany(mappedBy="expedientTipus", fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@Fetch(value = FetchMode.SUBSELECT)
	@OrderBy("ordre asc")
	public Set<Consulta> getConsultes() {
		return this.consultes;
	}
	public void setConsultes(Set<Consulta> consultes) {
		this.consultes = consultes;
	}
	public void addConsulta(Consulta consulta) {
		getConsultes().add(consulta);
	}
	public void removeConsulta(Consulta consulta) {
		getConsultes().remove(consulta);
	}

	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	public Set<Domini> getDominis() {
		return this.dominis;
	}
	public void setDominis(Set<Domini> dominis) {
		this.dominis = dominis;
	}
	public void addDomini(Domini domini) {
		getDominis().add(domini);
	}
	public void removeDomini(Domini domini) {
		getDominis().remove(domini);
	}

	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL})
	public Set<Enumeracio> getEnumeracions() {
		return this.enumeracions;
	}
	public void setEnumeracions(Set<Enumeracio> enumeracions) {
		this.enumeracions = enumeracions;
	}
	public void addEnumeracio(Enumeracio enumeracio) {
		getEnumeracions().add(enumeracio);
	}
	public void removeEnumeracio(Enumeracio enumeracio) {
		getEnumeracions().remove(enumeracio);
	}

	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	@MapKey(name = "any")
	@Sort(type = SortType.NATURAL)
	public SortedMap<Integer, SequenciaAny> getSequenciaAny() {
		return sequenciaAny;
	}
	public void setSequenciaAny(SortedMap<Integer, SequenciaAny> sequenciaAny) {
		this.sequenciaAny = sequenciaAny;
	}

	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	@MapKey(name = "any")
	@Sort(type = SortType.NATURAL)
	public SortedMap<Integer, SequenciaDefaultAny> getSequenciaDefaultAny() {
		return sequenciaDefaultAny;
	}
	public void setSequenciaDefaultAny(SortedMap<Integer, SequenciaDefaultAny> sequenciaDefaultAny) {
		this.sequenciaDefaultAny = sequenciaDefaultAny;
	}

	public void updateSequencia(Integer any, long increment) {
		if (any == null) any = Calendar.getInstance().get(Calendar.YEAR);
		if (this.isReiniciarCadaAny()) {
			if (this.getSequenciaAny().containsKey(any)) {
				this.getSequenciaAny().get(any).setSequencia(this.getSequenciaAny().get(any).getSequencia() + increment);
			} else {
				SequenciaAny sa = new SequenciaAny(this, any, increment);
				this.getSequenciaAny().put(any, sa);
			}
		} else {
			this.sequencia = this.sequencia + increment;
		}
	}
	
	public void updateSequenciaDefault(Integer any, long increment) {
		if (any == null) any = Calendar.getInstance().get(Calendar.YEAR);
		if (this.isReiniciarCadaAny()) {
			if (this.getSequenciaDefaultAny().containsKey(any)) {
				this.getSequenciaDefaultAny().get(any).setSequenciaDefault(this.getSequenciaDefaultAny().get(any).getSequenciaDefault() + increment);
			} else {
				SequenciaDefaultAny sda = new SequenciaDefaultAny(this, any, increment);
				this.getSequenciaDefaultAny().put(any, sda);
			}
		} else {
			this.sequenciaDefault = this.sequenciaDefault + increment;
		}
	}
	
	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL})
	public Set<Camp> getCamps() {
		return this.camps;
	}
	public void setCamps(Set<Camp> camps) {
		this.camps= camps;
	}
	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL})
	@OrderBy("ordre asc")
	public List<CampAgrupacio> getAgrupacions() {
		return this.agrupacions;
	}	
	public void setAgrupacions(List<CampAgrupacio> agrupacions) {
		this.agrupacions = agrupacions;
	}
	
	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL})
	public List<Document> getDocuments() {
		return this.documents;
	}	
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL})
	public List<Termini> getTerminis() {
		return this.terminis;
	}	
	public void setTerminis(List<Termini> terminis) {
		this.terminis = terminis;
	}
	
	@OneToMany(mappedBy="expedientTipus", cascade={CascadeType.ALL})
	public List<Accio> getAccions() {
		return this.accions;
	}	
	public void setAccions(List<Accio> accions) {
		this.accions = accions;
	}
	
	@Column(name="nti_actiu")
	public boolean isNtiActiu() {
		return ntiActiu;
	}
	public void setNtiActiu(boolean ntiActiu) {
		this.ntiActiu = ntiActiu;
	}
	
	@Column(name="nti_organo", length=256)
	public String getNtiOrgano() {
		return ntiOrgano;
	}
	public void setNtiOrgano(String ntiOrgano) {
		this.ntiOrgano = ntiOrgano;
	}

	@Column(name="nti_clasificacion", length=44)
	public String getNtiClasificacion() {
		return ntiClasificacion;
	}
	public void setNtiClasificacion(String ntiClasificacion) {
		this.ntiClasificacion = ntiClasificacion;
	}

	@Column(name="nti_seriedocumental", length=16)
	public String getNtiSerieDocumental() {
		return ntiSerieDocumental;
	}
	public void setNtiSerieDocumental(String ntiSerieDocumental) {
		this.ntiSerieDocumental = ntiSerieDocumental;
	}

	@Column(name="arxiu_actiu")
	public boolean isArxiuActiu() {
		return arxiuActiu;
	}
	public void setArxiuActiu(boolean arxiuActiu) {
		this.arxiuActiu = arxiuActiu;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
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
		ExpedientTipus other = (ExpedientTipus) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
