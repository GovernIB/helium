/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Date;

import org.jbpm.graph.exe.Token;


/**
 * Un token jbpm de tasca multiversió
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class JbpmToken {

	private Token token;



	public JbpmToken(Token token) {
		this.token = token;
	}

	public Token getToken() {
		return token;
	}
	public void setToken(Token token) {
		this.token = token;
	}

	public String getId() {
		return new Long(token.getId()).toString();
	}
	public String getName() {
		return token.getName();
	}
	public String getFullName() {
		return token.getFullName();
	}
	public String getNodeName() {
		return token.getNode().getName();
	}
	public Date getStart() {
		return token.getStart();
	}
	public Date getEnd() {
		return token.getEnd();
	}
	public boolean isAbleToReactivateParent() {
		return token.isAbleToReactivateParent();
	}
	public boolean isTerminationImplicit() {
		return token.isTerminationImplicit();
	}
	public boolean isSuspended() {
		return token.isSuspended();
	}
	public Date getNodeEnter() {
		return token.getNodeEnter();
	}
	public boolean isRoot() {
		return token.isRoot();
	}
	public String getParentTokenName() {
		if (token.getParent() != null)
			return token.getParent().getName();
		return null;
	}
	public String getParentTokenFullName() {
		if (token.getParent() != null)
			return token.getParent().getFullName();
		return null;
	}
	public String getProcessInstanceId() {
		return new Long(token.getProcessInstance().getId()).toString();
	}

}
