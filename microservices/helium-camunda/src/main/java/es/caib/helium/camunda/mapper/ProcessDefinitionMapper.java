package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.WProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {DtoFactory.class})
public interface ProcessDefinitionMapper {

    WProcessDefinition toWProcessDefinition(ProcessDefinition processDefinition);
    List<WProcessDefinition> toWProcessDefinitions(List<ProcessDefinition> processDefinitions);

}
