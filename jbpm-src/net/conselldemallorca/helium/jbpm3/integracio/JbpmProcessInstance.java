/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Date;

import org.jbpm.graph.exe.ProcessInstance;


/**
 * Representa una instància de procés jBPM3
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class JbpmProcessInstance {

	private ProcessInstance processInstance;



	public JbpmProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}
	public void setProcessInstance(
			org.jbpm.graph.exe.ProcessInstance processDefinition) {
		this.processInstance = processDefinition;
	}

	public String getId() {
		return new Long(processInstance.getId()).toString();
	}

	public Date getStart() {
		return processInstance.getStart();
	}

	public Date getEnd() {
		return processInstance.getEnd();
	}

	public String getParentProcessInstanceId() {
		if (processInstance.getSuperProcessToken() != null)
			return new Long(processInstance.getSuperProcessToken().getProcessInstance().getId()).toString();
		return null;
	}

	public String getProcessDefinitionId() {
		return new Long(processInstance.getProcessDefinition().getId()).toString();
	}
	public String getDescription() {
		return processInstance.getKey();
	}

}
