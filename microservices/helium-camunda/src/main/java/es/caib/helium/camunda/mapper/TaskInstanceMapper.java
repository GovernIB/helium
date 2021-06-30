package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.WProcessInstance;
import es.caib.helium.camunda.model.WTaskInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Task;
import org.mapstruct.Mapper;

@Mapper(uses = {DtoFactory.class})
public interface TaskInstanceMapper {

    public WTaskInstance toWTaskInstance(Task task);
    public WProcessInstance toWTaskInstance(HistoricTaskInstance historicTaskInstance);

}
