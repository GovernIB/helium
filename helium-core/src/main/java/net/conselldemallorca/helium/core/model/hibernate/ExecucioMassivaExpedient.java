/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa un expedient per a executar
 * a una acció massiva.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_exec_masexp")
public class ExecucioMassivaExpedient implements Serializable, GenericEntity<Long> {

	public enum ExecucioMassivaEstat {
		ESTAT_FINALITZAT,
		ESTAT_ERROR,
		ESTAT_PENDENT,
		ESTAT_CANCELAT
	}
	
	private Long id;
	@NotNull
	private Date dataInici;
	private Date dataFi;
	@NotNull
	private ExecucioMassivaEstat estat;
	private String error;
	private int ordre;

	@NotNull
	private ExecucioMassiva execucioMassiva;
	private Expedient expedient;
	private String tascaId;
	private String processInstanceId;


	public ExecucioMassivaExpedient() {}
	public ExecucioMassivaExpedient(ExecucioMassiva execucioMassiva, Expedient expedient, int ordre) {
		this.execucioMassiva = execucioMassiva;
		this.expedient = expedient;
		this.ordre = ordre;
		this.estat = ExecucioMassivaEstat.ESTAT_PENDENT;
	}
	public ExecucioMassivaExpedient(ExecucioMassiva execucioMassiva, Expedient expedient, String tascaId, int ordre) {
		this.execucioMassiva = execucioMassiva;
		this.expedient = expedient;
		this.tascaId = tascaId;
		this.ordre = ordre;
		this.estat = ExecucioMassivaEstat.ESTAT_PENDENT;
	}
	public ExecucioMassivaExpedient(ExecucioMassiva execucioMassiva, String processInstanceId, int ordre) {
		this.execucioMassiva = execucioMassiva;
		this.processInstanceId = processInstanceId;
		this.ordre = ordre;
		this.estat = ExecucioMassivaEstat.ESTAT_PENDENT;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_estat")
	@TableGenerator(name="gen_estat", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="data_inici")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}

	@Column(name="data_fi")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	@Column(name="estat", nullable=false)
	public ExecucioMassivaEstat getEstat() {
		return estat;
	}
	public void setEstat(ExecucioMassivaEstat estat) {
		this.estat = estat;
	}
	
	@Lob
	@Column(name="error")
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	@Column(name="ordre", nullable=false)
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="execmas_id")
	@ForeignKey(name="hel_execmas_exemasex_fk")
	public ExecucioMassiva getExecucioMassiva() {
		return execucioMassiva;
	}
	public void setExecucioMassiva(ExecucioMassiva execucioMassiva) {
		this.execucioMassiva = execucioMassiva;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_id")
	@ForeignKey(name="hel_expedient_exemasex_fk")
	public Expedient getExpedient() {
		return expedient;
	}
	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}

	@Column(name="tasca_id", nullable=true)
	public String getTascaId() {
		return tascaId;
	}
	public void setTascaId(String tascaId) {
		this.tascaId = tascaId;
	}

	@Column(name="procinst_id", nullable=true)
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((execucioMassiva == null) ? 0 : execucioMassiva.hashCode());
		result = prime * result
				+ ((expedient == null) ? 0 : expedient.hashCode());
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
		ExecucioMassivaExpedient other = (ExecucioMassivaExpedient) obj;
		if (execucioMassiva == null) {
			if (other.execucioMassiva != null)
				return false;
		} else if (!execucioMassiva.equals(other.execucioMassiva))
			return false;
		if (expedient == null) {
			if (other.expedient != null)
				return false;
		} else if (!expedient.equals(other.expedient))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
