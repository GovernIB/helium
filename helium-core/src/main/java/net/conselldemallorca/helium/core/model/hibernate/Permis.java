package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

/**
 * Objecte de domini que representa un permis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_permis")
public class Permis implements Serializable, GenericEntity<String> {

	@NotBlank
	@MaxLength(64)
	private String codi;
	@MaxLength(255)
	private String descripcio;

	private Set<Usuari> usuaris = new HashSet<Usuari>();



	public Permis() {}
	public Permis(String codi) {
		this.codi = codi;
	}
	public Permis(String codi, String descripcio) {
		this.codi = codi;
		this.descripcio = descripcio;
	}

	@Id
	@Column(name="codi", length=64)
	public String getCodi() {
		return this.codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	@Transient
	public String getId() {
		return this.codi;
	}

	@Column(name="descripcio", length=255, nullable=true)
	public String getDescripcio() {
		return this.descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	@ManyToMany(mappedBy="permisos")
	public Set<Usuari> getUsuaris() {
		return this.usuaris;
	}
	public void setUsuaris(Set<Usuari> usuaris) {
		this.usuaris = usuaris;
	}
	public void addUsuari(Usuari usuari) {
		getUsuaris().add(usuari);
	}
	public void removeUsuari(Usuari usuari) {
		getUsuaris().remove(usuari);
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
		Permis other = (Permis) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Permis codi=" + codi;
	}

	private static final long serialVersionUID = 1L;

}
