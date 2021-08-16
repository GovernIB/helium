/**
 * 
 */
package es.caib.helium.persist.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Objecte de domini que representa una definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(
		name="hel_definicio_proces",
		indexes = {
				@Index(name = "hel_defproc_entorn_i", columnList = "entorn_id"),
				@Index(name = "hel_defproc_exptip_i", columnList = "expedient_tipus_id"),
				@Index(name = "hel_defproc_jbpmid_i", columnList = "jbpm_id")
		}
)
public class DefinicioProces implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@Size(max = 255)
	private String jbpmId;
	@NotBlank
	@Size(max = 255)
	private String jbpmKey;
	private int versio = -1;
	@Size(max = 64)
	private String etiqueta;
	@NotNull
	private Date dataCreacio = new Date();

	@NotNull
	private Entorn entorn;
	private ExpedientTipus expedientTipus;

	private Set<Tasca> tasques = new HashSet<Tasca>();
	private Set<Camp> camps = new HashSet<Camp>();
	private Set<Document> documents = new HashSet<Document>();
	private Set<Termini> terminis = new HashSet<Termini>();
	private List<CampAgrupacio> agrupacions = new ArrayList<CampAgrupacio>();
	private Set<Accio> accions = new HashSet<Accio>();



	public DefinicioProces() {}
	public DefinicioProces(String jbpmId, String jbpmKey, int versio, Entorn entorn) {
		this.jbpmId = jbpmId;
		this.jbpmKey = jbpmKey;
		this.versio = versio;
		this.entorn = entorn;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_definicio_proces")
	@TableGenerator(name="gen_definicio_proces", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="jbpm_id", length=255, nullable=false, unique=true)
	public String getJbpmId() {
		return jbpmId;
	}
	public void setJbpmId(String jbpmId) {
		this.jbpmId = jbpmId;
	}

	@Column(name="jbpm_key", length=255, nullable=false)
	public String getJbpmKey() {
		return jbpmKey;
	}
	public void setJbpmKey(String jbpmKey) {
		this.jbpmKey = jbpmKey;
	}

	@Column(name="versio")
	public int getVersio() {
		return versio;
	}
	public void setVersio(int versio) {
		this.versio = versio;
	}

	@Column(name="etiqueta", length=64)
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	@Column(name="datacreacio", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}

	@ManyToOne(optional=false)
	@JoinColumn(
			name="entorn_id",
			foreignKey = @ForeignKey(name="hel_entorn_defproc_fk"))
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@ManyToOne
	@JoinColumn(
			name="expedient_tipus_id",
			foreignKey = @ForeignKey(name="hel_exptip_defproc_fk"))
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	@OneToMany(mappedBy="definicioProces", cascade={CascadeType.ALL})
	public Set<Tasca> getTasques() {
		return this.tasques;
	}
	public void setTasques(Set<Tasca> tasques) {
		this.tasques = tasques;
	}
	public void addTasca(Tasca tasca) {
		getTasques().add(tasca);
	}
	public void removeTasca(Tasca tasca) {
		getTasques().remove(tasca);
	}

	@OneToMany(mappedBy="definicioProces", cascade={CascadeType.ALL})
	public Set<Camp> getCamps() {
		return this.camps;
	}
	public void setCamps(Set<Camp> camps) {
		this.camps= camps;
	}
	public void addCamp(Camp camp) {
		getCamps().add(camp);
	}
	public void removeCamp(Camp camp) {
		getCamps().remove(camp);
	}

	@OneToMany(mappedBy="definicioProces", cascade={CascadeType.ALL})
	public Set<Document> getDocuments() {
		return this.documents;
	}
	public void setDocuments(Set<Document> documents) {
		this.documents= documents;
	}
	public void addDocument(Document document) {
		getDocuments().add(document);
	}
	public void removeDocument(Document document) {
		getDocuments().remove(document);
	}

	@OneToMany(mappedBy="definicioProces", cascade={CascadeType.ALL})
	public Set<Termini> getTerminis() {
		return this.terminis;
	}
	public void setTerminis(Set<Termini> terminis) {
		this.terminis= terminis;
	}
	public void addTermini(Termini termini) {
		getTerminis().add(termini);
	}
	public void removeTermini(Termini termini) {
		getTerminis().remove(termini);
	}

	@OneToMany(mappedBy="definicioProces", cascade={CascadeType.ALL})
	@OrderBy("ordre asc")
	public List<CampAgrupacio> getAgrupacions() {
		return this.agrupacions;
	}
	public void setAgrupacions(List<CampAgrupacio> agrupacions) {
		this.agrupacions = agrupacions;
	}
	public void addAgrupacio(CampAgrupacio agrupacio) {
		getAgrupacions().add(agrupacio);
	}
	public void removeAgrupacio(CampAgrupacio agrupacio) {
		getAgrupacions().remove(agrupacio);
	}

	@OneToMany(mappedBy="definicioProces", cascade={CascadeType.ALL})
	public Set<Accio> getAccions() {
		return this.accions;
	}
	public void setAccions(Set<Accio> accions) {
		this.accions = accions;
	}
	public void addAccio(Accio accio) {
		getAccions().add(accio);
	}
	public void removeAccio(Accio accio) {
		getAccions().remove(accio);
	}

	@Transient
	public String getIdPerMostrar() {
		if (etiqueta != null) {
			return etiqueta + " (" + jbpmKey + " v." + versio + ")";
		} else {
			return jbpmKey + " v." + versio;
		}
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((jbpmId == null) ? 0 : jbpmId.hashCode());
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
		DefinicioProces other = (DefinicioProces) obj;
		if (jbpmId == null) {
			if (other.jbpmId != null)
				return false;
		} else if (!jbpmId.equals(other.jbpmId))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
