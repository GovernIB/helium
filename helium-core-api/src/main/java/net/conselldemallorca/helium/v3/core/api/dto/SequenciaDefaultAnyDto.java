/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Objecte de domini que representa una sequencia anual d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SequenciaDefaultAnyDto implements Serializable {

	private Long id;
	private ExpedientTipusDto expedientTipus;
	private Integer any;
	private Long sequenciaDefault;


	public SequenciaDefaultAnyDto() {}
	public SequenciaDefaultAnyDto(ExpedientTipusDto expedientTipus, Integer any, Long sequenciaDefault) {
		this.expedientTipus = expedientTipus;
		this.any = any;
		this.sequenciaDefault = sequenciaDefault;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((any == null) ? 0 : any.hashCode());
		result = prime * result
				+ ((expedientTipus == null) ? 0 : expedientTipus.hashCode());
		return result;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	public Integer getAny() {
		return any;
	}
	public void setAny(Integer any) {
		this.any = any;
	}
	public Long getSequenciaDefault() {
		return sequenciaDefault;
	}
	public void setSequenciaDefault(Long sequenciaDefault) {
		this.sequenciaDefault = sequenciaDefault;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SequenciaDefaultAnyDto other = (SequenciaDefaultAnyDto) obj;
		if (any == null) {
			if (other.any != null)
				return false;
		} else if (!any.equals(other.any))
			return false;
		if (expedientTipus == null) {
			if (other.expedientTipus != null)
				return false;
		} else if (!expedientTipus.equals(other.expedientTipus))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
