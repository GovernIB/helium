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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;


/**
 * Objecte de domini que representa una entrada a la taula HEL_PARAMETRE amb els paràmetres de configuració d'Helim guardats
 * en BBDD.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(	name="hel_parametre",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_parametre",
		indexes = {
				@Index(name = "hel_parametre_codi", columnNames = {"codi"})})
public class Parametre implements Serializable, GenericEntity<Long> {

	private Long id;
	/** Codi del paràmetre, sol coincidir amb el codi dins del fitxer de propietats de configuració en el cas que s'hagi de 
	 * recarregar o guardar en memòria.
	 */
	@NotBlank
	@MaxLength(512)
	private String codi;
	

	/** Nom del paràmetre de configuració. */
	@NotBlank
	@MaxLength(255)
	private String nom;
	
	
	/** Descripció del paràmetre de configuració. */
	@NotBlank
	@MaxLength(1024)
	private String descripcio;
	
	
	@MaxLength(255)
	private String valor;

	
	public Parametre() {}

	public static Builder getBuilder(
			String codi,
			String nom,
			String descripcio,
			String valor) {
		return new Builder(
				codi,
				nom,
				descripcio,
				valor);
	}
	
	public static class Builder {
		Parametre built;
		Builder(
				String codi,
				String nom,
				String descripcio,
				String valor) {
			built = new Parametre();
			built.codi = codi;
			built.nom = nom;
			built.descripcio = descripcio;
			built.valor = valor;
		}
		public Parametre build() {
			return built;
		}
	}
	
	public void update(
			String codi,
			String nom,
			String descripcio,
			String valor) {
		this.codi = codi;
		this.nom = nom;
		this.descripcio = descripcio;
		this.valor = valor;
	}
			
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_parametre")
	@TableGenerator(name="gen_parametre", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=512, nullable=false)
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

	@Column(name="descripcio", length=1024)
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	@Column(name="valor", length=255)
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
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
		Parametre other = (Parametre) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
