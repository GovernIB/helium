/**
 * 
 */
package es.caib.helium.jbpm3.command;

import org.hibernate.SQLQuery;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;

/**
 * Command per a retornar l'id de la variable associat amb un log
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetVariableIdFromVariableLogCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public GetVariableIdFromVariableLogCommand(long id) {
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		SQLQuery q = jbpmContext.getSession().createSQLQuery("select l.variableinstance_ from jbpm_log l where l.id_=?");
		q.setLong(0, id);
		Number result = (Number)q.uniqueResult();
		if (result != null)
			return new Long(result.longValue());
		else
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
