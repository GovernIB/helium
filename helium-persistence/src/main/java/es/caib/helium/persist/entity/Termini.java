/**
 * 
 */
package es.caib.helium.persist.entity;

import es.caib.helium.logic.intf.util.TerminiStringUtil;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Objecte de domini que representa un termini de la definició
 * de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_termini",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "definicio_proces_id", "expedient_tipus_id"})},
		indexes = {
				@Index(name = "hel_termini_defproc_i", columnList = "definicio_proces_id"),
				@Index(name = "hel_termini_exptip_i", columnList = "expedient_tipus_id")
		}
)
public class Termini implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@Size(max = 64)
	private String codi;
	@NotBlank
	@Size(max = 255)
	private String nom;
	@Size(max = 255)
	private String descripcio;
	private boolean duradaPredefinida;
	private int anys;
	private int mesos;
	private int dies;
	private boolean laborable;
	private boolean manual = true;
	private Integer diesPrevisAvis;
	private boolean alertaPrevia;
	private boolean alertaFinal;
	private boolean alertaCompletat;

	private DefinicioProces definicioProces;
	private ExpedientTipus expedientTipus;

	private Set<TerminiIniciat> iniciats = new HashSet<TerminiIniciat>();



	public Termini() {}
	public Termini(DefinicioProces definicioProces, String codi, String nom, int anys, int mesos, int dies, boolean laborable) {
		this.definicioProces = definicioProces;
		this.codi = codi;
		this.nom = nom;
		this.anys = anys;
		this.mesos = mesos;
		this.dies = dies;
		this.laborable = laborable;
	}
	public Termini(ExpedientTipus expedientTipus, String codi, String nom, int anys, int mesos, int dies, boolean laborable) {
		this.expedientTipus = expedientTipus;
		this.codi = codi;
		this.nom = nom;
		this.anys = anys;
		this.mesos = mesos;
		this.dies = dies;
		this.laborable = laborable;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_termini")
	@TableGenerator(name="gen_termini", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@Column(name="descripcio", length=255)
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	@Column(name="durada_predef")
	public boolean isDuradaPredefinida() {
		return duradaPredefinida;
	}
	public void setDuradaPredefinida(boolean duradaPredefinida) {
		this.duradaPredefinida = duradaPredefinida;
	}

	@Column(name="anys")
	public int getAnys() {
		return anys;
	}
	public void setAnys(int anys) {
		this.anys = anys;
	}

	@Column(name="mesos")
	public int getMesos() {
		return mesos;
	}
	public void setMesos(int mesos) {
		this.mesos = mesos;
	}

	@Transient
	public String getDurada() {
		TerminiStringUtil tsu = new TerminiStringUtil(
				anys,
				mesos,
				dies);
		if (dies > 0)
			return tsu.toString() + ((laborable) ? " laborables" : " naturals");
		else
			return tsu.toString();
	}

	@Column(name="dies")
	public int getDies() {
		return dies;
	}
	public void setDies(int dies) {
		this.dies = dies;
	}

	@Column(name="laborable")
	public boolean isLaborable() {
		return laborable;
	}
	public void setLaborable(boolean laborable) {
		this.laborable = laborable;
	}

	@Column(name="manual")
	public boolean isManual() {
		return manual;
	}
	public void setManual(boolean manual) {
		this.manual = manual;
	}

	@Column(name="dies_previs_avis")
	public Integer getDiesPrevisAvis() {
		return diesPrevisAvis;
	}
	public void setDiesPrevisAvis(Integer diesPrevisAvis) {
		this.diesPrevisAvis = diesPrevisAvis;
	}

	@Column(name="alerta_previa")
	public boolean isAlertaPrevia() {
		return alertaPrevia;
	}
	public void setAlertaPrevia(boolean alertaPrevia) {
		this.alertaPrevia = alertaPrevia;
	}

	@Column(name="alerta_final")
	public boolean isAlertaFinal() {
		return alertaFinal;
	}
	public void setAlertaFinal(boolean alertaFinal) {
		this.alertaFinal = alertaFinal;
	}

	@Column(name="alerta_completat")
	public boolean isAlertaCompletat() {
		return alertaCompletat;
	}
	public void setAlertaCompletat(boolean alertaCompletat) {
		this.alertaCompletat = alertaCompletat;
	}
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@JoinColumn(
			name="definicio_proces_id",
			foreignKey = @ForeignKey(name="hel_defproc_termini_fk"))
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}
	
	@ManyToOne(optional=true)
	@JoinColumn(
			name="expedient_tipus_id",
			foreignKey = @ForeignKey(name="hel_exptip_termini_fk"))
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	@OneToMany(mappedBy="termini")
	public Set<TerminiIniciat> getIniciats() {
		return this.iniciats;
	}
	public void setIniciats(Set<TerminiIniciat> iniciats) {
		this.iniciats = iniciats;
	}
	public void addIniciat(TerminiIniciat iniciat) {
		getIniciats().add(iniciat);
	}
	public void removeIniciat(TerminiIniciat iniciat) {
		getIniciats().remove(iniciat);
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result
				+ ((definicioProces == null) ? 0 : definicioProces.hashCode());
		result = prime * result
				+ ((expedientTipus == null) ? 0 : expedientTipus.hashCode());
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
		Termini other = (Termini) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (definicioProces == null) {
			if (other.definicioProces != null)
				return false;
		} else if (!definicioProces.equals(other.definicioProces))
			return false;
		if (expedientTipus == null) {
			if (other.expedientTipus != null)
				return false;
		} else if (!expedientTipus.equals(other.expedientTipus))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}