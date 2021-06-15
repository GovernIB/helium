/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import org.hibernate.SQLQuery;
import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractGetObjectBaseCommand;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * Command per actualitzar els nodes de tipus ProcessState de la definició de procés per a que
 * apuntin correctament al processDefinition correcte segons el codi de la subdefinicó de procés.
 * Serveix per resoldre el problema del desplegament de noves definicions de procés o importació d'expedients
 * on es creaven els nodes de tipus ProcessState però es lligaven a una versió de la definició de procés incorrecta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UpdateSubprocessDefinitionCommand extends AbstractGetObjectBaseCommand {

	private static final long serialVersionUID = -3474000202985837075L;

	private ProcessDefinition processDefinition;
	private ProcessDefinition subprocessDefinition;

	public UpdateSubprocessDefinitionCommand(ProcessDefinition processDefinition, ProcessDefinition subprocessDefinition) {
		super();
		this.processDefinition = processDefinition;
		this.subprocessDefinition = subprocessDefinition;
	}


	public Object execute(JbpmContext jbpmContext) throws Exception {
		SQLQuery updateQuery = jbpmContext.getSession().createSQLQuery(
				"update jbpm_node " +
				"set subprocessdefinition_ = ? " +
				"where id_ in (  " +
				"	select n.id_  " +
				"	from jbpm_node n  " +
				"		inner join jbpm_processdefinition pd on n.subprocessdefinition_ = pd.id_  " +
				"	where  " +
				"		pd.name_ = ? " +
				"		and n.processdefinition_ = ? " +
				")");
		updateQuery.setLong(0, subprocessDefinition.getId());
		updateQuery.setString(1, subprocessDefinition.getName());
		updateQuery.setLong(2, processDefinition.getId());
		int result = updateQuery.executeUpdate();
		return result;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}
	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}
	public ProcessDefinition getSubprocessDefinition() {
		return subprocessDefinition;
	}
	public void setSubprocessDefinition(ProcessDefinition subprocessDefinition) {
		this.subprocessDefinition = subprocessDefinition;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "processDefinition=" + processDefinition.getId() + ", subprocessDefinition=" + subprocessDefinition.getId();
	}
}
