package es.caib.helium.persistence.entity;

import es.caib.helium.commons.dades.DocumentTipusEnum;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

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
}
