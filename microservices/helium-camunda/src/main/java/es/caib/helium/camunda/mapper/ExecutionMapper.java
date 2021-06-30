package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.WToken;
import org.camunda.bpm.engine.runtime.Execution;
import org.mapstruct.Mapper;

@Mapper(uses = {DtoFactory.class})
public interface ExecutionMapper {

    public WToken toWToken(Execution execution);

}
