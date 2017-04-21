package net.conselldemallorca.helium.core.api;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ResultatCompleteTask {

	private boolean completat;
	private boolean supervisat;
	private String tascaDelegadaId;
	
	public boolean isCompletat() {
		return completat;
	}
	public void setCompletat(boolean completat) {
		this.completat = completat;
	}
	public boolean isSupervisat() {
		return supervisat;
	}
	public void setSupervisat(boolean supervisat) {
		this.supervisat = supervisat;
	}
	public String getTascaDelegadaId() {
		return tascaDelegadaId;
	}
	public void setTascaDelegadaId(String tascaDelegadaId) {
		this.tascaDelegadaId = tascaDelegadaId;
	}

}
