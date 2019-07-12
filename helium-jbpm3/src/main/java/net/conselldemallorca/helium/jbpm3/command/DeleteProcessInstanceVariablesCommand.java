/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import org.hibernate.Query;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Command per esborrar variables d'una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DeleteProcessInstanceVariablesCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String[] variables;

	public DeleteProcessInstanceVariablesCommand() {}

	public DeleteProcessInstanceVariablesCommand(long id, String[] variables){
		super();
		this.id = id;
		this.variables = variables;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance processInstance = jbpmContext.getProcessInstance(id);
		Query query = jbpmContext.getSession().createQuery(
				  "delete org.jbpm.logging.log.ProcessLog "
				+ "where id in ("
				+ "		select pl.id "
				+ "		from org.jbpm.logging.log.ProcessLog as pl "
				+ "		where pl.token.processInstance = :processInstance)");
		query.setEntity("processInstance", processInstance);
		query.executeUpdate();
		if (processInstance != null && variables != null) {
			for (int i = 0; i < variables.length; i++) {
				processInstance.getContextInstance().deleteVariable(variables[i]);
				//processInstance.getContextInstance().get(variables[i])  getVariable(variables[i]);
//				Query query2 = jbpmContext.getSession().createQuery(
//						  "delete org.jbpm.context.exe.VariableInstance "
//						+ "where id in ("
//						+ "		select vi.id "
//						+ "		from org.jbpm.context.exe.VariableInstance as vi "
//						+ "		where vi.processInstance = :processInstance"
//						+ "		and vi.name = :name)");
//				query2.setEntity("processInstance", processInstance);
//				query2.setString("name", variables[i]);
//				query2.executeUpdate();
			}
		}
		
		
		
		return null;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String[] getVariables() {
		return variables;
	}
	public void setVariables(String[] variables) {
		this.variables = variables;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

}
