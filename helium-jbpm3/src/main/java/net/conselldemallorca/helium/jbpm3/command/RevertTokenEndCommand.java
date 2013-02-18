/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import org.hibernate.SQLQuery;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;

/**
 * Command per a retrocedir la finalització d'un token
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RevertTokenEndCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public RevertTokenEndCommand(long id){
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		SQLQuery updateQuery = jbpmContext.getSession().createSQLQuery("update jbpm_token set end_=null where id_=?");
		updateQuery.setLong(0, id);
		updateQuery.executeUpdate();
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

}
