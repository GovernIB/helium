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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una sequencia anual d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_expedient_tipus_seqany")
public class SequenciaAny implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotNull
	private ExpedientTipus expedientTipus;
	@NotBlank
	private Integer any;
	@NotBlank
	private Long sequencia;


	public SequenciaAny() {}
	public SequenciaAny(ExpedientTipus expedientTipus, Integer any, Long sequencia) {
		this.expedientTipus = expedientTipus;
		this.any = any;
		this.sequencia = sequencia;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_expedient_tipus_seqany")
	@TableGenerator(name="gen_expedient_tipus_seqany", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_tipus")
	@ForeignKey(name="hel_exptipus_seqany_fk")
	@Cascade({CascadeType.DELETE_ORPHAN})
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	
	@Column(name="any_", nullable=false)
	public Integer getAny() {
		return any;
	}
	public void setAny(Integer any) {
		this.any = any;
	}

	@Column(name="sequencia", nullable=false)
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
		SequenciaAny other = (SequenciaAny) obj;
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
