/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.Date;

import org.hibernate.SQLQuery;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.exe.Token;

/**
 * Command per activar un token donat el seu id
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TokenActivarCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long tokenId;
	private boolean activar;

	public TokenActivarCommand(long tokenId, boolean activar) {
		super();
		this.tokenId = tokenId;
		this.setActivar(activar);
	}

	public TokenActivarCommand(long tokenId){
		super();
		this.tokenId = tokenId;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Token token = jbpmContext.getToken(tokenId);
		SQLQuery updateQuery = jbpmContext.getSession().createSQLQuery(
				"update jbpm_token set end_ = ? where id_ = ?");
		updateQuery.setTimestamp(0, activar ? null : new Date());
		updateQuery.setLong(1, tokenId);
		int result = updateQuery.executeUpdate();
		jbpmContext.getSession().refresh(token);
		return result;
	}

	public long getId() {
		return tokenId;
	}
	public void setId(long tokenId) {
		this.tokenId = tokenId;
	}

	public boolean isActivar() {
		return activar;
	}

	public void setActivar(boolean activar) {
		this.activar = activar;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "tokenId=" + tokenId + ", activar=" + activar;
	}

	// methods for fluent programming
	public TokenActivarCommand tokenId(long tokenId) {
		setId(tokenId);
	    return this;
	}
}
