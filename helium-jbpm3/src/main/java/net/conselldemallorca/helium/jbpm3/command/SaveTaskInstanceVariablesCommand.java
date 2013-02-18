/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.command;

import java.util.Map;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Command per a guardar variables a dins una instància de tasca
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SaveTaskInstanceVariablesCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private Map<String, Object> variables;
	private boolean locally = false;
	private boolean deleteFirst = false;
	

	public SaveTaskInstanceVariablesCommand(
			long id,
			Map<String, Object> variables) {
		super();
		this.id = id;
		this.variables = variables;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		TaskInstance taskInstance = jbpmContext.getTaskInstance(id);
		if (deleteFirst) {
			for (String key: variables.keySet()) {
				if (locally)
					taskInstance.deleteVariableLocally(key);
				else
					taskInstance.deleteVariable(key);
			}
		}
		for (String key: variables.keySet()) {
			if (locally)
				taskInstance.setVariableLocally(
						key,
						variables.get(key));
			else
				taskInstance.setVariable(
						key,
						variables.get(key));
		}
		return taskInstance;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public boolean isLocally() {
		return locally;
	}
	public void setLocally(boolean locally) {
		this.locally = locally;
	}
	public boolean isDeleteFirst() {
		return deleteFirst;
	}
	public void setDeleteFirst(boolean deleteFirst) {
		this.deleteFirst = deleteFirst;
	}
	public Map<String, Object> getVariables() {
		return variables;
	}
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	@Override
	public String getAdditionalToStringInformation() {
	    return "id=" + id;
	}

}
