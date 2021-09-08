/**
 * 
 */
package es.caib.helium.expedient.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.domain.Persistable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objecte grup que representa la informació d'un grup assignat a una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(	name="hel_grup",
		uniqueConstraints={@UniqueConstraint(name = "hel_grup_uk", columnNames = {"tasca_id", "grup_codi"})},
		indexes = {
			@Index(name = "hel_grup_tasca_i", columnList = "tasca_id")
		})
@SequenceGenerator(name = "hel_grup_gen", sequenceName = "hel_grup_seq", allocationSize = 1)
public class Grup implements Persistable<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hel_grup_gen")
	private Long id;
	
	@ManyToOne(optional=false, cascade={CascadeType.ALL})
	@JoinColumn(name="tasca_id")
	// HEL_RESP_TASCA_FK
	private Tasca tasca;
	
	@NotNull
	@Column(name="grup_codi", nullable=false)
	@Size(max = 255)
	private String grupCodi;

	@Override
	public boolean isNew() {
		return id != null;
	}
}
