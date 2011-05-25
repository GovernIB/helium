/**
 * 
 */
package net.conselldemallorca.helium.core.model.dto;

import java.util.Date;


/**
 * DTO amb informació d'una token
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TokenDto {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String fullName;
	private String parentName;
	private String parentFullName;
	private Date start;
	private Date end;
	private String nodeName;
	private boolean ableToReactivateParent;
	private boolean terminationImplicit;
	private boolean suspended;
	private boolean root;
	private Date nodeEnter;
	private int nodePosX;
	private int nodePosY;
	private int nodeWidth;
	private int nodeHeight;



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getParentFullName() {
		return parentFullName;
	}
	public void setParentFullName(String parentFullName) {
		this.parentFullName = parentFullName;
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
	public int getNodePosX() {
		return nodePosX;
	}
	public void setNodePosX(int nodePosX) {
		this.nodePosX = nodePosX;
	}
	public int getNodePosY() {
		return nodePosY;
	}
	public void setNodePosY(int nodePosY) {
		this.nodePosY = nodePosY;
	}
	public int getNodeWidth() {
		return nodeWidth;
	}
	public void setNodeWidth(int nodeWidth) {
		this.nodeWidth = nodeWidth;
	}
	public int getNodeHeight() {
		return nodeHeight;
	}
	public void setNodeHeight(int nodeHeight) {
		this.nodeHeight = nodeHeight;
	}

}
