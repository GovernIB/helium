package net.conselldemallorca.helium.webapp.exportacio;

import java.util.Date;

/** Dades de les instànces de processos */
public class ProcesExportacio {

	private String id;
	private long expedientId;
	private String processDefinitionId;
	private String procesArrelId;
	private String procesPareId;
	private String descripcio;
	private Date dataInici;
	private Date dataFi;
	private boolean suspes;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(long expedientId) {
		this.expedientId = expedientId;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public String getProcesArrelId() {
		return procesArrelId;
	}
	public void setProcesArrelId(String procesArrelId) {
		this.procesArrelId = procesArrelId;
	}
	public String getProcesPareId() {
		return procesPareId;
	}
	public void setProcesPareId(String procesPareId) {
		this.procesPareId = procesPareId;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
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
	public boolean isSuspes() {
		return suspes;
	}
	public void setSuspes(boolean suspes) {
		this.suspes = suspes;
	}
}