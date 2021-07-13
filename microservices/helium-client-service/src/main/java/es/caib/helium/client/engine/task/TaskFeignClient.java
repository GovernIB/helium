package es.caib.helium.client.engine.task;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.engine.model.InfoCacheData;
import es.caib.helium.client.engine.model.ReassignTaskData;
import es.caib.helium.client.engine.model.WTaskInstance;

public interface TaskFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = TaskApiPath.GET_TASK_BY_ID)
	public ResponseEntity<WTaskInstance> getTaskById(
            @PathVariable("taskId") String taskId);
	
	@RequestMapping(method = RequestMethod.GET, value = TaskApiPath.FIND_TASK_INSTANCES_BY_PROCESS_INSTANCE_ID)
	public ResponseEntity<List<WTaskInstance>> findTaskInstancesByProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId);
	
	@RequestMapping(method = RequestMethod.GET, value = TaskApiPath.GET_TASK_INSTANCE_ID_BY_EXECUTION_TOKEN_ID)
	public ResponseEntity<String> getTaskInstanceIdByExecutionTokenId(
            @PathVariable("executionTokenId") String executionTokenId);
	
	@RequestMapping(method = RequestMethod.POST, value = TaskApiPath.TAKE_TASK_INSTANCE)
	public ResponseEntity<Void> takeTaskInstance(
            @PathVariable("taskId") String taskId,
            @PathVariable("actorId") String actorId);
	
	@RequestMapping(method = RequestMethod.POST, value = TaskApiPath.RELEASE_TASK_INSTANCE)
	public ResponseEntity<Void> releaseTaskInstance(
            @PathVariable("taskId") String taskId);
	
	@RequestMapping(method = RequestMethod.POST, value = TaskApiPath.START_TASK_INSTANCE)
	public ResponseEntity<WTaskInstance> startTaskInstance(
            @PathVariable("taskId") String taskId);
	
	@RequestMapping(method = RequestMethod.POST, value = TaskApiPath.END_TASK_INSTANCE)
	public ResponseEntity<Void> endTaskInstance(
            @PathVariable("taskId") String taskId,
            @RequestParam(value = "outcome", required = false) String outcome);
	
	@RequestMapping(method = RequestMethod.POST, value = TaskApiPath.CANCEL_TASK_INSTANCE)
	 public ResponseEntity<WTaskInstance> cancelTaskInstance(
	            @PathVariable("taskId") String taskId);
	
	@RequestMapping(method = RequestMethod.POST, value = TaskApiPath.SUSPEND_TASK_INSTANCE)
	public ResponseEntity<WTaskInstance> suspendTaskInstance(
            @PathVariable("taskId") String taskId);
	
	@RequestMapping(method = RequestMethod.POST, value = TaskApiPath.RESUME_TASK_INSTANCE)
	public ResponseEntity<WTaskInstance> resumeTaskInstance(
            @PathVariable("taskId") String taskId);
	
	@RequestMapping(method = RequestMethod.POST, value = TaskApiPath.REASSING_TASK_INSTANCE)
	public ResponseEntity<WTaskInstance> reassignTaskInstance(
            @PathVariable("taskId") String taskId,
            @RequestBody ReassignTaskData reassignTask);
	
	@RequestMapping(method = RequestMethod.PUT, value = TaskApiPath.UPDATE_TASK_INSTANCE_INFO_CACHE)
	public ResponseEntity<Void> updateTaskInstanceInfoCache(
            @PathVariable("taskId") String taskId,
            @RequestBody InfoCacheData info);
	
}
