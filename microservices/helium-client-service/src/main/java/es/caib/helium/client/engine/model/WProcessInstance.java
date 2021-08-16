package es.caib.helium.client.engine.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;

@JsonDeserialize(as = ProcessInstanceDto.class)
public interface WProcessInstance {

	public String getId();
	public String getProcessDefinitionId();
	public String getProcessDefinitionName();
	public String getParentProcessInstanceId();
	public Date getStartTime();
	public Date getEndTime();
	public String getDescription();
	public Long getExpedientId();
	
//	public Object getProcessInstance();
	
	/*
	 * getProcessInstance().getTaskMgmtInstance().getUnfinishedTasks(currentToken) ==> Retroces!!!
	 */
	
}
