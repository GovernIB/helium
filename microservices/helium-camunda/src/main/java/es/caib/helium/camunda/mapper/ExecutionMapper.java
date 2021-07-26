package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.ExecutionDto;
import es.caib.helium.camunda.model.WToken;
import org.camunda.bpm.engine.runtime.Execution;
import org.mapstruct.Mapper;

@Mapper
public interface ExecutionMapper {

    default WToken toWToken(Execution execution) {
        return toExecution(execution);
    }

    ExecutionDto toExecution(Execution execution);

}
