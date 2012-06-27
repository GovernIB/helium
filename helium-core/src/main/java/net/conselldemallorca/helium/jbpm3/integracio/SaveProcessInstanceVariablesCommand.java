/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Map;

import org.jbpm.JbpmContext;
import org.jbpm.command.AbstractBaseCommand;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Command per a guardar variables a dins una instància de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SaveProcessInstanceVariablesCommand extends AbstractBaseCommand {

	private static final long serialVersionUID = -1908847549444051495L;
	private long id;
	private Map<String, Object> variables;
	

	public SaveProcessInstanceVariablesCommand(
			long id,
			Map<String, Object> variables) {
		super();
		this.id = id;
		this.variables = variables;
	}

	public Object execute(JbpmContext jbpmContext) throws Exception {
		ProcessInstance processInstance = jbpmContext.getProcessInstance(id);
		for (String key: variables.keySet()) {
			processInstance.getContextInstance().setVariable(
					key,
					variables.get(key));
		}
		return processInstance;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
