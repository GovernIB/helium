/**
 * 
 */
package net.conselldemallorca.helium.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una alerta.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Entity
@Table(name="hel_alerta")
public class Alerta implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotNull
	private Date dataCreacio;
	@NotNull
	private String destinatari;
	@NotBlank
	@MaxLength(1024)
	private String text;
	@MaxLength(255)
	private String taskInstanceId;

	private Date dataLectura;
	private Date dataEliminacio;

	@NotNull
	private Entorn entorn;
	@NotNull
	private Expedient expedient;



	public Alerta() {}
	public Alerta(Date dataCreacio, String destinatari, String text, Entorn entorn) {
		this.dataCreacio = dataCreacio;
		this.destinatari = destinatari;
		this.text = text;
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

	@Column(name="destinatari", nullable=false)
	public String getDestinatari() {
		return destinatari;
	}
	public void setDestinatari(String destinatari) {
		this.destinatari = destinatari;
	}

	@Column(name="text", length=1024, nullable=true)
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	@Column(name="task_instance_id", length=255)
	public String getTaskInstanceId() {
		return taskInstanceId;
	}
	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
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
	@JoinColumn(name="entorn_id")
	@ForeignKey(name="hel_entorn_alerta_fk")
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="expedient_id")
	@ForeignKey(name="hel_expedient_alerta_fk")
	public Expedient getExpedient() {
		return expedient;
	}
	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
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
		result = prime * result
				+ ((taskInstanceId == null) ? 0 : taskInstanceId.hashCode());
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
		if (taskInstanceId == null) {
			if (other.taskInstanceId != null)
				return false;
		} else if (!taskInstanceId.equals(other.taskInstanceId))
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
