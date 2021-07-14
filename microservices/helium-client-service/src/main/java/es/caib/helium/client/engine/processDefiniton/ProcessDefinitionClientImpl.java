package es.caib.helium.client.engine.processDefiniton;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.engine.model.WProcessDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessDefinitionClientImpl implements ProcessDefinitionClient {

	private final String missatgeLog = "Cridant Engine Service - ProcessDefinition - ";

	private ProcessDefinitionFeignClient processDefinitionClient;

	@Override
	public WProcessDefinition getProcessDefinition(String deploymentId, String processDefinitionId) {
		
		log.debug(missatgeLog + " get process definition " + processDefinitionId+ " amb deploymentId " + deploymentId);
		var responseEntity = processDefinitionClient.getProcessDefinition(deploymentId, processDefinitionId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<WProcessDefinition> getSubProcessDefinitions(String deploymentId, String processDefinitionId) {
		
		log.debug(missatgeLog + " get sub process definitions  amb processDefinitionId" + processDefinitionId+ " amb deploymentId " + deploymentId);
		var responseEntity = processDefinitionClient.getSubProcessDefinitions(deploymentId, processDefinitionId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<String> getTaskNamesFromDeployedProcessDefinition(String deploymentId, String processDefinitionId) {
		
		log.debug(missatgeLog + " get task names del processDefinitionId" + processDefinitionId+ " amb deploymentId " + deploymentId);
		var responseEntity = processDefinitionClient.getTaskNamesFromDeployedProcessDefinition(deploymentId, processDefinitionId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public String getStartTaskName(String processDefinitionId) {
		
		log.debug(missatgeLog + " get start task name del processDefinitionId" + processDefinitionId);
		var responseEntity = processDefinitionClient.getStartTaskName(processDefinitionId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
		
		log.debug(missatgeLog + " fin process definition amb processInstanceId" + processInstanceId);
		var responseEntity = processDefinitionClient.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void updateSubprocessDefinition(String processDefinitionId1, String processDefinitionId2) {

		log.debug(missatgeLog + " update sub process definition amb processDefinitionId1" + processDefinitionId1 + " processDefinitionId2 " + processDefinitionId2);
		processDefinitionClient.updateSubprocessDefinition(processDefinitionId1, processDefinitionId2);
	}
}
