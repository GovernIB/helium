/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisDto implements Serializable {

	private Long id;
	private String principalNom;
	private PrincipalTipusEnumDto principalTipus;

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
	private boolean goBack;
	private boolean designAdmin;
	private boolean designDeleg;
	private boolean scriptExe;
	private boolean undoEnd;

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
	public boolean isGoBack() {
		return goBack;
	}
	public void setGoBack(boolean goBack) {
		this.goBack = goBack;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
