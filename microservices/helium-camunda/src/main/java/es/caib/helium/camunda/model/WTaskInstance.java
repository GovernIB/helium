package es.caib.helium.camunda.model;

import java.util.Date;
import java.util.Set;

public interface WTaskInstance {

	public String getId();
//	public Long getTaskInstanceId();
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

	// Cache
	public String getTitol();
	public void setTitol(String titol);
	public String getEntornId();
	public void setEntornId(String entornId);
	public Boolean getTramitacioMassiva();
	public void setTramitacioMassiva(Boolean tramitacioMassiva);
	public String getDefinicioProcesKey();
	public void setDefinicioProcesKey(String definicioProcesKey);
	public String getInfoTasca();
	
//	public Object getTaskInstance();
	
}
