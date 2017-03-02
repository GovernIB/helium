/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Date;

import org.jbpm.graph.exe.Token;

import net.conselldemallorca.helium.core.api.WToken;


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

	@Override
	public Token getToken() {
		return token;
	}
	public void setToken(Token token) {
		this.token = token;
	}

	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getId()
	 */
	@Override
	public String getId() {
		return new Long(token.getId()).toString();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getName()
	 */
	@Override
	public String getName() {
		return token.getName();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getFullName()
	 */
	@Override
	public String getFullName() {
		return token.getFullName();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getNodeName()
	 */
	@Override
	public String getNodeName() {
		if (token.getNode() == null)
			return null;
		else
			return token.getNode().getName();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getNodeClass()
	 */
	@Override
	public String getNodeClass() {
		if (token.getNode() == null)
			return null;
		else
			return token.getNode().getClass().getName();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getStart()
	 */
	@Override
	public Date getStart() {
		return token.getStart();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getEnd()
	 */
	@Override
	public Date getEnd() {
		return token.getEnd();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#isAbleToReactivateParent()
	 */
	@Override
	public boolean isAbleToReactivateParent() {
		return token.isAbleToReactivateParent();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#isTerminationImplicit()
	 */
	@Override
	public boolean isTerminationImplicit() {
		return token.isTerminationImplicit();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#isSuspended()
	 */
	@Override
	public boolean isSuspended() {
		return token.isSuspended();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getNodeEnter()
	 */
	@Override
	public Date getNodeEnter() {
		return token.getNodeEnter();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#isRoot()
	 */
	@Override
	public boolean isRoot() {
		return token.isRoot();
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getParentTokenName()
	 */
	@Override
	public String getParentTokenName() {
		if (token.getParent() != null)
			return token.getParent().getName();
		return null;
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getParentTokenFullName()
	 */
	@Override
	public String getParentTokenFullName() {
		if (token.getParent() != null)
			return token.getParent().getFullName();
		return null;
	}
	/* (non-Javadoc)
	 * @see net.conselldemallorca.helium.jbpm3.integracio.WToken#getProcessInstanceId()
	 */
	@Override
	public String getProcessInstanceId() {
		return new Long(token.getProcessInstance().getId()).toString();
	}

}
