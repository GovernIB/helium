/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.conselldemallorca.helium.api.service.WTaskInstance;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Una instància de tasca multiversió
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JbpmTask implements WTaskInstance {

	private static final String DESCRIPTION_FIELD_SEPARATOR = "@#@";

	private String id;
	private String taskInstanceId;
	private String processInstanceId;
	private String processDefinitionId;
	private String taskName;
	private String description;
	private String actorId;
	private Date createTime;
	private Date startTime;
	private Date endTime;
	private Date dueDate;
	private int priority;
	private boolean open;
	private boolean completed;
	private boolean suspended;
	private boolean cancelled;
	private Set<String> pooledActors;
	private boolean agafada;
	private String selectedOutcome;
	private String rols;
	private String titol;
	private String definicioProcesKey;
//	private String infoTasca;

//	private TaskInstance taskInstance;

	public JbpmTask(TaskInstance taskInstance) {

		this.id = String.valueOf(taskInstance.getId());
		this.taskInstanceId = String.valueOf(taskInstance.getId());
		this.processInstanceId = String.valueOf(taskInstance.getProcessInstance().getId());
		this.processDefinitionId = String.valueOf(taskInstance.getProcessInstance().getProcessDefinition().getId());
		this.taskName = taskInstance.getTask().getName();
		this.description = getDescription(taskInstance);
		this.actorId = taskInstance.getActorId();
		this.pooledActors = getPooledActors(taskInstance);
		this.createTime = taskInstance.getCreate();
		this.startTime = taskInstance.getStart();
		this.endTime = taskInstance.getEnd();
		this.dueDate = taskInstance.getDueDate();
		this.priority = 3 - taskInstance.getPriority();
		this.open = taskInstance.isOpen();
		this.completed = taskInstance.getEnd() != null;
		this.suspended = taskInstance.isSuspended();
		this.cancelled = taskInstance.isCancelled();
		this.agafada = isAgafada(taskInstance);
		this.selectedOutcome = taskInstance.getSelectedOutcome();
		this.rols = taskInstance.getRols();
		this.titol = getTitol();
		this.definicioProcesKey = taskInstance.getProcessInstance().getProcessDefinition().getName();
//		this.infoTasca = taskInstance.;
	}

//	public TaskInstance getTaskInstance() {
//		return taskInstance;
//	}
//	public void setTaskInstance(TaskInstance taskInstance) {
//		this.taskInstance = taskInstance;
//	}
//
//	public String getId() {
//		return new Long(taskInstance.getId()).toString();
//	}
//
//	public Long getTaskInstanceId() {
//		return taskInstance.getId();
//	}
//
//	public String getProcessInstanceId() {
//		return new Long(taskInstance.getProcessInstance().getId()).toString();
//	}
//
//	public String getProcessDefinitionId() {
//		return new Long(taskInstance.getProcessInstance().getProcessDefinition().getId()).toString();
//	}
//
//	public Long getExpedientId() {
//		if (taskInstance.getProcessInstance().getExpedient() == null)
//			return null;
//		return taskInstance.getProcessInstance().getExpedient().getId();
//	}

//	public String getTaskName() {
//		return taskInstance.getTask().getName();
//	}
	public String getDescription(TaskInstance taskInstance) {
		if (taskInstance.getDescription() == null)
			return null;
		if (taskInstance.getDescription().contains(DESCRIPTION_FIELD_SEPARATOR)) {
			int index = taskInstance.getDescription().lastIndexOf(DESCRIPTION_FIELD_SEPARATOR);
			if (index + 1 < taskInstance.getDescription().length())
				return taskInstance.getDescription().substring(index + 1);
			else
				return "";
		} else {
			return taskInstance.getDescription();
		}
	}
//	public String getActorId() {
//		return taskInstance.getActorId();
//	}
	public Set<String> getPooledActors(TaskInstance taskInstance) {
		Set<String> resultat = new HashSet<String>();
		for (PooledActor pa: taskInstance.getPooledActors()) {
			resultat.add(pa.getActorId());
		}
		return resultat;
	}
	public Set<String> getActors() {
		Set<String> resultat = new HashSet<String>();
		if (getActorId() != null) {
			resultat.add(getActorId());
		} else {
			resultat = getPooledActors();
		}
		return resultat;
	}
	public String getStringActors() {
		String actors = "";
		if (getActorId() != null) {
			actors = getActorId();
		} else {
			if (getPooledActors().size() > 0) {
				StringBuilder sb = new StringBuilder();
				for (String pa: getPooledActors()) {
					sb.append(pa);
					sb.append(",");
				}
				actors = "[" + sb.substring(0, sb.length() -1) + "]";
			}
		}
		return actors;
	}
//	public Date getCreateTime() {
//		return taskInstance.getCreate();
//	}
//	public Date getStartTime() {
//		return taskInstance.getStart();
//	}
//	public Date getEndTime() {
//		return taskInstance.getEnd();
//	}
//	public Date getDueDate() {
//		return taskInstance.getDueDate();
//	}
//	public int getPriority() {
//		return 3 - taskInstance.getPriority();
//	}
//	public boolean isOpen() {
//		return taskInstance.isOpen();
//	}
//	public boolean isCompleted() {
//		return taskInstance.getEnd() != null;
//	}
//	public boolean isSuspended() {
//		return taskInstance.isSuspended();
//	}
//	public boolean isCancelled() {
//		return taskInstance.isCancelled();
//	}
	private String getPooledActorsExpression(TaskInstance taskInstance) {
		if (taskInstance.getTask() == null) {
			return null;
		}
		return taskInstance.getTask().getPooledActorsExpression();
	}

	public boolean isAgafada(TaskInstance taskInstance) {
		boolean resultado = false;
		try {
			if (getActorId() != null) {
				// Tenía un grupo asignado
				boolean conGrupoAsignado = getPooledActorsExpression(taskInstance) != null && getPooledActorsExpression(taskInstance).length() > 0;
				// Se le reasignó posteriormente un grupo
				boolean conGrupoAnteriorAsignado = false;
				// Tenía un grupo asignado originalmente a la tarea
				boolean conGrupoOriginal = false;
				if (!conGrupoOriginal) {
					conGrupoAnteriorAsignado = getPooledActors() != null && !getPooledActors().isEmpty();
					if (!conGrupoAnteriorAsignado) {
						@SuppressWarnings("rawtypes")
						Iterator it = taskInstance.getTask().getTaskMgmtDefinition().getSwimlanes().entrySet().iterator();
						while (it.hasNext()) {
							@SuppressWarnings("unchecked")
							Map.Entry<String, Swimlane> e = (Map.Entry<String, Swimlane>)it.next();
							String pooledActorExpresion = taskInstance.getTask().getTaskMgmtDefinition().getSwimlane(e.getKey()).getPooledActorsExpression();
							if (pooledActorExpresion != null && pooledActorExpresion.length() > 0) {
								conGrupoOriginal = true;
								break;
							}
						}
					}
				}
				resultado = (conGrupoAsignado || conGrupoAnteriorAsignado || conGrupoOriginal);
			}
		} catch (Exception e) {}
		return resultado;
	}

//	public String getSelectedOutcome() {
//		return taskInstance.getSelectedOutcome();
//	}



	// CACHE
//	public void setCacheActiu() {
//		setFieldToCache("cache", "true");
//	}
//	public void setCacheInactiu() {
//		setFieldToCache("cache", "false");
//	}
	public boolean isCacheActiu() {
		return "true".equalsIgnoreCase(getFieldFromCache("cache"));
	}

	private String getFieldFromCache(String fieldName) {
		if (this.description == null)
			return null;
		String fieldHeader = DESCRIPTION_FIELD_SEPARATOR + fieldName + DESCRIPTION_FIELD_SEPARATOR;
		int indexInici = this.description.indexOf(fieldHeader);
		if (indexInici != -1) {
			int indexFi = this.description.indexOf(DESCRIPTION_FIELD_SEPARATOR, indexInici + fieldHeader.length());
			return this.description.substring(indexInici + fieldHeader.length(), indexFi);
		} else {
			return null;
		}
	}
//	private void setFieldToCache(String fieldName, String value) {
//		String currentFieldValue = getFieldFromCache(fieldName);
//		if (currentFieldValue != null) {
//			String currentFieldText = DESCRIPTION_FIELD_SEPARATOR + fieldName + DESCRIPTION_FIELD_SEPARATOR + currentFieldValue;
//			String newFieldText = DESCRIPTION_FIELD_SEPARATOR + fieldName + DESCRIPTION_FIELD_SEPARATOR + value;
//			taskInstance.setDescription(getDescriptionWithFields().replace(currentFieldText, newFieldText));
//		} else {
//			String newFieldText = DESCRIPTION_FIELD_SEPARATOR + fieldName + DESCRIPTION_FIELD_SEPARATOR + value;
//			String fields = getDescriptionWithFields();
//			if (fields == null || !fields.startsWith(DESCRIPTION_FIELD_SEPARATOR))
//				newFieldText += DESCRIPTION_FIELD_SEPARATOR;
//			taskInstance.setDescription(newFieldText + getDescriptionWithFields());
//		}
//	}

	@Override
	public String getTitol() {
		if (isCacheActiu())
			return getFieldFromCache("titol");
		else
			return getTaskName();
	}

//	@Override
//	public void setTitol(TaskInstance taskInstance, String titol) {
//		setFieldToCache(
//				taskInstance
//				"titol",
//				titol);
//	}

//	@Override
//	public Long getEntornId() {
//		String sEntornId = getFieldFromCache("entornId");
//		if (sEntornId != null)
//			return new Long(sEntornId);
//		else
//			return null;
//	}
//
//	@Override
//	public void setEntornId(Long entornId) {
//		setFieldToCache(
//				"entornId",
//				entornId.toString());
//	}
//
//	@Override
//	public Boolean getTramitacioMassiva() {
//		String sTramitacioMassiva = getFieldFromCache("tramitacioMassiva");
//		if (sTramitacioMassiva != null)
//			return new Boolean(sTramitacioMassiva);
//		else
//			return null;
//	}
//
//	@Override
//	public void setTramitacioMassiva(Boolean tramitacioMassiva) {
//		setFieldToCache(
//				"tramitacioMassiva",
//				tramitacioMassiva.toString());
//	}
//
//	@Override
//	public String getDefinicioProcesKey() {
//		return getFieldFromCache("definicioProcesJbpmKey");
//	}
//
//	@Override
//	public void setDefinicioProcesKey(String definicioProcesKey) {
//		setFieldToCache(
//				"definicioProcesJbpmKey",
//				definicioProcesKey);
//	}
//
//	@Override
//	public String getInfoTasca() {
//		return getDescriptionWithFields();
//	}
//
//	@Override
//	public String getRols() {
//		return taskInstance.getRols();
//	}

}
