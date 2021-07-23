package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.ProcessDefinitionDto;
import es.caib.helium.camunda.model.WProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface ProcessDefinitionMapper {

    default WProcessDefinition toWProcessDefinition(ProcessDefinition processDefinition) {
        return toProcessDefinition(processDefinition);
    }
    default List<? extends WProcessDefinition> toWProcessDefinitions(List<ProcessDefinition> processDefinitions) {
        return toProcessDefinitions(processDefinitions);
    }

    ProcessDefinitionDto toProcessDefinition(ProcessDefinition processDefinition);
    @Named("toProcessDefinition")
    List<ProcessDefinitionDto> toProcessDefinitions(List<ProcessDefinition> processDefinitions);
}
