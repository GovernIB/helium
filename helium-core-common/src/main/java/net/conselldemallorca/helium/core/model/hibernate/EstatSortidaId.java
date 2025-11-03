/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Identificador compost de EntradaSortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Embeddable
public class EstatSortidaId implements Serializable {
	
	@Column(name="estat_id")
	private Long estatId;
	@Column(name="estat_seguent_id")
	private Long estatSeguentId;

	public EstatSortidaId () {}
	public EstatSortidaId (Long estatId, Long estatSeguentId) {
		this.estatId = estatId;
		this.estatSeguentId = estatSeguentId;
	}

	public Long getEstatId() {
		return estatId;
	}
	public void setEstatId(Long estatId) {
		this.estatId = estatId;
	}
	public Long getEstatSeguentId() {
		return estatSeguentId;
	}
	public void setEstatSeguentId(Long estatSeguentId) {
		this.estatSeguentId = estatSeguentId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(estatId, estatSeguentId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EstatSortidaId other = (EstatSortidaId) obj;
		if (estatId == null) {
			if (other.estatId != null)
				return false;
		} else if (!estatId.equals(other.estatId))
			return false;
		if (estatSeguentId == null) {
			if (other.estatSeguentId != null)
				return false;
		} else if (!estatSeguentId.equals(other.estatSeguentId))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;
}
