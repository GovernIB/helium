/**
 * 
 */
package net.conselldemallorca.helium.model.hibernate;

import java.io.Serializable;
import java.util.Calendar;
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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa un termini iniciat per a un expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Entity
@Table(	name="hel_termini_iniciat",
		uniqueConstraints={@UniqueConstraint(columnNames={"termini_id", "process_instance_id"})})
public class TerminiIniciat implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotNull
	private Date dataInici;
	@NotNull
	private Date dataFi;
	private Date dataAturada;
	private Date dataCancelacio;
	private int diesAturat;
	@NotBlank
	@MaxLength(255)
	private String processInstanceId;
	@MaxLength(255)
	private String timerName;
	@MaxLength(255)
	private String jbpmVariable;

	private Termini termini;



	public TerminiIniciat() {}
	public TerminiIniciat(Termini termini, String processInstanceId, Date dataInici, Date dataFi) {
		this.termini = termini;
		this.processInstanceId = processInstanceId;
		this.dataInici = dataInici;
		this.dataFi = dataFi;
	}
	public TerminiIniciat(String jbpmVariable, String processInstanceId, Date dataInici, Date dataFi) {
		this.jbpmVariable = jbpmVariable;
		this.processInstanceId = processInstanceId;
		this.dataInici = dataInici;
		this.dataFi = dataFi;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_termini_iniciat")
	@TableGenerator(name="gen_termini_iniciat", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="data_inici", nullable=false)
	@Temporal(TemporalType.DATE)
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}

	@Column(name="data_fi", nullable=false)
	@Temporal(TemporalType.DATE)
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	@Column(name="data_aturada")
	@Temporal(TemporalType.DATE)
	public Date getDataAturada() {
		return dataAturada;
	}
	public void setDataAturada(Date dataAturada) {
		this.dataAturada = dataAturada;
	}

	@Column(name="data_cancel")
	@Temporal(TemporalType.DATE)
	public Date getDataCancelacio() {
		return dataCancelacio;
	}
	public void setDataCancelacio(Date dataCancelacio) {
		this.dataCancelacio = dataCancelacio;
	}

	@Column(name="dies_aturat")
	public int getDiesAturat() {
		return diesAturat;
	}
	public void setDiesAturat(int diesAturat) {
		this.diesAturat = diesAturat;
	}

	@Column(name="process_instance_id", length=255, nullable=false)
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name="timer_name", length=255)
	public String getTimerName() {
		return timerName;
	}
	public void setTimerName(String timerName) {
		this.timerName = timerName;
	}

	@Column(name="jbpm_variable", length=255)
	public String getJbpmVariable() {
		return jbpmVariable;
	}
	public void setJbpmVariable(String jbpmVariable) {
		this.jbpmVariable = jbpmVariable;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="termini_id")
	@ForeignKey(name="hel_termini_terminic_fk")
	public Termini getTermini() {
		return termini;
	}
	public void setTermini(Termini termini) {
		this.termini = termini;
	}

	@Transient
	public int getNumDiesAturadaActual(Date data) {
		if (getDataAturada() == null)
			return 0;
		long milisegonsUnDia = 1000 * 60 * 60 * 24;
		long inici = getDataAturada().getTime();
		long fi = data.getTime();
		return new Long((fi - inici) / milisegonsUnDia).intValue();
	}
	@Transient
	public Date getDataFiAmbAturadaActual() {
		if (dataFi == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataFi);
		cal.add(Calendar.DAY_OF_MONTH, getNumDiesAturadaActual(new Date()));
		return cal.getTime();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((processInstanceId == null) ? 0 : processInstanceId
						.hashCode());
		result = prime * result + ((termini == null) ? 0 : termini.hashCode());
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
		TerminiIniciat other = (TerminiIniciat) obj;
		if (processInstanceId == null) {
			if (other.processInstanceId != null)
				return false;
		} else if (!processInstanceId.equals(other.processInstanceId))
			return false;
		if (termini == null) {
			if (other.termini != null)
				return false;
		} else if (!termini.equals(other.termini))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
