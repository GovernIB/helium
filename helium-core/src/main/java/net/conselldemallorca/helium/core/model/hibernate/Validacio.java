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
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una validació de dades
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_validacio")
@org.hibernate.annotations.Table(
		appliesTo = "hel_validacio",
		indexes = {
				@Index(name = "hel_validacio_tasca_i", columnNames = {"tasca_id"}),
				@Index(name = "hel_validacio_camp_i", columnNames = {"camp_id"})})
public class Validacio implements Serializable, GenericEntity<Long> {

	private Long id;
	@MaxLength(255)
	private String nom;
	@NotNull
	@MaxLength(1024)
	private String expressio;
	@NotNull
	@MaxLength(255)
	private String missatge;
	int ordre;

	private Tasca tasca;
	private Camp camp;



	public Validacio() {}
	public Validacio(Tasca tasca, String expressio, String missatge) {
		this.expressio = expressio;
		this.missatge = missatge;
		this.tasca = tasca;
	}
	public Validacio(Camp camp, String expressio, String missatge) {
		this.expressio = expressio;
		this.missatge = missatge;
		this.camp = camp;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_validacio")
	@TableGenerator(name="gen_validacio", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="nom", length=255)
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	@Column(name="expressio", length=1024, nullable=false)
	public String getExpressio() {
		return expressio;
	}
	public void setExpressio(String expressio) {
		this.expressio = expressio;
	}

	@Column(name="missatge", length=255, nullable=false)
	public String getMissatge() {
		return missatge;
	}
	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}

	@Column(name="ordre", nullable=false)
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="tasca_id")
	@ForeignKey(name="hel_tasca_validacio_fk")
	public Tasca getTasca() {
		return tasca;
	}
	public void setTasca(Tasca tasca) {
		this.tasca = tasca;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="camp_id")
	@ForeignKey(name="hel_camp_validacio_fk")
	public Camp getCamp() {
		return camp;
	}
	public void setCamp(Camp camp) {
		this.camp = camp;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((camp == null) ? 0 : camp.hashCode());
		result = prime * result
				+ ((expressio == null) ? 0 : expressio.hashCode());
		result = prime * result + ((tasca == null) ? 0 : tasca.hashCode());
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
		Validacio other = (Validacio) obj;
		if (camp == null) {
			if (other.camp != null)
				return false;
		} else if (!camp.equals(other.camp))
			return false;
		if (expressio == null) {
			if (other.expressio != null)
				return false;
		} else if (!expressio.equals(other.expressio))
			return false;
		if (tasca == null) {
			if (other.tasca != null)
				return false;
		} else if (!tasca.equals(other.tasca))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
