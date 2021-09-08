/**
 * 
 */
package es.caib.helium.expedient.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
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
 * Objecte proces que representa la informació d'una instància de procés. Les instàncies de procecos
 * pertanyen a expedients i les tasques estan associades a instàncies de procesos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(	name="hel_proces",
		uniqueConstraints={@UniqueConstraint(name = "hel_proces_uk", columnNames = {"motor", "proces_id"})},
		indexes = {
			@Index(name = "hel_proces_exp_i", columnList = "expedient_id")
		})
@SequenceGenerator(name = "hel_proces_gen", sequenceName = "hel_proces_seq", allocationSize = 1)
public class Proces implements Persistable<Long> {

	/** Identificador intern auto generat. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hel_proces_gen")
	private Long id;

	/** Identificador del proces dins del motor */
	@NotEmpty
	@Size(max = 64)
	@Column(name="motor", nullable=false, length = 64)
	private String motor;

	/** Identificador del proces dins del motor */
	@NotEmpty
	@Size(max = 64)
	@Column(name="proces_id", nullable=false, length = 64)
	private String procesId;
	
	/** Expedient al qual pertany el proces */
	@ManyToOne(optional=true, cascade={CascadeType.ALL})
	@JoinColumn(name="expedient_id")
	private Expedient expedient;
	
	/** Procés arrel del procés. Pot ser ell mateix o tenir diferents nivells. */
	@ManyToOne(optional = false, fetch=FetchType.LAZY)
	@JoinColumn(name="proces_arrel_id", foreignKey = @ForeignKey(name="HEL_PROCES_ARREL_FK"))
	private Proces procesArrel;

	/** Procés pare del procés actual. Pot ser null si el procés actual és el procés arrel. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="proces_pare_id", foreignKey = @ForeignKey(name="HEL_PROCES_PARE_FK"))
	private Proces procesPare;
	
	/** Identificador de la definició de procés del procés. Correspon al processDefinitionId donat pel motor
	 * per a la instància de procés.
	 */
	@NotEmpty
	@Size(max = 64)
	@Column(name="processdefinition", nullable = false, length = 64)
	private String processDefinitionId;

	/** Pot contenir la propietat descripció de la instancia de procés. */
	@Size(max = 255)
	@Column(name="descripcio", length = 255)
	private String descripcio;

	/** Data d'inicio o creació del procés */
	@NotNull
	@Column(name="data_inici", nullable = false)
	private Date dataInici;

	/** Data de finalització del procés */
	@Column(name="data_fi")
	private Date dataFi;

	/** Camp actual amb la descripció. */
	@Builder.Default
	@Column(name="suspes", nullable = false)
	private boolean suspes = false;
	
	@OneToMany(mappedBy="proces", cascade={CascadeType.ALL}, fetch = FetchType.LAZY)
	private List<Tasca> tasques;

	@Override
	public boolean isNew() {
		return id != null;
	}
}
