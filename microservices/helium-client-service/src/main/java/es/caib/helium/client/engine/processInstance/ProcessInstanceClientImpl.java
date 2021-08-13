package es.caib.helium.client.engine.processInstance;

import es.caib.helium.client.engine.model.ProcessStartData;
import es.caib.helium.client.engine.model.WProcessInstance;
import es.caib.helium.client.model.OptionalString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessInstanceClientImpl implements ProcessInstanceClient{

    private final String MISSATGE_LOG = "Cridant Engine Service - Area carrec - ";

    private final ProcessInstanceFeignClient processInstanceClient;

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {

        log.debug(MISSATGE_LOG + " findProcessInstancesWithProcessDefinitionId " + processDefinitionId);
        var responseEntity = processInstanceClient.findProcessInstancesWithProcessDefinitionId(processDefinitionId);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName, String entornId) {

        log.debug(MISSATGE_LOG + " findProcessInstancesWithProcessDefinitionNameAndEntorn " + processName + " " + entornId);
        var responseEntity = processInstanceClient.findProcessInstancesWithProcessDefinitionNameAndEntorn(processName, entornId);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) {

        log.debug(MISSATGE_LOG + " getProcessInstanceTree " + rootProcessInstanceId);
        var responseEntity = processInstanceClient.getProcessInstanceTree(rootProcessInstanceId);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public WProcessInstance getProcessInstance(String processInstanceId) {

        log.debug(MISSATGE_LOG + " getProcessInstance " + processInstanceId);
        var responseEntity = processInstanceClient.getProcessInstance(processInstanceId);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public WProcessInstance getRootProcessInstance(String processInstanceId) {

        log.debug(MISSATGE_LOG + " getRootProcessInstance " + processInstanceId);
        var responseEntity = processInstanceClient.getRootProcessInstance(processInstanceId);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public WProcessInstance startProcessInstanceById(ProcessStartData processStartData) {

        log.debug(MISSATGE_LOG + " startProcessInstanceById " + processStartData.toString());
        var responseEntity = processInstanceClient.startProcessInstanceById(processStartData);
        var resultat = Objects.requireNonNull(responseEntity.getBody());
        return resultat;
    }

    @Override
    public void signalProcessInstance(String processInstanceId, String signalName) {

        log.debug(MISSATGE_LOG + " signalProcessInstance " + processInstanceId + " " + signalName);
        processInstanceClient.signalProcessInstance(processInstanceId, new OptionalString(signalName));
    }

    @Override
    public void deleteProcessInstance(String processInstanceId) {

        log.debug(MISSATGE_LOG + " deleteProcessInstance " + processInstanceId);
        processInstanceClient.deleteProcessInstance(processInstanceId);
    }

    @Override
    public void suspendProcessInstances(String[] processInstanceIds) {

        log.debug(MISSATGE_LOG + " suspendProcessInstances " + processInstanceIds);
        processInstanceClient.suspendProcessInstances(processInstanceIds);
    }

    @Override
    public void resumeProcessInstances(String[] processInstanceIds) {

        log.debug(MISSATGE_LOG + " resumeProcessInstances " + processInstanceIds);
        processInstanceClient.resumeProcessInstances(processInstanceIds);
    }

    @Override
    public void changeProcessInstanceVersion(String processInstanceId, Integer newVersion) {

        log.debug(MISSATGE_LOG + " changeProcessInstanceVersion " + processInstanceId + " newVersion" + newVersion);
        processInstanceClient.changeProcessInstanceVersion(processInstanceId, newVersion);
    }
}
