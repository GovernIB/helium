package es.caib.helium.client.engine.processDefiniton;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.client.engine.model.WProcessDefinition;

@Service
public interface ProcessDefinitionClient {

	public WProcessDefinition getProcessDefinition(String deploymentId, String processDefinitionId);
	
	public List<WProcessDefinition> getSubProcessDefinitions(String deploymentId, String processDefinitionId);
	
	public List<String> getTaskNamesFromDeployedProcessDefinition(String deploymentId, String processDefinitionId);
	
	public String getStartTaskName(String processDefinitionId);
	
	public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId);
	
	public void updateSubprocessDefinition(String processDefinitionId1, String processDefinitionId2); 
}
