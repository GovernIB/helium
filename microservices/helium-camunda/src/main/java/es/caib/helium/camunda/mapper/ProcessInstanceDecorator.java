package es.caib.helium.camunda.mapper;

import es.caib.helium.camunda.model.ProcessInstanceDto;
import es.caib.helium.camunda.model.WProcessInstance;
import es.caib.helium.camunda.service.HeliumDataService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ProcessInstanceDecorator implements ProcessInstanceMapper {

    private HeliumDataService heliumDataService;
    private ProcessInstanceMapper mapper;

    @Autowired
    public void setHeliumDataService(HeliumDataService heliumDataService) {
        this.heliumDataService = heliumDataService;
    }

    @Autowired
    public void setMapper(ProcessInstanceMapper mapper) {
        this.mapper = mapper;
    }

//    @Override
//    public WProcessInstance toWProcessInstance(ProcessInstance processInstance) {
//        return ProcessInstanceDto.builder()
//                .id(processInstance.getId())
//                .processDefinitionId(processInstance.getProcessDefinitionId())
//                .processDefinitionName(processInstance.getBusinessKey())
//                .parentProcessInstanceId(processInstance.getRootProcessInstanceId())
////                .expedientId(processInstance.)
//                .build();
//    };

    // TODO: Mirar on es necessita l'expedient!!

    @Override
    public WProcessInstance toWProcessInstance(ProcessInstance processInstance) {
        return mapper.toWProcessInstance(processInstance);
    }

    @Override
    public WProcessInstance toWProcessInstanceWithExpedient(ProcessInstance processInstance) {
        WProcessInstance dto = mapper.toWProcessInstance(processInstance);
        ((ProcessInstanceDto)dto).setExpedientId(heliumDataService.getExpedientIdByProcessInstanceId(processInstance.getId()));
        return dto;
    };

    @Override
    public WProcessInstance toWProcessInstance(HistoricProcessInstance processInstance) {
        return mapper.toWProcessInstance(processInstance);
    }

    @Override
    public WProcessInstance toWProcessInstanceWithExpedient(HistoricProcessInstance processInstance) {
        WProcessInstance dto = mapper.toWProcessInstance(processInstance);
        ((ProcessInstanceDto)dto).setExpedientId(heliumDataService.getExpedientIdByProcessInstanceId(processInstance.getId()));
        return dto;
    };
}
