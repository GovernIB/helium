/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import net.conselldemallorca.helium.api.service.WToken;
import org.jbpm.graph.exe.Token;

import java.util.Date;


/**
 * Un token jbpm de tasca multiversió
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JbpmToken implements WToken {

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

	@Override
	public String getId() {
		return new Long(token.getId()).toString();
	}
	@Override
	public String getName() {
		return token.getName();
	}
	@Override
	public String getFullName() {
		return token.getFullName();
	}
	@Override
	public String getNodeName() {
		if (token.getNode() == null)
			return null;
		else
			return token.getNode().getName();
	}
	@Override
	public String getNodeClass() {
		if (token.getNode() == null)
			return null;
		else
			return token.getNode().getClass().getName();
	}
	@Override
	public Date getStart() {
		return token.getStart();
	}
	@Override
	public Date getEnd() {
		return token.getEnd();
	}
	@Override
	public boolean isAbleToReactivateParent() {
		return token.isAbleToReactivateParent();
	}
	@Override
	public boolean isTerminationImplicit() {
		return token.isTerminationImplicit();
	}
	@Override
	public boolean isSuspended() {
		return token.isSuspended();
	}
	@Override
	public Date getNodeEnter() {
		return token.getNodeEnter();
	}
	@Override
	public boolean isRoot() {
		return token.isRoot();
	}
	@Override
	public String getParentTokenName() {
		if (token.getParent() != null)
			return token.getParent().getName();
		return null;
	}
	@Override
	public String getParentTokenFullName() {
		if (token.getParent() != null)
			return token.getParent().getFullName();
		return null;
	}
	@Override
	public String getProcessInstanceId() {
		return new Long(token.getProcessInstance().getId()).toString();
	}

}
