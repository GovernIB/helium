/**
 * 
 */
package es.caib.helium.jbpm3.command;

import org.hibernate.SQLQuery;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;

/**
 * Command per a retornar l'id de la instancia de tasca associat amb una variable
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GetTaskIdFromVariableLogCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;

	public GetTaskIdFromVariableLogCommand(long id) {
		super();
		this.id = id;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		SQLQuery q = jbpmContext.getSession().createSQLQuery("select v.taskinstance_ from jbpm_log l, jbpm_variableinstance v where l.id_=? and v.id_=l.variableinstance_");
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
