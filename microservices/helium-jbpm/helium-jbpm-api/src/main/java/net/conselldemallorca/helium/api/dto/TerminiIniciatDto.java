/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

/**
 * DTO amb informació d'un termini en execució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class TerminiIniciatDto {
	public enum TerminiIniciatEstat {
		NORMAL,
		AVIS,
		COMPLETAT_TEMPS,
		CADUCAT,
		COMPLETAT_FORA
	}
	
	private Long id;
	private Date dataInici;
	private Date dataFi;
	private Date dataAturada;
	private Date dataCancelacio;
	private Date dataFiProrroga;
	private Date dataCompletat;
	private int diesAturat;
	private int anys;
	private int mesos;
	private int dies;
	private String taskInstanceId;
	private TerminiDto termini;


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
	
	public int getNumDiesAturadaActual(Date data) {
		if (getDataAturada() == null)
			return 0;
		long milisegonsUnDia = 1000 * 60 * 60 * 24;
		long inici = getDataAturada().getTime();
		long fi = data.getTime();
		return new Long((fi - inici) / milisegonsUnDia).intValue();
	}

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

	public String getDurada() {
		return toString() + ((dies > 0) ? ((getTermini().isLaborable()) ? " laborables" : " naturals") : "");
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		boolean plural = false;
		if (anys > 0) {
			sb.append(anys);
			plural = anys > 1;
			sb.append((plural) ? " anys": " any");
			if (mesos > 0 && dies > 0)
				sb.append(", ");
			else if (mesos > 0 || dies > 0)
				sb.append(" i ");
		}
		if (mesos > 0) {
			sb.append(mesos);
			plural = mesos > 1;
			sb.append((plural) ? " mesos": " mes");
			if (dies > 0)
				sb.append(" i ");
		}
		if (dies > 0) {
			sb.append(dies);
			plural = dies > 1;
			sb.append((plural) ? " dies": " dia");
		}
		return sb.toString();
	}
}
