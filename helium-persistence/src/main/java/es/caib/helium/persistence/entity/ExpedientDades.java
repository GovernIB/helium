package es.caib.helium.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

/**
 * Classe tipus entity que mapeja la taula de dades d'expedients que conté la columna CLOB amb el JSON de les
 * dades dels expedients per expedient, procés i tasca.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Entity
@Table(
		name= "hel_expedient_dades",
		uniqueConstraints={
					@UniqueConstraint(
							name="hel_expeident_dades_uk",
							columnNames={"expedient_tipus_id", "expedient_id"})
					}
)
public class ExpedientDades implements Serializable, GenericEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_exdades")
	@TableGenerator(name="gen_exdades", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(
			name = "expedient_tipus_id",
			foreignKey = @ForeignKey(name = "hel_ed_et_fk"))
	private ExpedientTipus expedientTipus;

	@ManyToOne(optional = false)
	@JoinColumn(
			name = "expedient_id",
			foreignKey = @ForeignKey(name = "hel_ed_ex_fk"))
	private Expedient expedient;
	@Column(name="principal")
	private Boolean principal;
	@Column(name="process_id")
	private String processId;
	@Column(name="task_id")
	private String taskId;

	/** Columna on es guarda el JSON amb les dades. */
	@Lob
    @Column(name = "DADES", columnDefinition = "CLOB")
    private String dades;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expedientTipus == null) ? 0 : expedientTipus.hashCode());
		result = prime * result + ((expedient == null) ? 0 : expedient.hashCode());
		result = prime * result + ((processId == null) ? 0 : processId.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpedientDades other = (ExpedientDades) obj;
		if (expedientTipus == null) {
			if (other.expedientTipus != null)
				return false;
		} else if (!expedientTipus.equals(other.expedientTipus))
			return false;
		if (expedient == null) {
			if (other.expedient != null)
				return false;
		} else if (!expedient.equals(other.expedient))
			return false;
		if (processId == null) {
			if (other.processId != null)
				return false;
		} else if (!processId.equals(other.processId))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private static final long serialVersionUID = 1L;
}
