package es.caib.helium.bpmn.model;

import java.util.Date;

/**
 * Termini iniciat en una instància de procés: durada i dates.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TerminiInfo {

	private long id;
	private String codi;
	private int anys = 0;
	private int mesos = 0;
	private int dies = 0;
	private Date dataInici;
	private Date dataFi;
	private Date dataAturada;
	private Date dataCancelacio;

	public TerminiInfo() {
	}

	public TerminiInfo(int anys, int mesos, int dies) {
		super();
		this.anys = anys;
		this.mesos = mesos;
		this.dies = dies;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	/** Codi del termini, tal com està definit al tipus d'expedient o a la definició de procés. */
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
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
	/** Data en què s'ha pausat el termini, si està pausat. */
	public Date getDataAturada() {
		return dataAturada;
	}
	public void setDataAturada(Date dataAturada) {
		this.dataAturada = dataAturada;
	}
	/** Data en què s'ha cancel·lat el termini, si està cancel·lat. */
	public Date getDataCancelacio() {
		return dataCancelacio;
	}
	public void setDataCancelacio(Date dataCancelacio) {
		this.dataCancelacio = dataCancelacio;
	}

}
