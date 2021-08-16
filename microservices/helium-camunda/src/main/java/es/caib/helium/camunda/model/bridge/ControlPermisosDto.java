/**
 * 
 */
package es.caib.helium.camunda.model.bridge;

import java.io.Serializable;

/**
 * Objecte base per a controlar els permisos d'accés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class ControlPermisosDto implements Serializable {

	protected boolean permisRead; // 1
	protected boolean permisWrite; // 2
	protected boolean permisCreate; // 4
	protected boolean permisDelete; // 8
	protected boolean permisAdministration; // 16

	private boolean permisCancel;
	private boolean permisStop;
	private boolean permisRelate;
	private boolean permisDataManagement;
	private boolean permisDocManagement;
	private boolean permisTermManagement;
	private boolean permisTaskManagement;
	private boolean permisTaskSupervision;
	private boolean permisTaskAssign;
	private boolean permisLogRead;
	private boolean permisLogManage;
	private boolean permisTokenRead;
	private boolean permisTokenManage;
	private boolean permisDesignAdmin;
	private boolean permisDesignDeleg;
	private boolean permisScriptExe;
	private boolean permisUndoEnd;
	private boolean permisDefprocUpdate;

	protected boolean permisDesign; // 32
	protected boolean permisOrganization; // 64
	protected boolean permisSupervision; // 128
	protected boolean permisManage; // 256
	protected boolean permisReassignment; // 512



	public boolean isPermisRead() {
		return permisRead;
	}
	public void setPermisRead(boolean permisRead) {
		this.permisRead = permisRead;
	}
	public boolean isPermisWrite() {
		return permisWrite;
	}
	public void setPermisWrite(boolean permisWrite) {
		this.permisWrite = permisWrite;
	}
	public boolean isPermisCreate() {
		return permisCreate;
	}
	public void setPermisCreate(boolean permisCreate) {
		this.permisCreate = permisCreate;
	}
	public boolean isPermisDelete() {
		return permisDelete;
	}
	public void setPermisDelete(boolean permisDelete) {
		this.permisDelete = permisDelete;
	}
	public boolean isPermisAdministration() {
		return permisAdministration;
	}
	public void setPermisAdministration(boolean permisAdministration) {
		this.permisAdministration = permisAdministration;
	}
	public boolean isPermisCancel() {
		return permisCancel;
	}
	public void setPermisCancel(boolean permisCancel) {
		this.permisCancel = permisCancel;
	}
	public boolean isPermisStop() {
		return permisStop;
	}
	public void setPermisStop(boolean permisStop) {
		this.permisStop = permisStop;
	}
	public boolean isPermisRelate() {
		return permisRelate;
	}
	public void setPermisRelate(boolean permisRelate) {
		this.permisRelate = permisRelate;
	}
	public boolean isPermisDataManagement() {
		return permisDataManagement;
	}
	public void setPermisDataManagement(boolean permisDataManagement) {
		this.permisDataManagement = permisDataManagement;
	}
	public boolean isPermisDocManagement() {
		return permisDocManagement;
	}
	public void setPermisDocManagement(boolean permisDocManagement) {
		this.permisDocManagement = permisDocManagement;
	}
	public boolean isPermisTermManagement() {
		return permisTermManagement;
	}
	public void setPermisTermManagement(boolean permisTermManagement) {
		this.permisTermManagement = permisTermManagement;
	}
	public boolean isPermisTaskManagement() {
		return permisTaskManagement;
	}
	public void setPermisTaskManagement(boolean permisTaskManagement) {
		this.permisTaskManagement = permisTaskManagement;
	}
	public boolean isPermisTaskSupervision() {
		return permisTaskSupervision;
	}
	public void setPermisTaskSupervision(boolean permisTaskSupervision) {
		this.permisTaskSupervision = permisTaskSupervision;
	}
	public boolean isPermisTaskAssign() {
		return permisTaskAssign;
	}
	public void setPermisTaskAssign(boolean permisTaskAssign) {
		this.permisTaskAssign = permisTaskAssign;
	}
	public boolean isPermisLogRead() {
		return permisLogRead;
	}
	public void setPermisLogRead(boolean permisLogRead) {
		this.permisLogRead = permisLogRead;
	}
	public boolean isPermisLogManage() {
		return permisLogManage;
	}
	public void setPermisLogManage(boolean permisLogManage) {
		this.permisLogManage = permisLogManage;
	}
	public boolean isPermisTokenRead() {
		return permisTokenRead;
	}
	public void setPermisTokenRead(boolean permisTokenRead) {
		this.permisTokenRead = permisTokenRead;
	}
	public boolean isPermisTokenManage() {
		return permisTokenManage;
	}
	public void setPermisTokenManage(boolean permisTokenManage) {
		this.permisTokenManage = permisTokenManage;
	}
	public boolean isPermisDesignAdmin() {
		return permisDesignAdmin;
	}
	public void setPermisDesignAdmin(boolean permisDesignAdmin) {
		this.permisDesignAdmin = permisDesignAdmin;
	}
	public boolean isPermisDesignDeleg() {
		return permisDesignDeleg;
	}
	public void setPermisDesignDeleg(boolean permisDesignDeleg) {
		this.permisDesignDeleg = permisDesignDeleg;
	}
	public boolean isPermisScriptExe() {
		return permisScriptExe;
	}
	public void setPermisScriptExe(boolean permisScriptExe) {
		this.permisScriptExe = permisScriptExe;
	}
	public boolean isPermisUndoEnd() {
		return permisUndoEnd;
	}
	public void setPermisUndoEnd(boolean permisUndoEnd) {
		this.permisUndoEnd = permisUndoEnd;
	}
	public boolean isPermisDefprocUpdate() {
		return permisDefprocUpdate;
	}
	public void setPermisDefprocUpdate(boolean permisDefprocUpdate) {
		this.permisDefprocUpdate = permisDefprocUpdate;
	}
	public boolean isPermisDesign() {
		return permisDesign;
	}
	public void setPermisDesign(boolean permisDesign) {
		this.permisDesign = permisDesign;
	}
	public boolean isPermisOrganization() {
		return permisOrganization;
	}
	public void setPermisOrganization(boolean permisOrganization) {
		this.permisOrganization = permisOrganization;
	}
	public boolean isPermisSupervision() {
		return permisSupervision;
	}
	public void setPermisSupervision(boolean permisSupervision) {
		this.permisSupervision = permisSupervision;
	}
	public boolean isPermisManage() {
		return permisManage;
	}
	public void setPermisManage(boolean permisManage) {
		this.permisManage = permisManage;
	}
	public boolean isPermisReassignment() {
		return permisReassignment;
	}
	public void setPermisReassignment(boolean permisReassignment) {
		this.permisReassignment = permisReassignment;
	}

	private static final long serialVersionUID = 4183337699114324409L;

}
