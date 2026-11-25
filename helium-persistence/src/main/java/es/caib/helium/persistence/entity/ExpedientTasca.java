package es.caib.helium.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Classe tipus entity que mapeja la taula de tasques d'expedients
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
	name= "HEL_TASCA_EXPEDIENT",
	uniqueConstraints={
		@UniqueConstraint(
			name="HEL_TASCA_EXPEDIENT_UK",
			columnNames={"EXPEDIENT_ID", "TASK_ID"})
	}
)
public class ExpedientTasca implements Serializable, GenericEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_exdades")
	@TableGenerator(name="gen_exdades", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "EXPEDIENT_ID")
	private Expedient expedient;

	@Column(name="TASK_ID")
	private String taskId;

	@Column(name="TASK_CODE")
	private String taskCode;

	@Column(name="NAME")
	private String name;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="OWNER")
	private String owner;

	@Column(name="GROUP_ID")
	private String groupId;

	@Column(name="ASSIGNEE")
	private String assignee;

	@Column(name="CREATE_TIME")
	private Date createTime;

	@Column(name="START_TIME")
	private Date startTime;

	@Column(name="CLAIM_TIME")
	private Date claimTime;

	@Column(name="END_TIME")
	private Date endTime;

	@Column(name = "DUE_DATE")
	private Date dueDate;

	@Column(name="DURATION")
	private Long duration;

	@Column(name="PRIORITY")
	private Integer priority;

	@Column(name="OPEN")
	private Boolean open;
	@Column(name="COMPLETED")
	private Boolean completed;
	@Column(name="CANCELLED")
	private Boolean cancelled;
	@Column(name="SUSPENDED")
	private Boolean suspended;
	@Column(name="VALIDATED")
	private Boolean validated;

	// Finalització en segon pla (amb jBPM eren columnes de JBPM_TASKINSTANCE)
	/** Data en què l'usuari ha completat la tasca i ha quedat pendent de finalitzar en segon pla. */
	@Column(name="MARCADA_FINALITZAR")
	private Date marcadaFinalitzar;
	/** Data en què el procés en segon pla ha començat a finalitzar la tasca. */
	@Column(name="INICI_FINALITZACIO")
	private Date iniciFinalitzacio;
	@Column(name="ERROR_FINALITZACIO", length = 1000)
	private String errorFinalitzacio;
	/** Transició triada per l'usuari en completar la tasca. */
	@Column(name="SELECTED_OUTCOME", length = 255)
	private String selectedOutcome;
	/** Rols de l'usuari que ha completat la tasca, separats per comes, per finalitzar-la en el seu nom. */
	@Column(name="ROLS", length = 2000)
	private String rols;

	@OneToMany(mappedBy="tasca", fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	private List<TascaCandidate> candidates;

	public Set<String> getPooledActors() {
		if(candidates == null)
			return Set.of();

		return candidates
				.stream()
				.map(TascaCandidate::getUserId)
				.collect(Collectors.toSet());
	}
}
