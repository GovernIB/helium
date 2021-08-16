/**
 * 
 */
package es.caib.helium.persist.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Objecte de domini que representa la firma d'un document a una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_firma_tasca",
		uniqueConstraints={
			@UniqueConstraint(name = "hel_firmatasca_camp_id_tasca_id_uk", columnNames={"expedient_tipus_id", "document_id", "tasca_id"}),
			@UniqueConstraint(name = "hel_firmatasca_tasca_id_ordre_uk", columnNames={"expedient_tipus_id", "tasca_id", "ordre"})},
		indexes = {
				@Index(name = "hel_firtasca_document_i", columnList = "document_id"),
				@Index(name = "hel_firtasca_tasca_i", columnList = "tasca_id"),
				@Index(name = "hel_firtasca_extip_i", columnList = "expedient_tipus_id")
		}
)
public class FirmaTasca implements Serializable, GenericEntity<Long> {

	private Long id;

	private boolean required;
	private int order;

	@NotNull
	private Document document;
	@NotNull
	private Tasca tasca;
	
	/** Aquest valor només s'informa quan es relaciona una firma amb una tasca heretada pel tipus d'expedient. */
	private ExpedientTipus expedientTipus;



	public FirmaTasca() {}
	public FirmaTasca(
			Document document,
			Tasca tasca,
			boolean required,
			int order,
			ExpedientTipus expedientTipus) {
		this.document = document;
		this.tasca = tasca;
		this.required = required;
		this.order = order;
		this.expedientTipus = expedientTipus;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_firma_tasca")
	@TableGenerator(name="gen_firma_tasca", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@Column(name="ordre")
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(
			name="document_id",
			foreignKey = @ForeignKey(name="hel_document_firtasca_fk"))
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(
			name="tasca_id",
			foreignKey = @ForeignKey(name="hel_tasca_firtasca_fk"))
	public Tasca getTasca() {
		return tasca;
	}
	public void setTasca(Tasca tasca) {
		this.tasca = tasca;
	}

	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	@JoinColumn(
			name="expedient_tipus_id",
			foreignKey = @ForeignKey(name="hel_extipus_firtasca_fk"))
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
		FirmaTasca other = (FirmaTasca) obj;
		if (expedientTipus == null) {
			if (other.expedientTipus != null)
				return false;
		} else if (!expedientTipus.equals(other.expedientTipus))
			return false;
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
