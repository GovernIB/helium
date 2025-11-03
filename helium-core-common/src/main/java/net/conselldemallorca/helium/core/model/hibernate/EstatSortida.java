/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

/**
 * Objecte de domini que representa la relació entre un estat i els seus estats de sortida. S'ha creat sense
 * id per la qual cosa la clau primària són l'estat i l'estat de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_estat_sortida")
public class EstatSortida implements Serializable {

	@EmbeddedId
    private EstatSortidaId id;
	
	@ManyToOne(optional=false, fetch = FetchType.LAZY)
	@MapsId("estatId")
	@ForeignKey(name="i_fk_estat_sortida_estat")
    @JoinColumn(name = "estat_id")
	private Estat estat;

	@ManyToOne(optional=false, fetch = FetchType.LAZY)
	@MapsId("estatSeguentId")
	@ForeignKey(name="i_fk_estat_sortida_seguent")
    @JoinColumn(name = "estat_seguent_id")
	private Estat estatSeguent;

	public EstatSortida() {}

	public EstatSortida(Estat estat, Estat estatSeguent) {
		this.estat = estat;
		this.estatSeguent = estatSeguent;
		this.id = new EstatSortidaId(estat.getId(), estatSeguent.getId());
	}

	public EstatSortidaId getId() {
		return id;
	}

	public void setId(EstatSortidaId id) {
		this.id = id;
	}

	
	public Estat getEstat() {
		return estat;
	}
	public void setEstat(Estat estat) {
		this.estat = estat;
	}

	public Estat getEstatSeguent() {
		return estatSeguent;
	}
	public void setEstatSeguent(Estat estatSeguent) {
		this.estatSeguent = estatSeguent;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((estat == null) ? 0 : estat.hashCode());
		result = prime * result
				+ ((estatSeguent == null) ? 0 : estatSeguent.hashCode());
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
		EstatSortida other = (EstatSortida) obj;
		if (estat == null) {
			if (other.estat != null)
				return false;
		} else if (!estat.equals(other.estat))
			return false;
		if (estatSeguent == null) {
			if (other.estatSeguent != null)
				return false;
		} else if (!estatSeguent.equals(other.estatSeguent))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
