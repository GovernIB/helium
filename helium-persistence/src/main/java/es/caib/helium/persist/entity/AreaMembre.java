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

/**
 * Objecte de domini que representa un membre d'una àrea.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="hel_area_membre",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "area_id"})},
		indexes = @Index(name = "hel_areamembre_area_i", columnList = "area_id")
)
public class AreaMembre implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@Size(max = 64)
	private String codi;

	@NotNull
	private Area area;



	public AreaMembre() {}
	public AreaMembre(Area area, String codi) {
		this.codi = codi;
		this.area = area;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_area_membre")
	@TableGenerator(name="gen_area_membre", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@ManyToOne(optional=false)
	@JoinColumn(
			name="area_id",
			foreignKey = @ForeignKey(name="hel_area_areamembre_fk"))
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((area == null) ? 0 : area.hashCode());
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
		AreaMembre other = (AreaMembre) obj;
		if (area == null) {
			if (other.area != null)
				return false;
		} else if (!area.equals(other.area))
			return false;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
