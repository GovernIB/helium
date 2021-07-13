package es.caib.helium.client.engine.taskVariable;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.client.engine.model.UpdateVariablesData;
import es.caib.helium.client.engine.model.VariableRest;

@Service
public interface TaskVariableService {

	public List<VariableRest> getTaskInstanceVariables(String taskId);
	
	public VariableRest getTaskInstanceVariable(String taskId, String varName);
	
	public void setTaskInstanceVariable(String taskId, String varName, VariableRest variable);
	
	public void setTaskInstanceVariables(String taskId, UpdateVariablesData variables);
	
	public void deleteTaskInstanceVariable(String taskId, String varName);
}
