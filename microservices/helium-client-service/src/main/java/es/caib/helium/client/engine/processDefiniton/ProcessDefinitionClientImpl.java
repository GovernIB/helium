package es.caib.helium.client.engine.processDefiniton;

import es.caib.helium.client.engine.model.WProcessDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessDefinitionClientImpl implements ProcessDefinitionClient {

	private final String missatgeLog = "Cridant Engine Service - ProcessDefinition - ";

	private final ProcessDefinitionFeignClient processDefinitionClient;

	@Value("${es.caib.helium.engine.url:localhost:8083}")
	private String engineUrl;

	@Override
	public WProcessDefinition getProcessDefinition(String processDefinitionId) {
		
		log.debug(missatgeLog + " get process definition " + processDefinitionId);
		var responseEntity = processDefinitionClient.getProcessDefinition(processDefinitionId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<WProcessDefinition> getSubProcessDefinitions(String processDefinitionId) {
		
		log.debug(missatgeLog + " get sub process definitions  amb processDefinitionId" + processDefinitionId);
		var responseEntity = processDefinitionClient.getSubProcessDefinitions(processDefinitionId);
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
//		var resultat = Objects.requireNonNull(responseEntity.getBody());
//    	return resultat;
		return responseEntity.getBody();
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

	@Override
	public WProcessDefinition parse(MultipartFile zipFile) throws Exception {
		log.debug(missatgeLog + " parsejant definició de procés a partir de zip " + zipFile.getName());
		log.debug("Engine URL: " + engineUrl);
		var responseEntity = processDefinitionClient.parse(zipFile);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}
}
