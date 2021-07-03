package es.caib.helium.logic.intf;

import java.util.Date;

public interface WProcessInstance {

	public String getId();
	public String getProcessDefinitionId();
	public String getProcessDefinitionName();
	public String getParentProcessInstanceId();
	public Date getStartTime();
	public Date getEndTime();
	public String getDescription();
	public Long getExpedientId();
	
	public Object getProcessInstance();
	
	/*
	 * getProcessInstance().getTaskMgmtInstance().getUnfinishedTasks(currentToken) ==> Retroces!!!
	 */
	
}