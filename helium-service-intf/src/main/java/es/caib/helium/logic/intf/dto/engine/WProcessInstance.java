package es.caib.helium.logic.intf.dto.engine;


import java.util.Date;

import org.slf4j.Logger;

public interface WProcessInstance {

	public Long getId();
	public String getProcessDefinitionId();
	public String getProcessDefinitionName();
	public String getParentProcessInstanceId();
	public Date getStartTime();
	public Date getEndTime();
	public String getDescription();
	public Long getExpedientId();
	public WProcessInstance getProcessInstance();
	public WProcessDefinition getProcessDefinition();
	public String getKey();
	public WToken getSuperProcessToken();	
}
