/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per esborrar variables d'una instància de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DeleteTaskInstanceVariablesCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private String[] variables;
	private boolean locally = false;



	public DeleteTaskInstanceVariablesCommand() {}

	public DeleteTaskInstanceVariablesCommand(long id, String[] variables){
		super();
		this.id = id;
		this.variables = variables;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance taskInstance = jbpmContext.getTaskInstance(id);
		if (taskInstance != null && variables != null) {
			for (int i = 0; i < variables.length; i++)
				if (locally)
					taskInstance.deleteVariableLocally(variables[i]);
				else
					taskInstance.deleteVariable(variables[i]);
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

	public boolean isLocally() {
		return locally;
	}

	public void setLocally(boolean locally) {
		this.locally = locally;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

	//methods for fluent programming
	public DeleteTaskInstanceVariablesCommand id(long id) {
		setId(id);
	    return this;
	}

}
