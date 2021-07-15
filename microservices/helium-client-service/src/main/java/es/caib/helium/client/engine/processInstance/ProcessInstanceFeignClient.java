package es.caib.helium.client.engine.processInstance;

import es.caib.helium.client.engine.model.ProcessStartData;
import es.caib.helium.client.engine.model.WProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ProcessInstanceFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = ProcessInstanceApiPath.FIND_PROCESS_INSTANCE_WITH_PROCESS_DEFINITION_ID)
    ResponseEntity<List<WProcessInstance>> findProcessInstancesWithProcessDefinitionId(@PathVariable("processDefinitionId") String processDefinitionId);

    @RequestMapping(method = RequestMethod.GET, value = ProcessInstanceApiPath.FIND_PROCESS_INSTANCE_WITH_PROCESS_DEFINITION_NAME_AND_ENTORN)
    ResponseEntity<List<WProcessInstance>> findProcessInstancesWithProcessDefinitionNameAndEntorn(
            @PathVariable("processName") String processName,
            @RequestParam(value = "entornId", required = false) String entornId);

    @RequestMapping(method = RequestMethod.GET, value = ProcessInstanceApiPath.GET_PROCESS_INSTANCE_TREE)
    ResponseEntity<List<WProcessInstance>> getProcessInstanceTree(@PathVariable("rootProcessInstanceId") String rootProcessInstanceId);

    @RequestMapping(method = RequestMethod.GET, value = ProcessInstanceApiPath.GET_PROCESS_INSTANCE)
    ResponseEntity<WProcessInstance> getProcessInstance(@PathVariable("processInstanceId") String processInstanceId);

    @RequestMapping(method = RequestMethod.GET, value = ProcessInstanceApiPath.GET_ROOT_PROCESS_INSTANCE)
    ResponseEntity<WProcessInstance> getRootProcessInstance(@PathVariable("processInstanceId") String processInstanceId);

    @RequestMapping(method = RequestMethod.POST, value = ProcessInstanceApiPath.START_PROCESS_INSTANCE_BY_ID)
    ResponseEntity<WProcessInstance> startProcessInstanceById(@RequestBody ProcessStartData processStartData);

    @RequestMapping(method = RequestMethod.POST, value = ProcessInstanceApiPath.SIGNAL_PROCESS_INSTANCE)
    ResponseEntity<Void> signalProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody String signalName);

    @RequestMapping(method = RequestMethod.DELETE, value = ProcessInstanceApiPath.DELETE_PROCESS_INSTANCE)
    ResponseEntity<Void> deleteProcessInstance(@PathVariable("processInstanceId") String processInstanceId);

    @RequestMapping(method = RequestMethod.POST, value = ProcessInstanceApiPath.SUSPEND_PROCESS_INSTANCES)
    ResponseEntity<Void> suspendProcessInstances(@RequestBody String[] processInstanceIds);

    @RequestMapping(method = RequestMethod.POST, value = ProcessInstanceApiPath.RESUME_PROCESS_INSTANCES)
    ResponseEntity<Void> resumeProcessInstances(@RequestBody String[] processInstanceIds);

    @RequestMapping(method = RequestMethod.POST, value = ProcessInstanceApiPath.CHANGE_PROCESS_INSTANCE_VERSION)
    ResponseEntity<Void> changeProcessInstanceVersion(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody Integer newVersion);
}
