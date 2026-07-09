package es.caib.helium.persistence.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

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
	name= "HEL_TASCA_CANDIDATE",
	uniqueConstraints={
		@UniqueConstraint(
			name="HEL_TASCA_CANDIDATE_UK",
			columnNames={"TASCA_ID"})
	}
)
public class TascaCandidate implements Serializable, GenericEntity<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_exdades")
	@TableGenerator(name="gen_exdades", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "TASCA_ID")
	private ExpedientTasca tasca;

	@Column(name="GRUP_ID")
	private String grupId;

	@Column(name="USER_ID")
	private String userId;

	@Column(name="TIPUS")
	private String tipus;
}
