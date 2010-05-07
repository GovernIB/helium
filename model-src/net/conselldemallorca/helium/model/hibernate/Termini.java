/**
 * 
 */
package net.conselldemallorca.helium.model.hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa un termini de la definició
 * de procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Entity
@Table(	name="hel_termini",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "definicio_proces_id"})})
public class Termini implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@MaxLength(255)
	private String descripcio;
	private int anys;
	private int mesos;
	private int dies;
	private boolean laborable;
	private boolean manual = true;

	@NotNull
	private DefinicioProces definicioProces;

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
		net.conselldemallorca.helium.jbpm3.integracio.Termini t = new net.conselldemallorca.helium.jbpm3.integracio.Termini();
		t.setAnys(anys);
		t.setMesos(mesos);
		t.setDies(dies);
		return t.toString();
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

	@ManyToOne(optional=false)
	@JoinColumn(name="definicio_proces_id")
	@ForeignKey(name="hel_defproc_termini_fk")
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
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
		return true;
	}



	private static final long serialVersionUID = 1L;

}
