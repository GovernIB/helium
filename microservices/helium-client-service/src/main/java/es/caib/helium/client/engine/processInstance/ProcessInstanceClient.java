package es.caib.helium.client.engine.processInstance;

import es.caib.helium.client.engine.model.ProcessStartData;
import es.caib.helium.client.engine.model.WProcessInstance;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProcessInstanceClient {

    List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId);

    List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName, String entornId);

    List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId);

    WProcessInstance getProcessInstance(String processInstanceId);

    WProcessInstance getRootProcessInstance(String processInstanceId);

    WProcessInstance startProcessInstanceById(ProcessStartData processStartData);

    void signalProcessInstance(String processInstanceId, String signalName);

    void deleteProcessInstance(String processInstanceId);

    void suspendProcessInstances(String[] processInstanceIds);

    void resumeProcessInstances(String[] processInstanceIds);

    void changeProcessInstanceVersion(String processInstanceId, Integer newVersion);
}
