/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa un camp per a un formulari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_camp_tasca",
		uniqueConstraints={
			@UniqueConstraint(columnNames={"camp_id", "tasca_id"}),
			@UniqueConstraint(columnNames={"tasca_id", "ordre"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_camp_tasca",
		indexes = {
				@Index(name = "hel_camptasca_camp_i", columnNames = {"camp_id"}),
				@Index(name = "hel_camptasca_tasca_i", columnNames = {"tasca_id"})})
public class CampTasca implements Serializable, GenericEntity<Long> {

	private Long id;
	private boolean readFrom;
	private boolean writeTo;
	private boolean required;
	private boolean readOnly;
	private int order;

	@NotNull
	private Camp camp;
	@NotNull
	private Tasca tasca;



	public CampTasca() {}
	public CampTasca(
			Camp camp,
			Tasca tasca,
			boolean readFrom,
			boolean writeTo,
			boolean required,
			boolean readOnly,
			int order) {
		this.tasca = tasca;
		this.camp = camp;
		this.readFrom = readFrom;
		this.writeTo = writeTo;
		this.required = required;
		this.readOnly = readOnly;
		this.order = order;
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

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name="camp_id")
	@ForeignKey(name="hel_camp_camptasca_fk")
	public Camp getCamp() {
		return camp;
	}
	public void setCamp(Camp camp) {
		this.camp = camp;
	}

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name="tasca_id")
	@ForeignKey(name="hel_tasca_camptasca_fk")
	public Tasca getTasca() {
		return tasca;
	}
	public void setTasca(Tasca tasca) {
		this.tasca = tasca;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
