/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Calendar;
import java.util.Date;

/**
 * DTO amb informació d'un termini en execució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	
	private TerminiDto termini;

	public TerminiDto getTermini() {
		return termini;
	}
	public void setTermini(TerminiDto termini) {
		this.termini = termini;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public Date getDataAturada() {
		return dataAturada;
	}
	public void setDataAturada(Date dataAturada) {
		this.dataAturada = dataAturada;
	}
	public Date getDataCancelacio() {
		return dataCancelacio;
	}
	public void setDataCancelacio(Date dataCancelacio) {
		this.dataCancelacio = dataCancelacio;
	}
	public Date getDataFiProrroga() {
		return dataFiProrroga;
	}
	public void setDataFiProrroga(Date dataFiProrroga) {
		this.dataFiProrroga = dataFiProrroga;
	}
	public Date getDataCompletat() {
		return dataCompletat;
	}
	public void setDataCompletat(Date dataCompletat) {
		this.dataCompletat = dataCompletat;
	}
	public int getDiesAturat() {
		return diesAturat;
	}
	public void setDiesAturat(int diesAturat) {
		this.diesAturat = diesAturat;
	}
	public int getAnys() {
		return anys;
	}
	public void setAnys(int anys) {
		this.anys = anys;
	}
	public int getMesos() {
		return mesos;
	}
	public void setMesos(int mesos) {
		this.mesos = mesos;
	}
	public int getDies() {
		return dies;
	}
	public void setDies(int dies) {
		this.dies = dies;
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
