/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import net.conselldemallorca.helium.core.api.WorkflowRetroaccioApi.ExpedientRetroaccioEstat;
import net.conselldemallorca.helium.core.api.WorkflowRetroaccioApi.ExpedientRetroaccioTipus;
import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Objecte de domini que representa una entrada de log d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_expedient_log")
public class ExpedientLog implements Serializable, GenericEntity<Long> {

	private static int MAX_PARAMETERS_LENGTH = 2048;
	
	private Long id;
	@NotBlank
	@MaxLength(255)
	private String usuari;
	@MaxLength(2048)
	private String accioParams;
	@NotNull
	private Date data = new Date();
	@NotNull
	private String targetId;
	private Long jbpmLogId;
	private Long processInstanceId;
	@NotNull
	private ExpedientRetroaccioEstat estat = ExpedientRetroaccioEstat.NORMAL;
	@NotNull
	private ExpedientRetroaccioTipus accioTipus;

	@NotNull
	private Expedient expedient;
	private Long iniciadorRetroces;

	public ExpedientLog() {}
	public ExpedientLog(
			Expedient expedient,
			String usuari,
			String targetId,
			ExpedientRetroaccioTipus accioTipus) {
		this.expedient = expedient;
		this.usuari = usuari;
		this.targetId = targetId;
		this.accioTipus = accioTipus;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_expedient_log")
	@TableGenerator(name="gen_expedient_log", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="usuari", length=255, nullable=false)
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}

	@Column(name="accio_params", length=2048, nullable=true)
	public String getAccioParams() {
		return accioParams;
	}
	public void setAccioParams(String accioParams) {
		if (accioParams != null) {
			if (accioParams.length() > MAX_PARAMETERS_LENGTH)
				this.accioParams = accioParams.substring(0, MAX_PARAMETERS_LENGTH);
			else
				this.accioParams = accioParams;
		} else {
			this.accioParams = accioParams;
		}
	}

	@Column(name="data", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

	@Column(name="target_id", nullable=false)
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	@Column(name="jbpm_logid")
	public Long getJbpmLogId() {
		return jbpmLogId;
	}
	public void setJbpmLogId(Long jbpmLogId) {
		this.jbpmLogId = jbpmLogId;
	}

	@Column(name="process_instance_id")
	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name="estat", nullable=false)
	public ExpedientRetroaccioEstat getEstat() {
		return estat;
	}
	public void setEstat(ExpedientRetroaccioEstat estat) {
		this.estat = estat;
	}

	@Column(name="accio_tipus", nullable=false)
	public ExpedientRetroaccioTipus getAccioTipus() {
		return accioTipus;
	}
	public void setAccioTipus(ExpedientRetroaccioTipus accioTipus) {
		this.accioTipus = accioTipus;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="expedient_id")
	@ForeignKey(name="hel_expedient_logs_fk")
	public Expedient getExpedient() {
		return expedient;
	}
	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}

	@Column(name="ini_retroces", nullable=true)
	public Long getIniciadorRetroces() {
		return iniciadorRetroces;
	}
	public void setIniciadorRetroces(Long iniciadorRetroces) {
		this.iniciadorRetroces = iniciadorRetroces;
	}
	
	@Transient
	public boolean isTargetTasca() {
		return	accioTipus.equals(ExpedientRetroaccioTipus.TASCA_REASSIGNAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_FORM_GUARDAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_FORM_VALIDAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_FORM_RESTAURAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_DOCUMENT_AFEGIR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_DOCUMENT_MODIFICAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_DOCUMENT_ESBORRAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_DOCUMENT_SIGNAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_COMPLETAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_SUSPENDRE) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_CONTINUAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_CANCELAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_ACCIO_EXECUTAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.TASCA_MARCAR_FINALITZAR);
	}
	@Transient
	public boolean isTargetProces() {
		return	accioTipus.equals(ExpedientRetroaccioTipus.PROCES_VARIABLE_CREAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.PROCES_VARIABLE_MODIFICAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.PROCES_VARIABLE_ESBORRAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.PROCES_DOCUMENT_AFEGIR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.PROCES_DOCUMENT_MODIFICAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.PROCES_DOCUMENT_ESBORRAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.PROCES_DOCUMENT_ADJUNTAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.PROCES_DOCUMENT_SIGNAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.PROCES_SCRIPT_EXECUTAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.PROCES_ACTUALITZAR);
	}
	@Transient
	public boolean isTargetExpedient() {
		return	accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_INICIAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_MODIFICAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_ATURAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_REPRENDRE) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_RELACIO_AFEGIR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_RELACIO_ESBORRAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_ACCIO) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_FINALITZAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_DESFINALITZAR) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_MIGRAR_ARXIU) ||
				accioTipus.equals(ExpedientRetroaccioTipus.EXPEDIENT_RETROCEDIR_TASQUES);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accioParams == null) ? 0 : accioParams.hashCode());
		result = prime * result
				+ ((accioTipus == null) ? 0 : accioTipus.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((expedient == null) ? 0 : expedient.hashCode());
		result = prime * result + ((usuari == null) ? 0 : usuari.hashCode());
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
		ExpedientLog other = (ExpedientLog) obj;
		if (accioParams == null) {
			if (other.accioParams != null)
				return false;
		} else if (!accioParams.equals(other.accioParams))
			return false;
		if (accioTipus != other.accioTipus)
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (expedient == null) {
			if (other.expedient != null)
				return false;
		} else if (!expedient.equals(other.expedient))
			return false;
		if (usuari == null) {
			if (other.usuari != null)
				return false;
		} else if (!usuari.equals(other.usuari))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
