/**
 * 
 */
package net.conselldemallorca.helium.model.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una agrupació de camps.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Entity
@Table(	name="hel_camp_agrup",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "definicio_proces_id"})})
public class CampAgrupacio implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@MaxLength(255)
	private String descripcio;
	private int ordre;



	@NotNull
	private DefinicioProces definicioProces;

	private List<Camp> camps = new ArrayList<Camp>();



	public CampAgrupacio() {}
	public CampAgrupacio(DefinicioProces definicioProces, String codi, String nom, int ordre) {
		this.definicioProces = definicioProces;
		this.codi = codi;
		this.nom = nom;
		this.ordre = ordre;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_camp")
	@TableGenerator(name="gen_camp", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@Column(name="ordre")
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="definicio_proces_id")
	@ForeignKey(name="hel_defproc_campagrup_fk")
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}

	@OneToMany(mappedBy="agrupacio")
	@OrderBy("ordre asc")
	public List<Camp> getCamps() {
		return this.camps;
	}
	public void setCamps(List<Camp> camps) {
		this.camps = camps;
	}
	public void addCamp(Camp camp) {
		getCamps().add(camp);
	}
	public void removeCamp(Camp camp) {
		getCamps().remove(camp);
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
		CampAgrupacio other = (CampAgrupacio) obj;
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
