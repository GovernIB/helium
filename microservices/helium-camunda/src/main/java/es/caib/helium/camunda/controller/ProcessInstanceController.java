package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.WProcessInstance;
import es.caib.helium.camunda.service.ProcessInstanceService;
import es.caib.helium.client.engine.model.ProcessStartData;
import es.caib.helium.client.model.OptionalString;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.caib.helium.client.engine.helper.VariableHelper.variableRestToObjectMapConvert;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ProcessInstanceController.API_PATH)
public class ProcessInstanceController {

    public static final String API_PATH = "/api/v1/processInstances";

    private final ProcessInstanceService processInstanceService;


    @GetMapping(value="/byProcessDefinition/{processDefinitionId}")
    public ResponseEntity<List<WProcessInstance>> findProcessInstancesWithProcessDefinitionId(
            @PathVariable("processDefinitionId") String processDefinitionId) {

        List<WProcessInstance> processInstances = processInstanceService.findProcessInstancesWithProcessDefinitionId(processDefinitionId);
        if (processInstances == null || processInstances.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(processInstances, HttpStatus.OK);
    }

    @GetMapping(value="/byProcessDefinitionName/{processName}")
    public ResponseEntity<List<WProcessInstance>> findProcessInstancesWithProcessDefinitionNameAndEntorn(
            @PathVariable("processName") String processName,
            @RequestParam(value = "entornId", required = false) String entornId) {
        List<WProcessInstance> processInstances;
        if (entornId != null) {
            processInstances = processInstanceService.findProcessInstancesWithProcessDefinitionNameAndEntorn(processName, entornId);
        } else {
            processInstances = processInstanceService.findProcessInstancesWithProcessDefinitionName(processName);
        }
        if (processInstances == null || processInstances.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(processInstances, HttpStatus.OK);
    }

    @GetMapping(value="/{rootProcessInstanceId}/tree")
    public ResponseEntity<List<WProcessInstance>> getProcessInstanceTree(
            @PathVariable("rootProcessInstanceId") String rootProcessInstanceId) {

        List<WProcessInstance> processInstances = processInstanceService.getProcessInstanceTree(rootProcessInstanceId);
        if (processInstances == null || processInstances.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(processInstances, HttpStatus.OK);
    }

    @GetMapping(value="/{processInstanceId}")
    public ResponseEntity<WProcessInstance> getProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity<>(
                processInstanceService.getProcessInstance(processInstanceId),
                HttpStatus.OK);
    }

    @GetMapping(value="/{processInstanceId}/root")
    public ResponseEntity<WProcessInstance> getRootProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity<>(
                processInstanceService.getRootProcessInstance(processInstanceId),
                HttpStatus.OK);
    }

    // TODO: Tasca utilitzada per a consulta d'expedients
    //       Això ha d'anar al MS d'expedients i tasques o al MS de dades!!
//    @GetMapping(value="/root")
//    @ResponseBody
//    public ResponseEntity<List<String>> findRootProcessInstances(
//            @RequestParam(value = "actorId") String actorId,
//            @RequestParam(value = "processInstanceIds") List<String> processInstanceIds,
//            @RequestParam(value = "nomesMeves") boolean nomesMeves,
//            @RequestParam(value = "nomesTasquesPersonals") boolean nomesTasquesPersonals,
//            @RequestParam(value = "nomesTasquesGrup") boolean nomesTasquesGrup) {
//
//        List<String> rootProcessInstances = processInstanceService.findRootProcessInstances(
//                actorId,
//                processInstanceIds,
//                nomesMeves,
//                nomesTasquesPersonals,
//                nomesTasquesGrup);
//        if (rootProcessInstances == null || rootProcessInstances.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//
//        return new ResponseEntity<>(rootProcessInstances, HttpStatus.OK);
//    }

    @PostMapping(value="/start")
    public ResponseEntity<WProcessInstance> startProcessInstanceById(
            @RequestBody ProcessStartData processStartData) {
        return new ResponseEntity<>(
                processInstanceService.startProcessInstanceById(
                        processStartData.getActorId(),
                        processStartData.getProcessDefinitionId(),
                        variableRestToObjectMapConvert(processStartData.getVariables())),
                HttpStatus.OK);
    }

    @PostMapping(value="/{processInstanceId}/signal")
    public ResponseEntity<Void> signalProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody OptionalString signalName) {
        processInstanceService.signalProcessInstance(processInstanceId, signalName.getValue());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: Afegir possibilitat a Helium --> Mirar que fer amb la resta d'events
    @PostMapping(value="/{processInstanceId}/message")
    public ResponseEntity<Void> messageProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody String messageName) {
        processInstanceService.messageProcessInstance(processInstanceId, messageName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value="/{processInstanceId}")
    public ResponseEntity<Void> deleteProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        processInstanceService.deleteProcessInstance(processInstanceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/suspend")
    public ResponseEntity<Void> suspendProcessInstances(
            @RequestBody String[] processInstanceIds) {
        processInstanceService.suspendProcessInstances(processInstanceIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/resume")
    public ResponseEntity<Void> resumeProcessInstances(
            @RequestBody String[] processInstanceIds) {
        processInstanceService.resumeProcessInstances(processInstanceIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: Millorar --> Passar mapejos de tasques
    @PutMapping(value="/{processInstanceId}/version")
    public ResponseEntity<Void> changeProcessInstanceVersion(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody Integer newVersion) {
        processInstanceService.changeProcessInstanceVersion(processInstanceId, newVersion);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}