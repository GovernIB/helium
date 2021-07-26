package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.ProcessInstanceDto;
import es.caib.helium.camunda.model.WProcessInstance;
import es.caib.helium.camunda.service.HeliumDataService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel="spring")
public abstract class ProcessInstanceMapper {

    @Autowired
    private HeliumDataService heliumDataService;

    public WProcessInstance toWProcessInstance(ProcessInstance processInstance) {
        return toProcessInstance(processInstance);
    }
    public WProcessInstance toWProcessInstance(HistoricProcessInstance historicProcessInstance) {
        return toProcessInstance(historicProcessInstance);
    }

    public WProcessInstance toWProcessInstanceWithExpedient(ProcessInstance processInstance) {
        return toProcessInstanceWithExpedient(processInstance);
    }
    public WProcessInstance toWProcessInstanceWithExpedient(HistoricProcessInstance historicProcessInstance) {
        return toProcessInstanceWithExpedient(historicProcessInstance);
    }

    @Mapping(source = "rootProcessInstanceId", target = "parentProcessInstanceId")
    abstract ProcessInstanceDto toProcessInstance(ProcessInstance processInstance);
    @Mapping(source = "superProcessInstanceId", target = "parentProcessInstanceId")
    abstract ProcessInstanceDto toProcessInstance(HistoricProcessInstance processInstance);

    @BeanMapping( qualifiedByName = "withExpedient")
    @Mapping(source = "rootProcessInstanceId", target = "parentProcessInstanceId")
    abstract ProcessInstanceDto toProcessInstanceWithExpedient(ProcessInstance processInstance);
    @BeanMapping( qualifiedByName = "withExpedientHistoric")
    @Mapping(source = "superProcessInstanceId", target = "parentProcessInstanceId")
    abstract ProcessInstanceDto toProcessInstanceWithExpedient(HistoricProcessInstance historicProcessInstance);

    @AfterMapping
    @Named("withExpedient")
    void addExpedient(ProcessInstance processInstance, @MappingTarget ProcessInstanceDto processInstanceDto) {
        processInstanceDto.setExpedientId(heliumDataService.getExpedientIdByProcessInstanceId(processInstance.getId()));
    }
    @AfterMapping
    @Named("withExpedientHistoric")
    void addExpedient(HistoricProcessInstance historicProcessInstance, @MappingTarget ProcessInstanceDto processInstanceDto) {
        processInstanceDto.setExpedientId(heliumDataService.getExpedientIdByProcessInstanceId(historicProcessInstance.getId()));
    }
}
