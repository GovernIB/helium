package es.caib.helium.client.engine.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Date;
import java.util.Set;

@JsonDeserialize(as = TaskInstanceDto.class)
public interface WTaskInstance {

	public String getId();
	public Long getTaskInstanceId();
	public String getProcessInstanceId();
	public String getProcessDefinitionId();
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
	default public String getStringActors() {
		if (this.getPooledActors() == null || this.getPooledActors().isEmpty())
			return null;
		return String.join(",", this.getPooledActors());
	};
	public boolean isAgafada();
	public String getSelectedOutcome();
	public String getRols();
	public Set<String> getGrups();

	// Cache
	public String getTitol();
//	public void setTitol(String titol);
//	public Long getEntornId();
//	public void setEntornId(Long entornId);
//	public Boolean getTramitacioMassiva();
//	public void setTramitacioMassiva(Boolean tramitacioMassiva);
	public String getDefinicioProcesKey();
	public void setDefinicioProcesKey(String definicioProcesKey);
//	public String getInfoTasca();
	
//	public Object getTaskInstance();

	// TODO: Mirar que fer amb això:
//	default public boolean isCacheActiu() {
//		return false;
//	}
//	default public void setCacheActiu() {};
//	default public void setCacheInactiu() {};
	
}
