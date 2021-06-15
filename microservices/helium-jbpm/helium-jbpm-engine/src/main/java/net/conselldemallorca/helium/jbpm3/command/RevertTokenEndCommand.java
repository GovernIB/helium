/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;


import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
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
	private JbpmToken jtoken;
	private long id;

	public RevertTokenEndCommand(JbpmToken jtoken){
		super();
		this.jtoken = jtoken;
		this.id = Long.parseLong(jtoken.getId());
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		SQLQuery updateQuery = jbpmContext.getSession().createSQLQuery("update jbpm_token set end_=null where id_=?");
		updateQuery.setLong(0, id);
		updateQuery.executeUpdate();
		jbpmContext.getSession().refresh(jtoken.getToken());
		return null;
	}

	public JbpmToken getJtoken() {
		return jtoken;
	}

	public void setJtoken(JbpmToken jtoken) {
		this.jtoken = jtoken;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

}
