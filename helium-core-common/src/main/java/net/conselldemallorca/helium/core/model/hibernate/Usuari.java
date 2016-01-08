package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

/**
 * Objecte de domini que representa un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="hel_usuari")
public class Usuari implements Serializable, GenericEntity<String> {

	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String contrasenya;
	private boolean actiu = true;

	private Set<Permis> permisos = new HashSet<Permis>();



	public Usuari() {}
	public Usuari(String codi, String contrasenya, boolean actiu) {
		this.codi = codi;
		this.contrasenya = contrasenya;
		this.actiu = actiu;
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

	@Column(name="contrasenya", length=255, nullable=true)
	public String getContrasenya() {
		return this.contrasenya;
	}
	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}

	@Column(name="actiu", nullable=false)
	public Boolean getActiu() {
		return actiu;
	}
	public void setActiu(Boolean actiu) {
		this.actiu = actiu;
	}

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
		name="hel_usuari_permis",
		joinColumns=
			@JoinColumn(name="codi", referencedColumnName="codi"),
		inverseJoinColumns=
			@JoinColumn(name="permis", referencedColumnName="codi")
    )
    @ForeignKey(name="hel_permis_usuari_fk", inverseName="hel_usuari_permis_fk")
	public Set<Permis> getPermisos() {
		return this.permisos;
	}
	public void setPermisos(Set<Permis> permisos) {
		this.permisos = permisos;
	}
	public void addPermis(Permis permis) {
		getPermisos().add(permis);
	}
	public void removePermis(Permis permis) {
		getPermisos().remove(permis);
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
		Usuari other = (Usuari) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Usuari codi=" + codi + " actiu=" + actiu;
	}

	private static final long serialVersionUID = 1L;

}
