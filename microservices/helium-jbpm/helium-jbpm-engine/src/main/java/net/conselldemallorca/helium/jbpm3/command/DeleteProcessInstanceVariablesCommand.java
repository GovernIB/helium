/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

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
		if (processInstance != null && variables != null) {
			for (int i = 0; i < variables.length; i++) {
				processInstance.getContextInstance().deleteVariable(variables[i]);
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
