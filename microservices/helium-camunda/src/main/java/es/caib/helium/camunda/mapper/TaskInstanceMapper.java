package es.caib.helium.camunda.mapper;

import es.caib.helium.client.engine.model.TaskInstanceDto;
import es.caib.helium.client.engine.model.WTaskInstance;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.camunda.bpm.engine.task.Task;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

import static es.caib.helium.camunda.service.TaskInstanceServiceImpl.CANCEL_REASON;

@Mapper(componentModel="spring")
public abstract class TaskInstanceMapper {

    @Autowired
    private TaskService taskService;

    public WTaskInstance toWTaskInstance(Task task) {
        return toTaskInstance(task);
    }
    public WTaskInstance toWTaskInstance(HistoricTaskInstance historicTaskInstance) {
        return toTaskInstance(historicTaskInstance);
    }

    public WTaskInstance toWTaskInstanceWithDetails(Task task) {
        return toTaskInstanceWithDetails(task);
    }
    public WTaskInstance toWTaskInstanceWithDetails(HistoricTaskInstance historicTaskInstance) {
        return toTaskInstanceWithDetails(historicTaskInstance);
    }

    @Mapping(source = "name", target = "taskName")
    @Mapping(source = "assignee", target = "actorId")
    @Mapping(source = "description", target = "titol")
//    @Mapping(source = "tenantId", target = "entornId")
    abstract TaskInstanceDto toTaskInstance(Task task);

    @Mapping(source = "name", target = "taskName")
    @Mapping(source = "assignee", target = "actorId")
    @Mapping(source = "description", target = "titol")
//    @Mapping(source = "tenantId", target = "entornId")
    @Mapping(source = "processDefinitionKey", target = "definicioProcesKey")
    abstract TaskInstanceDto toTaskInstance(HistoricTaskInstance historicTaskInstance);

    @BeanMapping( qualifiedByName = "withDetails")
    @Mapping(source = "name", target = "taskName")
    @Mapping(source = "assignee", target = "actorId")
    @Mapping(source = "description", target = "titol")
//    @Mapping(source = "tenantId", target = "entornId")
    abstract TaskInstanceDto toTaskInstanceWithDetails(Task task);

    @BeanMapping( qualifiedByName = "withDetails")
    @Mapping(source = "name", target = "taskName")
    @Mapping(source = "assignee", target = "actorId")
    @Mapping(source = "description", target = "titol")
//    @Mapping(source = "tenantId", target = "entornId")
    @Mapping(source = "processDefinitionKey", target = "definicioProcesKey")
    abstract TaskInstanceDto toTaskInstanceWithDetails(HistoricTaskInstance historicTaskInstance);

    @AfterMapping
    @Named("withDetails")
    void addDetails(Task taskInstance, @MappingTarget TaskInstanceDto taskInstanceDto) {
        setCandidates(taskInstanceDto, taskInstance.getId());
        taskInstanceDto.setOpen(true);
        taskInstanceDto.setCompleted(false);
        taskInstanceDto.setCancelled(false);
        taskInstanceDto.setAgafada(taskInstance.getAssignee() != null && !taskInstance.getAssignee().isBlank());
    }

    @AfterMapping
    @Named("withDetails")
    void addDetails(HistoricTaskInstance taskInstance, @MappingTarget TaskInstanceDto taskInstanceDto) {
        setCandidates(taskInstanceDto, taskInstance.getId());
        taskInstanceDto.setOpen(taskInstance.getEndTime() == null);
        taskInstanceDto.setCompleted(taskInstance.getEndTime() != null);
        var task = taskService.createTaskQuery()
                .taskId(taskInstance.getId())
                .singleResult();
        taskInstanceDto.setSuspended(task != null && task.isSuspended());
        taskInstanceDto.setCancelled(CANCEL_REASON.equals(taskInstance.getDeleteReason()));
        taskInstanceDto.setAgafada(taskInstance.getAssignee() != null && !taskInstance.getAssignee().isBlank());
    }

    private void setCandidates(TaskInstanceDto taskInstanceDto, String taskId) {
        var candidates = taskService.getIdentityLinksForTask(taskId)
                .stream()
                .filter(i -> i.getType().equals(IdentityLinkType.CANDIDATE))
                .map(i -> i.getUserId())
                .collect(Collectors.toSet());
        if (candidates != null && !candidates.isEmpty()) {
            taskInstanceDto.setPooledActors(candidates);
        }
    }

}
