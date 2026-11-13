package es.caib.helium.persistence.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import es.caib.helium.commons.dades.DocumentTipusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe tipus entity que mapeja la taula de documents d'expedients
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "HEL_EXPEDIENT_DOCUMENT")
public class ExpedientDocument implements Serializable, GenericEntity<Long> {


	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_camp")
	@TableGenerator(name="gen_camp", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	Long id;

	@ManyToOne(optional = false)
	@JoinColumn(
			name = "DOCUMENT_STORE_ID",
			foreignKey = @ForeignKey(name = "HEL_EXP_DOC_DOCUMENT_STORE_FK"))
	private DocumentStore documentStore;

	@Column(name = "CODI")
	private String codi;

	@Column(name = "TIPUS")
	@Enumerated(EnumType.STRING)
	private DocumentTipusEnum tipus;

	@ManyToOne(optional = false)
	@JoinColumn(
			name = "EXPEDIENT_ID",
			foreignKey = @ForeignKey(name = "HEL_EXP_DOC_EXPEDIENT_FK"))
	private Expedient expedient;

	@Column(name = "TASK_ID", length = 64)
	private String taskId;

	@Column(name = "PROCESS_INSTANCE_ID", length = 64)
	private String processInstanceId;

	private static final long serialVersionUID = 1L;
}
