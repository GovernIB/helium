package es.caib.helium.client.engine.taskVariable;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.engine.model.UpdateVariablesData;
import es.caib.helium.client.engine.model.VariableRest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskVariableClientImpl implements TaskVariableClient {

	private final String missatgeLog = "Cridant Engine Service - TaskVariable - ";

	private TaskVariableFeignClient taskVariableClient;

	@Override
	public List<VariableRest> getTaskInstanceVariables(String taskId) {
		
		log.debug(missatgeLog + " get task instance variables amb taskId " + taskId);
		var responseEntity = taskVariableClient.getTaskInstanceVariables(taskId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public VariableRest getTaskInstanceVariable(String taskId, String varName) {
		
		log.debug(missatgeLog + " get task instance variables amb taskId " + taskId + " varName " + varName);
		var responseEntity = taskVariableClient.getTaskInstanceVariable(taskId, varName);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void setTaskInstanceVariable(String taskId, String varName, VariableRest variable) {
		
		log.debug(missatgeLog + " get task instance variables amb taskId " + taskId + " varName " + varName + " variable: " + variable.toString());
		taskVariableClient.setTaskInstanceVariable(taskId, varName, variable);
	}

	@Override
	public void setTaskInstanceVariables(String taskId, UpdateVariablesData variables) {

		log.debug(missatgeLog + " get task instance variables amb taskId " + taskId + " variables: " + variables.toString());
		taskVariableClient.setTaskInstanceVariables(taskId, variables);
	}

	@Override
	public void deleteTaskInstanceVariable(String taskId, String varName) {

		log.debug(missatgeLog + " delete task instance variables amb taskId " + taskId + " varName: " + varName.toString());
		taskVariableClient.deleteTaskInstanceVariable(taskId, varName);
	}
}
