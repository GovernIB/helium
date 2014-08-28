/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * Objecte base per a controlar els permisos d'accés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class ControlPermisosDto implements Serializable {

	protected boolean permisCreate;
	protected boolean permisRead;
	protected boolean permisWrite;
	protected boolean permisDelete;
	
	protected boolean permisSupervision;
	protected boolean permisReassignment;



	public boolean isPermisCreate() {
		return permisCreate;
	}
	public void setPermisCreate(boolean permisCreate) {
		this.permisCreate = permisCreate;
	}
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
	public boolean isPermisDelete() {
		return permisDelete;
	}
	public void setPermisDelete(boolean permisDelete) {
		this.permisDelete = permisDelete;
	}
	public boolean isPermisSupervision() {
		return permisSupervision;
	}
	public void setPermisSupervision(boolean permisSupervision) {
		this.permisSupervision = permisSupervision;
	}
	public boolean isPermisReassignment() {
		return permisReassignment;
	}
	public void setPermisReassignment(boolean permisReassignment) {
		this.permisReassignment = permisReassignment;
	}

	private static final long serialVersionUID = 4183337699114324409L;

}
