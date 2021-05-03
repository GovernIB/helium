package net.conselldemallorca.helium.core.api;

import java.util.Date;
import java.util.Set;

public interface WTaskInstance {

	public String getId();
	public Long getTaskInstanceId();
	public String getProcessInstanceId();
	public String getProcessDefinitionId();
	public Long getExpedientId();
	public String getTaskName();
	public String getDescription();
	public String getActorId();
	public Date getCreateTime();
	public Date getStartTime();
	public Date getEndTime();
	public Date getDueDate();
	public int getPriority();
	public boolean isOpen();
	public boolean isCompleted();
	public boolean isSuspended();
	public boolean isCancelled();
	public Set<String> getPooledActors();
	public boolean isAgafada();
	public String getSelectedOutcome();
	public String getRols();
	public Set<String> getActors();
	public String getStringActors();

	public boolean isCacheActiu();
	public void setCacheActiu();
	public void setCacheInactiu();
//	public String getFieldFromCache(String field);
	public String getTitol();
	public void setTitol(String titol);
	public Long getEntornId();
	public void setEntornId(Long entornId);
	public Boolean getTramitacioMassiva();
	public void setTramitacioMassiva(Boolean tramitacioMassiva);
	public String getDefinicioProcesKey();
	public void setDefinicioProcesKey(String definicioProcesKey);
	public String getInfoTasca();
	
	public Object getTaskInstance();
	
}
