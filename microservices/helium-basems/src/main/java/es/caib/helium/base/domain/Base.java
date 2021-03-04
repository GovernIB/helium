/**
 * 
 */
package es.caib.helium.base.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Objecte base d'exemple
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(	name="hel_base",
		uniqueConstraints={@UniqueConstraint(columnNames = {"codi"})},
		indexes = {
			@Index(name = "hel_base_codi_i", columnList = "codi")
		})
@SequenceGenerator(name = "hel_base_gen", sequenceName = "hel_base_seq", allocationSize = 1)
public class Base implements Persistable<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hel_base_gen")
//	@Column(name="id")
	private Long id;

	@NotBlank
	@Size(max = 64)
//	@Column(name="codi", length=64, nullable=false)
	private String codi;

	@NotBlank
	@Size(max = 255)
//	@Column(name="nom", length=255, nullable=false)
	private String nom;

	private static final long serialVersionUID = 1L;

	@Override
	public boolean isNew() {
		return id == null || id.equals(0L);
	}
}
