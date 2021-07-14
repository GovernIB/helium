package es.caib.helium.client.engine.task;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.client.engine.model.InfoCacheData;
import es.caib.helium.client.engine.model.ReassignTaskData;
import es.caib.helium.client.engine.model.WTaskInstance;

@Service
public interface TaskClient {

	public WTaskInstance getTaskById(String taskId);
	
	public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId);
	
	public String getTaskInstanceIdByExecutionTokenId(String executionTokenId);
	
	public void takeTaskInstance(String taskId, String actorId);
	
	public void releaseTaskInstance(String taskId);
	
	public WTaskInstance startTaskInstance(String taskId);
	
	public void endTaskInstance(String taskId, String outcome);
	
	 public WTaskInstance cancelTaskInstance(String taskId);
	
	public WTaskInstance suspendTaskInstance(String taskId);
	
	public WTaskInstance resumeTaskInstance(String taskId);
	
	public WTaskInstance reassignTaskInstance(String taskId, ReassignTaskData reassignTask);
	
	public void updateTaskInstanceInfoCache(String taskId, InfoCacheData info);
}
