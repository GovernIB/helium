package es.caib.helium.client.engine.taskVariable;

import es.caib.helium.client.engine.model.UpdateVariablesData;
import es.caib.helium.client.engine.model.VariableRest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskVariableClientImpl implements TaskVariableClient {

	private final String missatgeLog = "Cridant Engine Service - TaskVariable - ";

	private final TaskVariableFeignClient taskVariableClient;

	@Override
	public List<VariableRest> getTaskInstanceVariables(String taskId) {
		
		log.debug(missatgeLog + " get task instance variables amb taskId " + taskId);
		try {
			var responseEntity = taskVariableClient.getTaskInstanceVariables(taskId);
			if (HttpStatus.NO_CONTENT.equals(responseEntity.getStatusCode())) {
				return new ArrayList<>();
			} else {
				return responseEntity.getBody();
			}
		} catch (HttpClientErrorException ex) {
			if (HttpStatus.NO_CONTENT.equals(ex.getStatusCode())) {
				return new ArrayList<>();
			}
			throw ex;
		}
	}

	@Override
	public VariableRest getTaskInstanceVariable(String taskId, String varName) {
		
		log.debug(missatgeLog + " get task instance variables amb taskId " + taskId + " varName " + varName);
		try {
			var responseEntity = taskVariableClient.getTaskInstanceVariable(taskId, varName);
			if (HttpStatus.NOT_FOUND.equals(responseEntity.getStatusCode())) {
				return null;
			} else {
				return responseEntity.getBody();
			}
		} catch (HttpClientErrorException ex) {
			if (HttpStatus.NOT_FOUND.equals(ex.getStatusCode())) {
				return null;
			}
			throw ex;
		}
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
