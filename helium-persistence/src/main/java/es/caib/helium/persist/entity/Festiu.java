/**
 * 
 */
package es.caib.helium.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Objecte de domini que representa un dia festiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_festiu")
public class Festiu implements Serializable {

	private Long id;
	@NotNull
	private Date data;



	public Festiu() {}
	public Festiu(Date data) {
		this.data = data;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_festiu")
	@TableGenerator(name="gen_festiu", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="data", nullable=false, unique=true)
	@Temporal(TemporalType.DATE)
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		Festiu other = (Festiu) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Data id=" + id + ", datam=" + data;
	}

	private static final long serialVersionUID = 1L;

}
