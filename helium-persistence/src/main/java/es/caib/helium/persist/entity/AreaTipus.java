/**
 * 
 */
package es.caib.helium.persist.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Objecte de domini que representa una àrea de l'organigrama.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="hel_area_tipus",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "entorn_id"})},
		indexes = @Index(name = "hel_areatipus_entorn_i", columnList = "entorn_id")
)
public class AreaTipus implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@Size(max = 64)
	private String codi;
	@NotBlank
	@Size(max = 255)
	private String nom;
	@Size(max = 255)
	private String descripcio;

	@NotNull
	private Entorn entorn;

	private Set<Area> arees = new HashSet<Area>();



	public AreaTipus() {}
	public AreaTipus(String codi, String nom, Entorn entorn) {
		this.codi = codi;
		this.nom = nom;
		this.entorn = entorn;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_area_tipus")
	@TableGenerator(name="gen_area_tipus", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@ManyToOne(optional=false)
	@JoinColumn(
			name="entorn_id",
			foreignKey = @ForeignKey(name="hel_entorn_areatipus_fk"))
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@OneToMany(mappedBy="tipus", cascade=CascadeType.REMOVE)
	public Set<Area> getArees() {
		return this.arees;
	}
	public void setArees(Set<Area> arees) {
		this.arees = arees;
	}
	public void addFill(Area area) {
		getArees().add(area);
	}
	public void removeFill(Area area) {
		getArees().remove(area);
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((entorn == null) ? 0 : entorn.hashCode());
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
		AreaTipus other = (AreaTipus) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (entorn == null) {
			if (other.entorn != null)
				return false;
		} else if (!entorn.equals(other.entorn))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
