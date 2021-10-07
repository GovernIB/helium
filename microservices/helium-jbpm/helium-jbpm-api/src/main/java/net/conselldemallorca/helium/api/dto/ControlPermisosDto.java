/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Objecte base per a controlar els permisos d'accés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
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

	private static final long serialVersionUID = 4183337699114324409L;

}
