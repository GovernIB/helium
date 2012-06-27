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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa un document de la definició
 * de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_accio",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "definicio_proces_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_accio",
		indexes = @Index(name = "hel_accio_defproc_i", columnNames = {"definicio_proces_id"}))
public class Accio implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@MaxLength(255)
	private String descripcio;
	@NotBlank
	@MaxLength(255)
	private String jbpmAction;
	private boolean publica;
	private boolean oculta;
	private String cron;



	@NotNull
	private DefinicioProces definicioProces;



	public Accio() {}
	public Accio(DefinicioProces definicioProces, String codi, String nom, String jbpmAction) {
		this.definicioProces = definicioProces;
		this.codi = codi;
		this.nom = nom;
		this.jbpmAction = jbpmAction;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_document")
	@TableGenerator(name="gen_document", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@Column(name="jbpm_action", length=255, nullable=false)
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}

	@Column(name="publica")
	public boolean isPublica() {
		return publica;
	}
	public void setPublica(boolean publica) {
		this.publica = publica;
	}

	@Column(name="oculta")
	public boolean isOculta() {
		return oculta;
	}
	public void setOculta(boolean oculta) {
		this.oculta = oculta;
	}

	@Column(name="cron", length=255)
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="definicio_proces_id")
	@ForeignKey(name="hel_defproc_accio_fk")
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
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
		Accio other = (Accio) obj;
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
