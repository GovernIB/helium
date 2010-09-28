/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

/**
 * Command per a la delegació de tasques
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class TascaDelegacioCommand {

	private String taskId;
	private String actorId;
	private String comentari;
	private boolean supervisada = true;



	public TascaDelegacioCommand() {}

	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}
	public boolean isSupervisada() {
		return supervisada;
	}
	public void setSupervisada(boolean supervisada) {
		this.supervisada = supervisada;
	}

}
