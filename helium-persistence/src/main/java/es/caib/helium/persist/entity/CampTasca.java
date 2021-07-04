/**
 * 
 */
package es.caib.helium.persist.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Objecte de domini que representa un camp per a un formulari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_camp_tasca",
		uniqueConstraints={
			@UniqueConstraint(name = "hel_camptasca_camp_id_tasca_id_uk", columnNames={"expedient_tipus_id", "camp_id", "tasca_id"}),
			@UniqueConstraint(name = "hel_camptasca_tasca_id_ordre_uk", columnNames={"expedient_tipus_id", "tasca_id", "ordre"})
		},
		indexes = {
				@Index(name = "hel_camptasca_camp_i", columnList = "camp_id"),
				@Index(name = "hel_camptasca_tasca_i", columnList = "tasca_id"),
				@Index(name = "hel_camptasca_extip_i", columnList = "expedient_tipus_id")
		}
)
public class CampTasca implements Serializable, GenericEntity<Long> {

	private Long id;
	private boolean readFrom;
	private boolean writeTo;
	private boolean required;
	private boolean readOnly;
	private int order;
	private int ampleCols;
	private int buitCols;

	@NotNull
	private Camp camp;
	@NotNull
	private Tasca tasca;
	
	/** Aquest valor només s'informa quan es relaciona un camp amb una tasca heretada pel tipus d'expedient. */
	private ExpedientTipus expedientTipus;



	public CampTasca() {}
	public CampTasca(
			Camp camp,
			Tasca tasca,
			boolean readFrom,
			boolean writeTo,
			boolean required,
			boolean readOnly,
			int order,
			int ampleCols,
			int buitCols,
			ExpedientTipus expedientTipus) {
		this.tasca = tasca;
		this.camp = camp;
		this.readFrom = readFrom;
		this.writeTo = writeTo;
		this.required = required;
		this.readOnly = readOnly;
		this.order = order;
		this.ampleCols = ampleCols;
		this.buitCols = buitCols;
		this.expedientTipus = expedientTipus;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_camp_tasca")
	@TableGenerator(name="gen_camp_tasca", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="rf")
	public boolean isReadFrom() {
		return readFrom;
	}
	public void setReadFrom(boolean readFrom) {
		this.readFrom = readFrom;
	}

	@Column(name="wt")
	public boolean isWriteTo() {
		return writeTo;
	}
	public void setWriteTo(boolean writeTo) {
		this.writeTo = writeTo;
	}

	@Column(name="rq")
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}

	@Column(name="ro")
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Column(name="ordre")
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	@Column(name="ample_cols")
	public int getAmpleCols() {
		return ampleCols;
	}
	public void setAmpleCols(int ampleCols) {
		this.ampleCols = ampleCols;
	}
	@Column(name="buit_cols")
	public int getBuitCols() {
		return buitCols;
	}
	public void setBuitCols(int buitCols) {
		this.buitCols = buitCols;
	}
	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(
			name="camp_id",
			foreignKey = @ForeignKey(name="hel_camp_camptasca_fk"))
	public Camp getCamp() {
		return camp;
	}
	public void setCamp(Camp camp) {
		this.camp = camp;
	}

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(
			name="tasca_id",
			foreignKey = @ForeignKey(name="hel_tasca_camptasca_fk"))
	public Tasca getTasca() {
		return tasca;
	}
	public void setTasca(Tasca tasca) {
		this.tasca = tasca;
	}

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@JoinColumn(
			name="expedient_tipus_id",
			foreignKey = @ForeignKey(name="hel_extipus_camptasca_fk"))
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expedientTipus == null) ? 0 : expedientTipus.hashCode());
		result = prime * result + ((camp == null) ? 0 : camp.hashCode());
		result = prime * result + ((tasca == null) ? 0 : tasca.hashCode());
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
		CampTasca other = (CampTasca) obj;
		if (expedientTipus == null) {
			if (other.expedientTipus != null)
				return false;
		} else if (!expedientTipus.equals(other.expedientTipus))
			return false;
		if (camp == null) {
			if (other.camp != null)
				return false;
		} else if (!camp.equals(other.camp))
			return false;
		if (tasca == null) {
			if (other.tasca != null)
				return false;
		} else if (!tasca.equals(other.tasca))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
