package es.caib.helium.client.engine.processDefiniton;

import es.caib.helium.client.engine.model.WProcessDefinition;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ProcessDefinitionClient {

	public WProcessDefinition getProcessDefinition(String processDefinitionId);
	
	public List<WProcessDefinition> getSubProcessDefinitions(String processDefinitionId);
	
	public List<String> getTaskNamesFromDeployedProcessDefinition(String deploymentId, String processDefinitionId);
	
	public String getStartTaskName(String processDefinitionId);
	
	public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId);
	
	public void updateSubprocessDefinition(String processDefinitionId1, String processDefinitionId2);

	public WProcessDefinition parse(MultipartFile zipFile) throws Exception;
}
