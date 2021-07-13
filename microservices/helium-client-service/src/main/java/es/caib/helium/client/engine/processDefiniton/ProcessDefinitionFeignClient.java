package es.caib.helium.client.engine.processDefiniton;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.engine.model.WProcessDefinition;

public interface ProcessDefinitionFeignClient {
	
	@RequestMapping(method = RequestMethod.GET, value = ProcessDefinitionApiPath.GET_PROCESS_DEFINITION)
	public ResponseEntity<WProcessDefinition> getProcessDefinition(
            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId);
	
	@RequestMapping(method = RequestMethod.GET, value = ProcessDefinitionApiPath.GET_SUB_PROCESS_DEFINITIONS)
	public ResponseEntity<List<WProcessDefinition>> getSubProcessDefinitions(
            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId);
	
	@RequestMapping(method = RequestMethod.GET, value = ProcessDefinitionApiPath.GET_TASK_NAMES_FROM_DEPLOYED_PROCESS_DEFINITON)
	public ResponseEntity<List<String>> getTaskNamesFromDeployedProcessDefinition(
            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId);
	
	@RequestMapping(method = RequestMethod.GET, value = ProcessDefinitionApiPath.GET_START_TASK_NAME)
	public ResponseEntity<String> getStartTaskName(
            @PathVariable("processDefinitionId") String processDefinitionId);
	
	@RequestMapping(method = RequestMethod.GET, value = ProcessDefinitionApiPath.FIND_PROCESS_DEFINITION_WITH_PROCESS_INSTANCE_ID)
	public ResponseEntity<WProcessDefinition> findProcessDefinitionWithProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId);
	
	@RequestMapping(method = RequestMethod.GET, value = ProcessDefinitionApiPath.UPDATE_SUB_PROCESS_DEFINITON)
	public ResponseEntity<Void> updateSubprocessDefinition(
            @RequestParam("processDefinitionId1") String processDefinitionId1,
            @RequestParam("processDefinitionId2") String processDefinitionId2); 
}
