package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;


/**
 * DTO amb informació d'un token.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TokenDto {
	
	private String id;
	private String fullName;
	private String parentTokenFullName;
	private Date start;
	private Date end;
	private String nodeName;
	private boolean ableToReactivateParent;
	private boolean terminationImplicit;
	private boolean suspended;
	private boolean root;
	private Date nodeEnter;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getParentTokenFullName() {
		return parentTokenFullName;
	}
	public void setParentTokenFullName(String parentTokenFullName) {
		this.parentTokenFullName = parentTokenFullName;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public boolean isAbleToReactivateParent() {
		return ableToReactivateParent;
	}
	public void setAbleToReactivateParent(boolean ableToReactivateParent) {
		this.ableToReactivateParent = ableToReactivateParent;
	}
	public boolean isTerminationImplicit() {
		return terminationImplicit;
	}
	public void setTerminationImplicit(boolean terminationImplicit) {
		this.terminationImplicit = terminationImplicit;
	}
	public boolean isSuspended() {
		return suspended;
	}
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
	public boolean isRoot() {
		return root;
	}
	public void setRoot(boolean root) {
		this.root = root;
	}
	public Date getNodeEnter() {
		return nodeEnter;
	}
	public void setNodeEnter(Date nodeEnter) {
		this.nodeEnter = nodeEnter;
	}

}
