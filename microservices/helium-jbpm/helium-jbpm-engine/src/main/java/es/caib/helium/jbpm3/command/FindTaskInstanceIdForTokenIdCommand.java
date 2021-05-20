/**
 * 
 */
package es.caib.helium.jbpm3.command;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;

/**
 * Command per a cercar instancies de tasca actives que
 * pertanyen a la mateixa tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FindTaskInstanceIdForTokenIdCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long tokenId;

	public FindTaskInstanceIdForTokenIdCommand(long tokenId) {
		super();
		this.tokenId = tokenId;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		Session session = jbpmContext.getSession();
		Query query = session.createQuery(
				"select ti.id " + 
				"from " +
				"	 org.jbpm.taskmgmt.exe.TaskInstance ti " +		
				"where " +
				"	 ti.token.id = :tokenId " +
				"and ti.marcadaFinalitzar is not null " + 
				"order by ti.end desc");
		query.setParameter("tokenId", this.tokenId);
		query.setFirstResult(0);
		query.setMaxResults(1);
		Number result = (Number)query.uniqueResult();
		
		if (result != null)
			return new Long(result.longValue());
		else
			return null;
	}
	
	public long getTokenId() {
		return tokenId;
	}
	public void setTokenId(long tokenId) {
		this.tokenId = tokenId;
	}
}

	



