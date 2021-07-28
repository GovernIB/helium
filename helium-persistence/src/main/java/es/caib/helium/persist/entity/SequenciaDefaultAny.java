/**
 * 
 */
package es.caib.helium.persist.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Objecte de domini que representa una sequencia anual d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_expedient_tipus_seqdefany")
public class SequenciaDefaultAny implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotNull
	private ExpedientTipus expedientTipus;
	@NotBlank
	private Integer any;
	@NotBlank
	private Long sequenciaDefault;


	public SequenciaDefaultAny() {}
	public SequenciaDefaultAny(ExpedientTipus expedientTipus, Integer any, Long sequenciaDefault) {
		this.expedientTipus = expedientTipus;
		this.any = any;
		this.sequenciaDefault = sequenciaDefault;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_expedient_tipus_seqdefany")
	@TableGenerator(name="gen_expedient_tipus_seqdefany", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@SuppressWarnings("deprecation")
	@ManyToOne(optional=true)
	@JoinColumn(
			name="expedient_tipus",
			foreignKey = @ForeignKey(name="hel_exptipus_seqdefany_fk"))
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

	@Column(name="sequenciadefault", nullable=false)
	public Long getSequenciaDefault() {
		return sequenciaDefault;
	}
	public void setSequenciaDefault(Long sequenciaDefault) {
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
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SequenciaDefaultAny other = (SequenciaDefaultAny) obj;
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
