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
 * Objecte de domini que representa un document d'una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_document_tasca",
		uniqueConstraints={
			@UniqueConstraint(columnNames={"document_id", "tasca_id"}),
			@UniqueConstraint(columnNames={"tasca_id", "ordre"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_document_tasca",
		indexes = {
				@Index(name = "hel_doctasca_document_i", columnNames = {"document_id"}),
				@Index(name = "hel_doctasca_tasca_i", columnNames = {"tasca_id"}),
				@Index(name = "hel_doctasca_extip_i", columnNames = {"expedient_tipus_id"})})
public class DocumentTasca implements Serializable, GenericEntity<Long> {

	private Long id;

	private boolean required;
	private boolean readOnly;
	private int order;

	@NotNull
	private Document document;
	@NotNull
	private Tasca tasca;
	
	/** Aquest valor només s'informa quan es relaciona un document amb una tasca heretada pel tipus d'expedient. */
	private ExpedientTipus expedientTipus;



	public DocumentTasca() {}
	public DocumentTasca(
			Document document,
			Tasca tasca,
			boolean required,
			boolean readOnly,
			int order) {
		this.document = document;
		this.tasca = tasca;
		this.required = required;
		this.readOnly = readOnly;
		this.order = order;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_document_tasca")
	@TableGenerator(name="gen_document_tasca", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	@JoinColumn(name="document_id")
	@ForeignKey(name="hel_document_doctasca_fk")
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}

	@ManyToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name="tasca_id")
	@ForeignKey(name="hel_tasca_doctasca_fk")
	public Tasca getTasca() {
		return tasca;
	}
	public void setTasca(Tasca tasca) {
		this.tasca = tasca;
	}

	@ManyToOne(optional=true, fetch=FetchType.EAGER)
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_extipus_doctasca_fk")
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
		result = prime * result
				+ ((document == null) ? 0 : document.hashCode());
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
		DocumentTasca other = (DocumentTasca) obj;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
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
