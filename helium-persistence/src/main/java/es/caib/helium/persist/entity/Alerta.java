/**
 * 
 */
package es.caib.helium.persist.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Objecte de domini que representa una alerta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_alerta",
		indexes = {
				@Index(name = "hel_alerta_entorn_i", columnList = "entorn_id"),
				@Index(name = "hel_alerta_expedient_i", columnList = "expedient_id")
		}
)
public class Alerta implements Serializable, GenericEntity<Long> {

	public enum AlertaPrioritat {
		MOLT_BAIXA,
		BAIXA,
		NORMAL,
		ALTA,
		MOLT_ALTA
	}

	private Long id;
	@NotNull
	private Date dataCreacio;
	@NotNull
	private String destinatari;
	@NotBlank
	@Size(max = 255)
	private String causa;
	@NotBlank
	@Size(max = 1024)
	private String text;

	private Date dataLectura;
	private Date dataEliminacio;

	@NotNull
	private Entorn entorn;
	@NotNull
	private Expedient expedient;
	private TerminiIniciat terminiIniciat;
	private AlertaPrioritat prioritat;



	public Alerta() {}
	public Alerta(Date dataCreacio, String destinatari, String text, Entorn entorn) {
		this.dataCreacio = dataCreacio;
		this.destinatari = destinatari;
		this.text = text;
		this.entorn = entorn;
	}
	public Alerta(Date dataCreacio, String destinatari, String causa, String text, Entorn entorn) {
		this.dataCreacio = dataCreacio;
		this.destinatari = destinatari;
		this.text = text;
		this.entorn = entorn;
	}
	public Alerta(Date dataCreacio, String destinatari, AlertaPrioritat prioritat, Entorn entorn) {
		this.dataCreacio = dataCreacio;
		this.destinatari = destinatari;
		this.prioritat = prioritat;
		this.entorn = entorn;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_area")
	@TableGenerator(name="gen_area", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="data_creacio", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}

	@Column(name="destinatari", nullable=true)
	public String getDestinatari() {
		return destinatari;
	}
	public void setDestinatari(String destinatari) {
		this.destinatari = destinatari;
	}

	@Column(name="causa", length=255, nullable=true)
	public String getCausa() {
		return causa;
	}
	public void setCausa(String causa) {
		this.causa = causa;
	}
	
	@Column(name="text", length=1024, nullable=true)
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	@Column(name="data_lectura", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataLectura() {
		return dataLectura;
	}
	public void setDataLectura(Date dataLectura) {
		this.dataLectura = dataLectura;
	}

	@Column(name="data_eliminacio", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataEliminacio() {
		return dataEliminacio;
	}
	public void setDataEliminacio(Date dataEliminacio) {
		this.dataEliminacio = dataEliminacio;
	}

	@ManyToOne(optional=false)
	@JoinColumn(
			name="entorn_id",
			foreignKey = @ForeignKey(name="hel_entorn_alerta_fk"))
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@ManyToOne(optional=false)
	@JoinColumn(
			name="expedient_id",
			foreignKey = @ForeignKey(name="hel_expedient_alerta_fk"))
	public Expedient getExpedient() {
		return expedient;
	}
	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}

	@ManyToOne(optional=true)
	@JoinColumn(
			name="termini_iniciat_id",
			foreignKey = @ForeignKey(name="hel_termini_alerta_fk"))
	public TerminiIniciat getTerminiIniciat() {
		return terminiIniciat;
	}
	public void setTerminiIniciat(TerminiIniciat terminiIniciat) {
		this.terminiIniciat = terminiIniciat;
	}

	@Column(name="prioritat")
	public AlertaPrioritat getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(AlertaPrioritat prioritat) {
		this.prioritat = prioritat;
	}
	
	@Transient
	public boolean isLlegida() {
		return dataLectura != null;
	}
	@Transient
	public boolean isEliminada() {
		return dataEliminacio != null;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataCreacio == null) ? 0 : dataCreacio.hashCode());
		result = prime * result
				+ ((destinatari == null) ? 0 : destinatari.hashCode());
		result = prime * result + ((entorn == null) ? 0 : entorn.hashCode());
		result = prime * result
				+ ((expedient == null) ? 0 : expedient.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		Alerta other = (Alerta) obj;
		if (dataCreacio == null) {
			if (other.dataCreacio != null)
				return false;
		} else if (!dataCreacio.equals(other.dataCreacio))
			return false;
		if (destinatari == null) {
			if (other.destinatari != null)
				return false;
		} else if (!destinatari.equals(other.destinatari))
			return false;
		if (entorn == null) {
			if (other.entorn != null)
				return false;
		} else if (!entorn.equals(other.entorn))
			return false;
		if (expedient == null) {
			if (other.expedient != null)
				return false;
		} else if (!expedient.equals(other.expedient))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
