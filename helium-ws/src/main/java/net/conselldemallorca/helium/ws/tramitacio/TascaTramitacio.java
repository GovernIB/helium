/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Informació d'una tasca en tramitació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaTramitacio {

	private String id;
	private String codi;
	private String titol;
	private String expedient;
	private String missatgeInfo;
	private String missatgeWarn;
	private String responsable;
	private Set<String> responsables;
	
	private Date dataCreacio;
	private Date dataInici;
	private Date dataFi;
	private Date dataLimit;
	private int prioritat;

	private boolean open;
	private boolean completed;
	private boolean cancelled;
	private boolean suspended;

	private List<String> transicionsSortida;

	private String processInstanceId;



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getExpedient() {
		return expedient;
	}
	public void setExpedient(String expedient) {
		this.expedient = expedient;
	}
	public String getMissatgeInfo() {
		return missatgeInfo;
	}
	public void setMissatgeInfo(String missatgeInfo) {
		this.missatgeInfo = missatgeInfo;
	}
	public String getMissatgeWarn() {
		return missatgeWarn;
	}
	public void setMissatgeWarn(String missatgeWarn) {
		this.missatgeWarn = missatgeWarn;
	}
	public String getResponsable() {
		return responsable;
	}
	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}
	public Set<String> getResponsables() {
		return responsables;
	}
	public void setResponsables(Set<String> responsables) {
		this.responsables = responsables;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
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
	public Date getDataLimit() {
		return dataLimit;
	}
	public void setDataLimit(Date dataLimit) {
		this.dataLimit = dataLimit;
	}
	public int getPrioritat() {
		return prioritat;
	}
	public void setPrioritat(int prioritat) {
		this.prioritat = prioritat;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public boolean isSuspended() {
		return suspended;
	}
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
	public List<String> getTransicionsSortida() {
		return transicionsSortida;
	}
	public void setTransicionsSortida(List<String> transicionsSortida) {
		this.transicionsSortida = transicionsSortida;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

}
