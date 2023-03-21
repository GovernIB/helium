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
 * Objecte de domini que representa una entrada de log d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_expedient_log")
public class ExpedientLog implements Serializable, GenericEntity<Long> {

	private static int MAX_PARAMETERS_LENGTH = 2048;
	
	public enum ExpedientLogAccioTipus {
		PROCES_VARIABLE_CREAR,			// 0
		PROCES_VARIABLE_MODIFICAR,
		PROCES_VARIABLE_ESBORRAR,
		PROCES_DOCUMENT_AFEGIR,
		PROCES_DOCUMENT_MODIFICAR,
		PROCES_DOCUMENT_ESBORRAR,		// 5
		PROCES_DOCUMENT_ADJUNTAR,
		PROCES_SCRIPT_EXECUTAR,
		PROCES_ACTUALITZAR,
		TASCA_REASSIGNAR,
		TASCA_FORM_GUARDAR,				// 10
		TASCA_FORM_VALIDAR,
		TASCA_FORM_RESTAURAR,
		TASCA_ACCIO_EXECUTAR,
		TASCA_DOCUMENT_AFEGIR,
		TASCA_DOCUMENT_MODIFICAR,		// 15
		TASCA_DOCUMENT_ESBORRAR,
		TASCA_DOCUMENT_SIGNAR,
		TASCA_COMPLETAR,
		TASCA_SUSPENDRE,
		TASCA_CONTINUAR,				// 20
		TASCA_CANCELAR,
		TASCA_MARCAR_FINALITZAR,
		EXPEDIENT_INICIAR,
		EXPEDIENT_MODIFICAR,
		EXPEDIENT_ATURAR,				// 25
		EXPEDIENT_REPRENDRE,
		EXPEDIENT_RELACIO_AFEGIR,
		EXPEDIENT_RELACIO_ESBORRAR,
		EXPEDIENT_ACCIO,
		EXPEDIENT_RETROCEDIR,			// 30
		PROCES_DOCUMENT_PORTAFIRMES,
		EXPEDIENT_RETROCEDIR_TASQUES,
		PROCES_LLAMAR_SUBPROCES,
		EXPEDIENT_FINALITZAR,
		EXPEDIENT_DESFINALITZAR,		// 35
		EXPEDIENT_MIGRAR_ARXIU,
		ANOTACIO_RELACIONAR,
		PROCES_DOCUMENT_FIRMAR
		}			

	public enum ExpedientLogEstat {
		NORMAL,
		RETROCEDIT,
		IGNORAR,
		BLOCAR,
		RETROCEDIT_TASQUES}
	
	public enum LogInfo {
		NUMERO,
		TITOL,
		RESPONSABLE,
		INICI,
		COMENTARI,
		ESTAT,
		GEOPOSICIOX,
		GEOPOSICIOY,
		GEOREFERENCIA,
		GRUP}

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
	private ExpedientLogEstat estat = ExpedientLogEstat.NORMAL;
	@NotNull
	private ExpedientLogAccioTipus accioTipus;

	@NotNull
	private Expedient expedient;
	private Long iniciadorRetroces;

	public ExpedientLog() {}
	public ExpedientLog(
			Expedient expedient,
			String usuari,
			String targetId,
			ExpedientLogAccioTipus accioTipus) {
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
	public ExpedientLogEstat getEstat() {
		return estat;
	}
	public void setEstat(ExpedientLogEstat estat) {
		this.estat = estat;
	}

	@Column(name="accio_tipus", nullable=false)
	public ExpedientLogAccioTipus getAccioTipus() {
		return accioTipus;
	}
	public void setAccioTipus(ExpedientLogAccioTipus accioTipus) {
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
		return	accioTipus.equals(ExpedientLogAccioTipus.TASCA_REASSIGNAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_FORM_GUARDAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_FORM_VALIDAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_FORM_RESTAURAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_DOCUMENT_AFEGIR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_DOCUMENT_MODIFICAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_DOCUMENT_ESBORRAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_DOCUMENT_SIGNAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_COMPLETAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_SUSPENDRE) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_CONTINUAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_CANCELAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_ACCIO_EXECUTAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.TASCA_MARCAR_FINALITZAR);
	}
	@Transient
	public boolean isTargetProces() {
		return	accioTipus.equals(ExpedientLogAccioTipus.PROCES_VARIABLE_CREAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.PROCES_VARIABLE_ESBORRAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.PROCES_DOCUMENT_AFEGIR) ||
				accioTipus.equals(ExpedientLogAccioTipus.PROCES_DOCUMENT_MODIFICAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.PROCES_DOCUMENT_ESBORRAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.PROCES_DOCUMENT_ADJUNTAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.PROCES_DOCUMENT_PORTAFIRMES) ||
				accioTipus.equals(ExpedientLogAccioTipus.PROCES_SCRIPT_EXECUTAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.PROCES_ACTUALITZAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.PROCES_DOCUMENT_FIRMAR);
	}
	@Transient
	public boolean isTargetExpedient() {
		return	accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_INICIAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_MODIFICAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_ATURAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_RELACIO_ESBORRAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_ACCIO) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_FINALITZAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_DESFINALITZAR) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_MIGRAR_ARXIU) ||
				accioTipus.equals(ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR_TASQUES);
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
