package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.WProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DtoFactory.class})
@DecoratedWith(ProcessInstanceDecorator.class)
public interface ProcessInstanceMapper {

    public WProcessInstance toWProcessInstance(ProcessInstance processInstance);
    public WProcessInstance toWProcessInstance(HistoricProcessInstance processInstance);

    public WProcessInstance toWProcessInstanceWithExpedient(ProcessInstance processInstance);
    public WProcessInstance toWProcessInstanceWithExpedient(HistoricProcessInstance processInstance);

}
