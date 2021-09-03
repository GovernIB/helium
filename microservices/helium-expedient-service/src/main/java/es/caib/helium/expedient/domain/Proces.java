/**
 * 
 */
package es.caib.helium.expedient.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

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
		indexes = {
			@Index(name = "hel_proces_exp_i", columnList = "expedient_id")
		})
public class Proces implements Persistable<String> {

	/** Identificador del proces dins del motor */
	@Id
	@NotEmpty
	@Size(max = 64)
	@Column(name="id", nullable=false, length = 64)
	private String id;
	
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
	
	@Override
	public boolean isNew() {
		return id != null;
	}
}
