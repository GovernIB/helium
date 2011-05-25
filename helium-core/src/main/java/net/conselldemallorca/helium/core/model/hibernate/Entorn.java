/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import net.conselldemallorca.helium.core.security.acl.SecureObject;
import net.conselldemallorca.helium.core.security.audit.Auditable;

import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

/**
 * Objecte de domini que representa un entorn.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_entorn")
public class Entorn implements Serializable, GenericEntity<Long>, Auditable, SecureObject {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@MaxLength(255)
	private String descripcio;
	private boolean actiu;

	private Set<DefinicioProces> definicionsProces = new HashSet<DefinicioProces>();
	private Set<ExpedientTipus> expedientTipus = new HashSet<ExpedientTipus>();
	private Set<Expedient> expedients = new HashSet<Expedient>();
	private Set<Domini> dominis = new HashSet<Domini>();
	private Set<Enumeracio> enumeracions = new HashSet<Enumeracio>();
	private Set<Consulta> consultes = new HashSet<Consulta>();



	public Entorn() {}
	public Entorn(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_entorn")
	@TableGenerator(name="gen_entorn", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=64, nullable=false, unique=true)
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

	@Column(name="actiu")
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}

	@OneToMany(mappedBy="entorn")
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

	@OneToMany(mappedBy="entorn")
	public Set<ExpedientTipus> getExpedientTipus() {
		return this.expedientTipus;
	}
	public void setExpedientTipus(Set<ExpedientTipus> expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	public void addExpedientTipus(ExpedientTipus expedientTipus) {
		getExpedientTipus().add(expedientTipus);
	}
	public void removeExpedientTipus(ExpedientTipus expedientTipus) {
		getExpedientTipus().remove(expedientTipus);
	}

	@OneToMany(mappedBy="entorn")
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

	@OneToMany(mappedBy="entorn")
	public Set<Domini> getDominis() {
		return this.dominis;
	}
	public void setDominis(Set<Domini> dominis) {
		this.dominis = dominis;
	}
	public void addDomini(Domini domini) {
		getDominis().add(domini);
	}
	public void removeDomini(Enumeracio domini) {
		getDominis().remove(domini);
	}

	@OneToMany(mappedBy="entorn")
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

	@OneToMany(mappedBy="entorn")
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



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
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
		Entorn other = (Entorn) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Entorn id=" + id + ", nom=" + nom;
	}

	private static final long serialVersionUID = 1L;

}
