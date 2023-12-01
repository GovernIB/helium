/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import net.conselldemallorca.helium.v3.core.api.dto.PrincipalTipusEnumDto;

/**
 * Command per al manteniment de permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String principalNom;
	@NotNull
	private PrincipalTipusEnumDto principalTipus;
	@NotEmpty
	private String unitatOrganitzativaCodiNom;

	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;

	private boolean cancel;
	private boolean stop;
	private boolean relate;
	private boolean dataManagement;
	private boolean docManagement;
	private boolean termManagement;
	private boolean taskManagement;
	private boolean taskSupervision;
	private boolean taskAssign;
	private boolean logRead;
	private boolean logManage;
	private boolean tokenRead;
	private boolean tokenManage;
	private boolean designAdmin;
	private boolean designDeleg;
	private boolean scriptExe;
	private boolean undoEnd;
	private boolean defprocUpdate;

	/* Permisos antics */
	private boolean design;
	private boolean organization;
	private boolean supervision;
	private boolean manage;
	private boolean reassignment;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPrincipalNom() {
		return principalNom;
	}
	public void setPrincipalNom(String principalNom) {
		this.principalNom = principalNom;
	}
	public PrincipalTipusEnumDto getPrincipalTipus() {
		return principalTipus;
	}
	public void setPrincipalTipus(PrincipalTipusEnumDto principalTipus) {
		this.principalTipus = principalTipus;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public boolean isWrite() {
		return write;
	}
	public void setWrite(boolean write) {
		this.write = write;
	}
	public boolean isCreate() {
		return create;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public boolean isAdministration() {
		return administration;
	}
	public void setAdministration(boolean administration) {
		this.administration = administration;
	}
	public boolean isCancel() {
		return cancel;
	}
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}
	public boolean isStop() {
		return stop;
	}
	public void setStop(boolean stop) {
		this.stop = stop;
	}
	public boolean isRelate() {
		return relate;
	}
	public void setRelate(boolean relate) {
		this.relate = relate;
	}
	public boolean isDataManagement() {
		return dataManagement;
	}
	public void setDataManagement(boolean dataManagement) {
		this.dataManagement = dataManagement;
	}
	public boolean isDocManagement() {
		return docManagement;
	}
	public void setDocManagement(boolean docManagement) {
		this.docManagement = docManagement;
	}
	public boolean isTermManagement() {
		return termManagement;
	}
	public void setTermManagement(boolean termManagement) {
		this.termManagement = termManagement;
	}
	public boolean isTaskManagement() {
		return taskManagement;
	}
	public void setTaskManagement(boolean taskManagement) {
		this.taskManagement = taskManagement;
	}
	public boolean isTaskSupervision() {
		return taskSupervision;
	}
	public void setTaskSupervision(boolean taskSupervision) {
		this.taskSupervision = taskSupervision;
	}
	public boolean isTaskAssign() {
		return taskAssign;
	}
	public void setTaskAssign(boolean taskAssign) {
		this.taskAssign = taskAssign;
	}
	public boolean isLogRead() {
		return logRead;
	}
	public void setLogRead(boolean logRead) {
		this.logRead = logRead;
	}
	public boolean isLogManage() {
		return logManage;
	}
	public void setLogManage(boolean logManage) {
		this.logManage = logManage;
	}
	public boolean isTokenRead() {
		return tokenRead;
	}
	public void setTokenRead(boolean tokenRead) {
		this.tokenRead = tokenRead;
	}
	public boolean isTokenManage() {
		return tokenManage;
	}
	public void setTokenManage(boolean tokenManage) {
		this.tokenManage = tokenManage;
	}
	public boolean isDesignAdmin() {
		return designAdmin;
	}
	public void setDesignAdmin(boolean designAdmin) {
		this.designAdmin = designAdmin;
	}
	public boolean isDesignDeleg() {
		return designDeleg;
	}
	public void setDesignDeleg(boolean designDeleg) {
		this.designDeleg = designDeleg;
	}
	public boolean isScriptExe() {
		return scriptExe;
	}
	public void setScriptExe(boolean scriptExe) {
		this.scriptExe = scriptExe;
	}
	public boolean isUndoEnd() {
		return undoEnd;
	}
	public void setUndoEnd(boolean undoEnd) {
		this.undoEnd = undoEnd;
	}
	public boolean isDefprocUpdate() {
		return defprocUpdate;
	}
	public void setDefprocUpdate(boolean defprocUpdate) {
		this.defprocUpdate = defprocUpdate;
	}
	public boolean isDesign() {
		return design;
	}
	public void setDesign(boolean design) {
		this.design = design;
	}
	public boolean isOrganization() {
		return organization;
	}
	public void setOrganization(boolean organization) {
		this.organization = organization;
	}
	public boolean isSupervision() {
		return supervision;
	}
	public void setSupervision(boolean supervision) {
		this.supervision = supervision;
	}
	public boolean isManage() {
		return manage;
	}
	public void setManage(boolean manage) {
		this.manage = manage;
	}
	public boolean isReassignment() {
		return reassignment;
	}
	public void setReassignment(boolean reassignment) {
		this.reassignment = reassignment;
	}

	public String getUnitatOrganitzativaCodiNom() {
		return unitatOrganitzativaCodiNom;
	}
	public void setUnitatOrganitzativaCodiNom(String unitatOrganitzativaCodiNom) {
		this.unitatOrganitzativaCodiNom = unitatOrganitzativaCodiNom;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}