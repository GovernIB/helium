package es.caib.helium.disseny.engine;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/** Classe per per representar la informació general d'una instància de taska dins d'un workflow engine.
 *
 */
@Data
public class WTaskInstance {

	private String id;
	private Set<String> pooledActors = new HashSet<>();
	private Set<String> rols = new HashSet<>();
	private String taskName;
	private String name;
	private String description;
	private Date createTime;
	private Date startTime;
	private Date claimTime;
	private Date endTime;
	private Date dueDate;
	private int priority;
	private String actorId;

	private String processInstanceId;
	private String processDefinitionId;

	private boolean isSuspended;
	private boolean isCancelled;

	public boolean isOpen() {
		//return !this.isSuspended() && !this.isCancelled();
		return !this.isCancelled() && endTime == null;
	}
	public boolean isCompleted() {
		return false; //TODO: revisar
	}

	public boolean isAgafada() {
		return this.getActorId() != null; //TODO: revisar
	}

	// Mètodes per la caché, revisar si són necessaris

	private static final String DESCRIPTION_FIELD_SEPARATOR = "@#@";

	public void setCacheActiu() {
		setFieldFromDescription("cache", "true");
	}
	public void setCacheInactiu() {
		setFieldFromDescription("cache", "false");
	}
	public boolean isCacheActiu() {
		return "true".equalsIgnoreCase(getFieldFromDescription("cache"));
	}

	public String getDescriptionWithFields() {
		return this.getDescription();
	}

	public String getFieldFromDescription(String name) {
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

	public void setFieldFromDescription(String name, String value) {
		String currentFieldValue = getFieldFromDescription(name);
		if (currentFieldValue != null) {
			String currentFieldText = DESCRIPTION_FIELD_SEPARATOR + name + DESCRIPTION_FIELD_SEPARATOR + currentFieldValue;
			String newFieldText = DESCRIPTION_FIELD_SEPARATOR + name + DESCRIPTION_FIELD_SEPARATOR + value;
			this.setDescription(getDescriptionWithFields().replace(currentFieldText, newFieldText));
		} else {
			String newFieldText = DESCRIPTION_FIELD_SEPARATOR + name + DESCRIPTION_FIELD_SEPARATOR + value;
			String fields = getDescriptionWithFields();
			if (fields == null || !fields.startsWith(DESCRIPTION_FIELD_SEPARATOR))
				newFieldText += DESCRIPTION_FIELD_SEPARATOR;
			this.setDescription(newFieldText + getDescriptionWithFields());
		}
	}

	//TODO: mirar com establir la transició seleccionada
	public String getSelectedOutcome() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	private Long TaskInstanceId;
	private String ProcessInstanceId;
	private String ProcessDefinitionId;
	private String RootProcessInstanceId;
	private String TaskName;
	private String Description;
	private String ActorId;
	private Date CreateTime;
	private Date StartTime;
	private Date EndTime;
	private Date DueDate;
	private int Priority;
	private boolean isOpen;
	private boolean isCompleted;
	private boolean isSuspended;
	private boolean isCancelled;
	default private String StringActors() {
		if (this.getPooledActors() == null || this.getPooledActors().isEmpty())
			return null;
		return String.join(",", this.getPooledActors());
	};
	private boolean isAgafada;
	private String SelectedOutcome;
	private String Rols;
	private Set<String> Grups;

	// Cache
	private String Titol;
//	private void setTitol(String titol);
//	private Long EntornId;
//	private void setEntornId(Long entornId);
//	private Boolean TramitacioMassiva;
//	private void setTramitacioMassiva(Boolean tramitacioMassiva);
	private String DefinicioProcesKey;
	private void setDefinicioProcesKey(String definicioProcesKey);
//	private String InfoTasca;
	private void setFieldFromDescription(String string, String string2);
	private void setCacheInactiu;
	private boolean isCacheActiu;
	private Object DescriptionWithFields;
	private void setCacheActiu;
	private String FieldFromDescription(String string);
	private WTaskInstance Task;
	private WProcessInstance ProcessInstance;

//	private Object TaskInstance;

	// TODO: Mirar que fer amb això:
//	default private boolean isCacheActiu() {
//		return false;
//	}
//	default private void setCacheActiu() {};
//	default private void setCacheInactiu() {};

	*/
}
