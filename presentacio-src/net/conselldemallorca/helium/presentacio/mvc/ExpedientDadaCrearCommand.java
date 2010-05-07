/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.presentacio.mvc.util.BaseController;

/**
 * Command per afegir dades a un expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientDadaCrearCommand extends BaseController {

	private String id;
	private String taskId;
	private Camp camp;
	private String varCodi;
	private boolean modificar;



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public Camp getCamp() {
		return camp;
	}
	public void setCamp(Camp camp) {
		this.camp = camp;
	}
	public String getVarCodi() {
		return varCodi;
	}
	public void setVarCodi(String varCodi) {
		this.varCodi = varCodi;
	}
	public boolean isModificar() {
		return modificar;
	}
	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

}
