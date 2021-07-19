package es.caib.helium.client.engine.task;

import es.caib.helium.client.engine.model.InfoCacheData;
import es.caib.helium.client.engine.model.ReassignTaskData;
import es.caib.helium.client.engine.model.WTaskInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskClientImpl implements TaskClient {

	private final String missatgeLog = "Cridant Engine Service - Task - ";

	private TaskFeignClient taskClient;

	@Override
	public WTaskInstance getTaskById(String taskId) {
		
		log.debug(missatgeLog + " get task amb id " + taskId);
		var responseEntity = taskClient.getTaskById(taskId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId) {
		
		log.debug(missatgeLog + " find task instances by proces instanceId  " + processInstanceId);
		var responseEntity = taskClient.findTaskInstancesByProcessInstanceId(processInstanceId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public String getTaskInstanceIdByExecutionTokenId(String executionTokenId) {

		log.debug(missatgeLog + " get task instance by execution token id  " + executionTokenId);
		var responseEntity = taskClient.getTaskInstanceIdByExecutionTokenId(executionTokenId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void takeTaskInstance(String taskId, String actorId) {

		log.debug(missatgeLog + " take task instance amb id  " + taskId + " amb actorId " + actorId);
		taskClient.takeTaskInstance(taskId, actorId);
	}

	@Override
	public void releaseTaskInstance(String taskId) {

		log.debug(missatgeLog + " release task instance amb id  " + taskId);
		taskClient.releaseTaskInstance(taskId);
	}

	@Override
	public WTaskInstance startTaskInstance(String taskId) {
		
		log.debug(missatgeLog + " start task instance amb id  " + taskId);
		var responseEntity = taskClient.startTaskInstance(taskId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void endTaskInstance(String taskId, String outcome) {

		log.debug(missatgeLog + " end task instance amb id  " + taskId + " i outcome " + outcome);
		taskClient.endTaskInstance(taskId, outcome);
	}

	@Override
	public WTaskInstance cancelTaskInstance(String taskId) {

		log.debug(missatgeLog + " cancel task instance amb id  " + taskId);
		var responseEntity = taskClient.cancelTaskInstance(taskId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public WTaskInstance suspendTaskInstance(String taskId) {
		
		log.debug(missatgeLog + " suspend task instance amb id  " + taskId);
		var responseEntity = taskClient.suspendTaskInstance(taskId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public WTaskInstance resumeTaskInstance(String taskId) {
		
		log.debug(missatgeLog + " resume task instance amb id  " + taskId);
		var responseEntity = taskClient.resumeTaskInstance(taskId);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public WTaskInstance reassignTaskInstance(String taskId, ReassignTaskData reassignTask) {
		
		log.debug(missatgeLog + " reassing task instance amb id  " + taskId + " task " + reassignTask.toString());
		var responseEntity = taskClient.reassignTaskInstance(taskId, reassignTask);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public void setTaskInstanceActorId(String taskInstanceId, String actorId) {
		log.debug(missatgeLog + " reassing task instance amb id  " + taskInstanceId + " to " + actorId);
		taskClient.setTaskInstanceActorId(taskInstanceId, actorId);
	}

	@Override
	public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) {
		log.debug(missatgeLog + " reassing task instance amb id  " + taskInstanceId + " to users: " + String.join(",", pooledActors));
		taskClient.setTaskInstancePooledActors(taskInstanceId, pooledActors);
	}

	@Override
	public void updateTaskInstanceInfoCache(String taskId, InfoCacheData info) {

		log.debug(missatgeLog + " update task instance info cache amb id  " + taskId + " task " + info.toString());
		taskClient.updateTaskInstanceInfoCache(taskId, info);
	}

	@Override
	public List<String> findStartTaskOutcomes(String definicioProces, String taskName) {
		log.debug(missatgeLog + " get startTask output transitions amb definicioProces=" + definicioProces + ", taskName=" + taskName);
		var responseEntity = taskClient.findStartTaskOutcomes(definicioProces, taskName);
		return responseEntity.getBody();
	}

	@Override
	public List<String> findTaskInstanceOutcomes(String taskInstanceId) {
		log.debug(missatgeLog + " get task instance output transitions amb id=" + taskInstanceId);
		var responseEntity = taskClient.findTaskInstanceOutcomes(taskInstanceId);
		return responseEntity.getBody();
	}

}
