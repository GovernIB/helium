/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.integracio;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;

import net.conselldemallorca.helium.core.api.WTaskInstance;


/**
 * Una instància de tasca multiversió
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JbpmTask implements WTaskInstance {

	private static final String DESCRIPTION_FIELD_SEPARATOR = "@#@";

	private TaskInstance taskInstance;

	public JbpmTask(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}

	public TaskInstance getTaskInstance() {
		return taskInstance;
	}
	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}

	public String getId() {
		return new Long(taskInstance.getId()).toString();
	}
	
	public Long getTaskInstanceId() {
		return taskInstance.getId();
	}

	public String getProcessInstanceId() {
		return new Long(taskInstance.getProcessInstance().getId()).toString();
	}

	public String getProcessDefinitionId() {
		return new Long(taskInstance.getProcessInstance().getProcessDefinition().getId()).toString();
	}

	public Long getExpedientId() {
		if (taskInstance.getProcessInstance().getExpedient() == null)
			return null;
		return taskInstance.getProcessInstance().getExpedient().getId();
	}

	public String getTaskName() {
		return taskInstance.getTask().getName();
	}
	public String getDescription() {
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
	public String getActorId() {
		return taskInstance.getActorId();
	}
	public Set<String> getPooledActors() {
		Set<String> resultat = new HashSet<String>();
		for (PooledActor pa: taskInstance.getPooledActors()) {
			resultat.add(pa.getActorId());
		}
		return resultat;
	}
	public Date getCreateTime() {
		return taskInstance.getCreate();
	}
	public Date getStartTime() {
		return taskInstance.getStart();
	}
	public Date getEndTime() {
		return taskInstance.getEnd();
	}
	public Date getDueDate() {
		return taskInstance.getDueDate();
	}
	public int getPriority() {
		return 3 - taskInstance.getPriority();
	}
	public boolean isOpen() {
		return taskInstance.isOpen();
	}
	public boolean isCompleted() {
		return taskInstance.getEnd() != null;
	}
	public boolean isSuspended() {
		return taskInstance.isSuspended();
	}
	public boolean isCancelled() {
		return taskInstance.isCancelled();
	}
	public String getPooledActorsExpression() {
		if (taskInstance.getTask() == null) {
			return null;
		}
		return taskInstance.getTask().getPooledActorsExpression();
	}
	
	public boolean isAgafada() {
		boolean resultado = false;
		try {
			if (getActorId() != null) {
				// Tenía un grupo asignado
				boolean conGrupoAsignado = getPooledActorsExpression() != null && getPooledActorsExpression().length() > 0;
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

	public String getSelectedOutcome() {
		return taskInstance.getSelectedOutcome();
	}
	
	

	// CACHE
	private void setCacheActiu() {
		setFieldFromDescription("cache", "true");
	}
//	private void setCacheInactiu() {
//		setFieldFromDescription("cache", "false");
//	}
	private boolean isCacheActiu() {
		return "true".equalsIgnoreCase(getFieldFromDescription("cache"));
	}

	private String getDescriptionWithFields() {
		return taskInstance.getDescription();
	}
	private String getFieldFromDescription(String name) {
		String text = getDescriptionWithFields();
		if (text == null)
			return null;
		String fieldHeader = DESCRIPTION_FIELD_SEPARATOR + name + DESCRIPTION_FIELD_SEPARATOR;
		int indexInici = text.indexOf(fieldHeader);
		if (indexInici != -1) {
			int indexFi = text.indexOf(DESCRIPTION_FIELD_SEPARATOR, indexInici + fieldHeader.length());
			return text.substring(indexInici + fieldHeader.length(), indexFi);
		} else {
			return null;
		}
	}
	private void setFieldFromDescription(String name, String value) {
		String currentFieldValue = getFieldFromDescription(name);
		if (currentFieldValue != null) {
			String currentFieldText = DESCRIPTION_FIELD_SEPARATOR + name + DESCRIPTION_FIELD_SEPARATOR + currentFieldValue;
			String newFieldText = DESCRIPTION_FIELD_SEPARATOR + name + DESCRIPTION_FIELD_SEPARATOR + value;
			taskInstance.setDescription(getDescriptionWithFields().replace(currentFieldText, newFieldText));
		} else {
			String newFieldText = DESCRIPTION_FIELD_SEPARATOR + name + DESCRIPTION_FIELD_SEPARATOR + value;
			String fields = getDescriptionWithFields();
			if (fields == null || !fields.startsWith(DESCRIPTION_FIELD_SEPARATOR))
				newFieldText += DESCRIPTION_FIELD_SEPARATOR;
			taskInstance.setDescription(newFieldText + getDescriptionWithFields());
		}
	}

	@Override
	public String getTitol() {
		if (isCacheActiu())
			return getFieldFromDescription("titol");
		else
			return getTaskName();
	}
	
	@Override
	public void setTitol(String titol) {
		setFieldFromDescription(
				"titol",
				titol);
	}

	@Override
	public Long getEntornId() {
		String sEntornId = getFieldFromDescription("entornId");
		if (sEntornId != null)
			return new Long(sEntornId);
		else
			return null;
	}

	@Override
	public void setEntornId(Long entornId) {
		setFieldFromDescription(
				"entornId",
				entornId.toString());
	}

	@Override
	public Boolean getTramitacioMassiva() {
		String sTramitacioMassiva = getFieldFromDescription("tramitacioMassiva");
		if (sTramitacioMassiva != null)
			return new Boolean(sTramitacioMassiva);
		else
			return null;
	}

	@Override
	public void setTramitacioMassiva(Boolean tramitacioMassiva) {
		setFieldFromDescription(
				"tramitacioMassiva",
				tramitacioMassiva.toString());	
	}

	@Override
	public String getDefinicioProcesKey() {
		return getFieldFromDescription("definicioProcesJbpmKey");
	}

	@Override
	public void setDefinicioProcesKey(String definicioProcesKey) {
		setFieldFromDescription(
				"definicioProcesJbpmKey",
				definicioProcesKey);		
	}

	@Override
	public String getInfoTasca() {
		return getDescriptionWithFields();
	}
	
	@Override
	public String getRols() {
		return taskInstance.getRols();
	}
	
//	@Override
//	public void setInfoTasca(
//			String titol,
//			String descripcio) {
//		taskInstance.setName(titol);
//		taskInstance.setDescription(descripcio);
//	}

//	@Override
//	public boolean hasInfoTasca() {
//		return isCacheActiu();
//	}
//
//	@Override
//	public void cleanInfoTasca() {
//		setCacheInactiu();
//	}

}
