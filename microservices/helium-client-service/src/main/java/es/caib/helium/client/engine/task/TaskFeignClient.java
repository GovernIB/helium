package es.caib.helium.client.engine.task;

import es.caib.helium.client.engine.model.InfoCacheData;
import es.caib.helium.client.engine.model.ReassignTaskData;
import es.caib.helium.client.engine.model.WTaskInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
	public ResponseEntity<WTaskInstance> takeTaskInstance(
            @PathVariable("taskId") String taskId,
            @PathVariable("actorId") String actorId);
	
	@RequestMapping(method = RequestMethod.POST, value = TaskApiPath.RELEASE_TASK_INSTANCE)
	public ResponseEntity<WTaskInstance> releaseTaskInstance(
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

	@RequestMapping(method = RequestMethod.POST, value=TaskApiPath.REASSING_TASK_INSTANCE_USER)
	public ResponseEntity<Void> setTaskInstanceActorId(
			@PathVariable("taskInstanceId") String taskInstanceId,
			@RequestBody String actorId);

	@RequestMapping(method = RequestMethod.POST, value=TaskApiPath.REASSING_TASK_INSTANCE_GROUP)
	public ResponseEntity<Void> setTaskInstancePooledActors(
			@PathVariable("taskInstanceId") String taskInstanceId,
			@RequestBody String[] pooledActors);
	
	@RequestMapping(method = RequestMethod.PUT, value = TaskApiPath.UPDATE_TASK_INSTANCE_INFO_CACHE)
	public ResponseEntity<Void> updateTaskInstanceInfoCache(
            @PathVariable("taskId") String taskId,
            @RequestBody InfoCacheData info);

	@RequestMapping(method = RequestMethod.GET, value=TaskApiPath.FIND_STARTTASK_OUT_TRANSITION)
	public ResponseEntity<List<String>> findStartTaskOutcomes(
			@PathVariable("processDefinitionId") String processDefinitionId,
			@PathVariable("taskName") String taskName);

	@RequestMapping(method = RequestMethod.GET, value=TaskApiPath.FIND_TASK_OUT_TRANSITION)
	public ResponseEntity<List<String>> findTaskInstanceOutcomes(
			@PathVariable("taskInstanceId") String taskInstanceId);
	
}
