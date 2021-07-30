/**
 * 
 */
package es.caib.helium.camunda.model.bridge;

import java.io.Serializable;

/**
 * Objecte de domini que representa una sequencia anual d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SequenciaAnyDto implements Serializable {

	private Long id;
	private ExpedientTipusDto expedientTipus;
	private Integer any;
	private Long sequencia;


	public SequenciaAnyDto() {}
	public SequenciaAnyDto(ExpedientTipusDto expedientTipus, Integer any, Long sequencia) {
		this.expedientTipus = expedientTipus;
		this.any = any;
		this.sequencia = sequencia;
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
	public Long getSequencia() {
		return sequencia;
	}
	public void setSequencia(Long sequencia) {
		this.sequencia = sequencia;
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
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SequenciaAnyDto other = (SequenciaAnyDto) obj;
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
