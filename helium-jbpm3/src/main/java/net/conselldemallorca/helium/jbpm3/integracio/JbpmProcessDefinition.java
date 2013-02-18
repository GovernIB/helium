/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import org.jbpm.graph.def.ProcessDefinition;



/**
 * Una definició de procés multiversió
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JbpmProcessDefinition {

	private ProcessDefinition processDefinition;



	public JbpmProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public org.jbpm.graph.def.ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}
	public void setProcessDefinition(
			org.jbpm.graph.def.ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public String getId() {
		return new Long(processDefinition.getId()).toString();
	}
	public String getKey() {
		return processDefinition.getName();
	}
	public String getName() {
		return processDefinition.getName();
	}
	public int getVersion() {
		return processDefinition.getVersion();
	}

}
