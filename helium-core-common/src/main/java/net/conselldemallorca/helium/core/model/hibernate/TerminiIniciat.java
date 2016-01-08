/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import net.conselldemallorca.helium.core.common.TerminiStringUtil;

/**
 * Objecte de domini que representa un termini iniciat per a un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_termini_iniciat",
		uniqueConstraints={@UniqueConstraint(columnNames={"termini_id", "process_instance_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_termini_iniciat",
		indexes = @Index(name = "hel_terminic_termini_i", columnNames = {"termini_id"}))
public class TerminiIniciat implements Serializable, GenericEntity<Long> {

	public enum TerminiIniciatEstat {
		NORMAL,
		AVIS,
		COMPLETAT_TEMPS,
		CADUCAT,
		COMPLETAT_FORA
	}

	private Long id;
	@NotNull
	private Date dataInici;
	@NotNull
	private Date dataFi;
	private Date dataAturada;
	private Date dataCancelacio;
	private Date dataFiProrroga;
	private Date dataCompletat;
	private int diesAturat;
	private int anys;
	private int mesos;
	private int dies;
	@NotBlank
	@MaxLength(255)
	private String processInstanceId;
	@MaxLength(1024)
	private String timerIds;
	private String taskInstanceId;
	private boolean alertaPrevia;
	private boolean alertaFinal;
	private boolean alertaCompletat;

	@NotNull
	private Termini termini;

	private Set<Alerta> alertes = new HashSet<Alerta>();



	public TerminiIniciat() {}
	public TerminiIniciat(Termini termini, String processInstanceId, Date dataInici, Date dataFi) {
		this.termini = termini;
		this.anys = termini.getAnys();
		this.mesos = termini.getMesos();
		this.dies = termini.getDies();
		this.processInstanceId = processInstanceId;
		this.dataInici = dataInici;
		this.dataFi = dataFi;
	}
	public TerminiIniciat(Termini termini, int anys, int mesos, int dies, String processInstanceId, Date dataInici, Date dataFi) {
		this.termini = termini;
		this.anys = anys;
		this.mesos = mesos;
		this.dies = dies;
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

	@Column(name="data_fi_prorroga")
	@Temporal(TemporalType.DATE)
	public Date getDataFiProrroga() {
		return dataFiProrroga;
	}
	public void setDataFiProrroga(Date dataFiProrroga) {
		this.dataFiProrroga = dataFiProrroga;
	}

	@Column(name="data_completat")
	@Temporal(TemporalType.DATE)
	public Date getDataCompletat() {
		return dataCompletat;
	}
	public void setDataCompletat(Date dataCompletat) {
		this.dataCompletat = dataCompletat;
	}
	
	@Column(name="dies_aturat")
	public int getDiesAturat() {
		return diesAturat;
	}
	public void setDiesAturat(int diesAturat) {
		this.diesAturat = diesAturat;
	}

	@Column(name="anys")
	public int getAnys() {
		return anys;
	}
	public void setAnys(int anys) {
		this.anys = anys;
	}

	@Column(name="mesos")
	public int getMesos() {
		return mesos;
	}
	public void setMesos(int mesos) {
		this.mesos = mesos;
	}

	@Column(name="dies")
	public int getDies() {
		return dies;
	}
	public void setDies(int dies) {
		this.dies = dies;
	}

	@Transient
	public String getDurada() {
		TerminiStringUtil tsu = new TerminiStringUtil(
				anys,
				mesos,
				dies);
		if (dies > 0)
			return tsu.toString() + ((termini.isLaborable()) ? " laborables" : " naturals");
		else
			return tsu.toString();
	}

	@Column(name="process_instance_id", length=255, nullable=false)
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name="timer_ids", length=1024)
	public String getTimerIds() {
		return timerIds;
	}
	public void setTimerIds(String timerIds) {
		this.timerIds = timerIds;
	}

	@Column(name="task_instance_id", length=255)
	public String getTaskInstanceId() {
		return taskInstanceId;
	}
	public void setTaskInstanceId(String taskInstanceId) {
		this.taskInstanceId = taskInstanceId;
	}

	@Column(name="alerta_previa")
	public boolean isAlertaPrevia() {
		return alertaPrevia;
	}
	public void setAlertaPrevia(boolean alertaPrevia) {
		this.alertaPrevia = alertaPrevia;
	}

	@Column(name="alerta_final")
	public boolean isAlertaFinal() {
		return alertaFinal;
	}
	public void setAlertaFinal(boolean alertaFinal) {
		this.alertaFinal = alertaFinal;
	}
	
	@Column(name="alerta_completat")
	public boolean isAlertaCompletat() {
		return alertaCompletat;
	}
	public void setAlertaCompletat(boolean alertaCompletat) {
		this.alertaCompletat = alertaCompletat;
	}
	
	@ManyToOne(optional=false)
	@JoinColumn(name="termini_id")
	@ForeignKey(name="hel_termini_terminic_fk")
	public Termini getTermini() {
		return termini;
	}
	public void setTermini(Termini termini) {
		this.termini = termini;
	}

	@OneToMany(mappedBy="terminiIniciat", cascade=CascadeType.REMOVE)
	public Set<Alerta> getAlertes() {
		return this.alertes;
	}
	public void setAlertes(Set<Alerta> alertes) {
		this.alertes = alertes;
	}
	public void addAlerta(Alerta alerta) {
		getAlertes().add(alerta);
	}
	public void removeAlerta(Alerta alerta) {
		getAlertes().remove(alerta);
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
		if (dataFiProrroga != null)
			return dataFiProrroga;
		if (dataFi == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataFi);
		cal.add(Calendar.DAY_OF_MONTH, getNumDiesAturadaActual(new Date()));
		return cal.getTime();
	}
	@Transient
	public TerminiIniciatEstat getEstat() {
		Date dataFi = getDataFiAmbAturadaActual();
		if (dataCompletat != null) {
			if (dataCompletat.before(dataFi))
				return TerminiIniciatEstat.COMPLETAT_TEMPS;
			return TerminiIniciatEstat.COMPLETAT_FORA;
		}
		Date ara = new Date();
		if (ara.after(dataFi))
			return TerminiIniciatEstat.CADUCAT;
		if (termini.getDiesPrevisAvis() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFi);
			cal.add(Calendar.DAY_OF_MONTH, -termini.getDiesPrevisAvis().intValue());
			if (ara.after(cal.getTime()))
				return TerminiIniciatEstat.AVIS;
		}
		return TerminiIniciatEstat.NORMAL;
	}

	@Transient
	public void afegirTimerId(long id) {
		if (timerIds == null) {
			timerIds = new Long(id).toString();
		} else {
			if (!timerIds.contains(new Long(id).toString()))
				timerIds += "," + id;
		}
	}
	@Transient
	public void esborrarTimerId(long id) {
		if (timerIds != null) {
			timerIds.replaceAll(new Long(id).toString(), "");
			timerIds.replaceAll(",,", ",");
		}
	}
	@Transient
	public long[] getTimerIdsArray() {
		if (timerIds != null) {
			List<Long> ids = new ArrayList<Long>();
			for (String id: timerIds.split(",")) {
				if (id.length() > 0)
					ids.add(new Long(id));
			}
			long[] resposta = new long[ids.size()];
			for (int i = 0; i < ids.size(); i++)
				resposta[i] = ids.get(i).longValue();
			return resposta;
		}
		return new long[0];
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
