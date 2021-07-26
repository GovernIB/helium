package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.TaskInstanceDto;
import es.caib.helium.camunda.model.WTaskInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Task;
import org.mapstruct.Mapper;

@Mapper
public interface TaskInstanceMapper {

    default WTaskInstance toWTaskInstance(Task task) {
        return toTaskInstance(task);
    }
    default WTaskInstance toWTaskInstance(HistoricTaskInstance historicTaskInstance) {
        return toTaskInstance(historicTaskInstance);
    }

    TaskInstanceDto toTaskInstance(Task task);
    TaskInstanceDto toTaskInstance(HistoricTaskInstance historicTaskInstance);

}
