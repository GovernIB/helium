package es.caib.helium.camunda.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInstanceDto implements WTaskInstance {

    String id;
//    Long taskInstanceId;
    String processInstanceId;
    String processDefinitionId;
    Long expedientId;
    String taskName;
    String description;
    String actorId;
    Date createTime;
    Date startTime;
    Date endTime;
    Date dueDate;
    int priority;
    boolean open;
    boolean completed;
    boolean suspended;
    boolean cancelled;
    Set<String> pooledActors;
    boolean agafada;
    String selectedOutcome;
    String rols;

    // Cache
    String titol;
    String entornId;
    Boolean tramitacioMassiva;
    String definicioProcesKey;
    String infoTasca;
}
