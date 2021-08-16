/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.api.service.WProcessInstance;
import org.jbpm.graph.exe.ProcessInstance;

import java.util.Date;


/**
 * Representa una instància de procés jBPM3
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class JbpmProcessInstance implements WProcessInstance {

	private String id;
	private String processDefinitionId;
	private String processDefinitionName;
	private String parentProcessInstanceId;
	private Date startTime;
	private Date endTime;
	private String description;

//	private ProcessInstance processInstance;

	public JbpmProcessInstance(ProcessInstance processInstance) {
//		this.processInstance = processInstance;
		this.id = String.valueOf(processInstance.getId());
		this.processDefinitionId = String.valueOf(processInstance.getProcessDefinition().getId());
		this.processDefinitionName = processInstance.getProcessDefinition().getName();
		this.parentProcessInstanceId = (processInstance.getSuperProcessToken() != null ?
				String.valueOf(processInstance.getSuperProcessToken().getProcessInstance().getId()) :
				null);
		this.startTime = processInstance.getStart();
		this.endTime = processInstance.getEnd();
		this.description = processInstance.getKey();
	}

//	public ProcessInstance getProcessInstance() {
//		return processInstance;
//	}
//	public void setProcessInstance(
//			org.jbpm.graph.exe.ProcessInstance processDefinition) {
//		this.processInstance = processDefinition;
//	}
//
//	@Override
//	public String getId() {
//		return new Long(processInstance.getId()).toString();
//	}
//
//	@Override
//	public Date getStartTime() {
//		return processInstance.getStart();
//	}
//
//	@Override
//	public Date getEndTime() {
//		if (processInstance == null)
//			return null;
//		return processInstance.getEnd();
//	}
//
//	@Override
//	public String getParentProcessInstanceId() {
//		if (processInstance.getSuperProcessToken() != null)
//			return new Long(processInstance.getSuperProcessToken().getProcessInstance().getId()).toString();
//		return null;
//	}
//
//	@Override
//	public String getProcessDefinitionId() {
//		return new Long(processInstance.getProcessDefinition().getId()).toString();
//	}
//
//	@Override
//	public String getDescription() {
//		return processInstance.getKey();
//	}
//
//	@Override
//	public String getProcessDefinitionName() {
//		if (processInstance == null)
//			return null;
//		return processInstance.getProcessDefinition().getName();
//	}
//
//	@Override
//	public Long getExpedientId() {
//		if (processInstance == null)
//			return null;
//		return processInstance.getExpedient().getId();
//	}

}
