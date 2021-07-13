package es.caib.helium.client.engine.taskVariable;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.helium.client.engine.model.UpdateVariablesData;
import es.caib.helium.client.engine.model.VariableRest;

public interface TaskVariableFeignClient {
	
	@RequestMapping(method = RequestMethod.GET, value = TaskVariableApiPath.GET_TASK_INSTANCE_VARIABLES)
	public ResponseEntity<List<VariableRest>> getTaskInstanceVariables(
            @PathVariable("taskId") String taskId);
	
	@RequestMapping(method = RequestMethod.GET, value = TaskVariableApiPath.GET_TASK_INSTANCE_VARIABLE)
	public ResponseEntity<VariableRest> getTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName);
	
	@RequestMapping(method = RequestMethod.POST, value = TaskVariableApiPath.SET_TASK_INSTANCE_VARIABLE)
	public ResponseEntity<Void> setTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName,
            @RequestBody VariableRest variable);
	
	
	@RequestMapping(method = RequestMethod.POST, value = TaskVariableApiPath.SET_TASK_INSTANCE_VARIABLES)
	public ResponseEntity<Void> setTaskInstanceVariables(
            @PathVariable("taskId") String taskId,
            @RequestBody UpdateVariablesData variables);
	
	@RequestMapping(method = RequestMethod.DELETE, value = TaskVariableApiPath.DELETE_TASK_INSTANCE_VARIABLE)
	public ResponseEntity<Void> deleteTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName);

}
