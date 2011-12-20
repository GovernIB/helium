/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa els valors d'una enumeració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_enumeracio_valors")
@org.hibernate.annotations.Table(
		appliesTo = "hel_enumeracio_valors",
		indexes = @Index(name = "hel_enum_id", columnNames = {"enumeracio_id"}))
public class EnumeracioValors implements Serializable {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	int ordre;

	@NotNull
	private Enumeracio enumeracio;

	public EnumeracioValors() {}
	public EnumeracioValors(Enumeracio enumeracio, String codi, String nom) {
		this.enumeracio = enumeracio;
		this.codi = codi;
		this.nom = nom;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_enumeracio_valor")
	@TableGenerator(name="gen_enumeracio_valor", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=64, nullable=false)
	public String getCodi() {
		return this.codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@Column(name="nom", length=255, nullable=false)
	public String getNom() {
		return this.nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	@Column(name="ordre", nullable=false)
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="enumeracio_id")
	@ForeignKey(name="hel_enumeracio_valors_fk")
	public Enumeracio getEnumeracio() {
		return enumeracio;
	}
	public void setEnumeracio(Enumeracio enumeracio) {
		this.enumeracio = enumeracio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((enumeracio == null) ? 0 : enumeracio.hashCode());
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
		EnumeracioValors other = (EnumeracioValors) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (enumeracio == null) {
			if (other.enumeracio != null)
				return false;
		} else if (!enumeracio.equals(other.enumeracio))
			return false;
		return true;
	}

	private static final long serialVersionUID = -4869633305652583392L;

}
