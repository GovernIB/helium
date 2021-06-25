package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.VariableRest;
import es.caib.helium.camunda.model.WProcessInstance;
import es.caib.helium.camunda.service.ProcessInstanceService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ProcessInstanceController.API_PATH)
public class ProcessInstanceController {

    public static final String API_PATH = "/api/v1/processInstances";

    private final ProcessInstanceService processInstanceService;


    @GetMapping(value="/byProcessDefinition/{processDefinitionId}")
    @ResponseBody
    public ResponseEntity<List<WProcessInstance>> findProcessInstancesWithProcessDefinitionId(
            @PathVariable("processDefinitionId") String processDefinitionId) {

        List<WProcessInstance> processInstances = processInstanceService.findProcessInstancesWithProcessDefinitionId(processDefinitionId);
        if (processInstances == null || processInstances.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(processInstances, HttpStatus.OK);
    }

    @GetMapping(value="/byProcessDefinitionName/{processName}")
    @ResponseBody
    public ResponseEntity<List<WProcessInstance>> findProcessInstancesWithProcessDefinitionNameAndEntorn(
            @PathVariable("processName") String processName,
            @RequestParam(value = "entornId", required = false) Long entornId) {
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
    @ResponseBody
    public ResponseEntity<List<WProcessInstance>> getProcessInstanceTree(
            @PathVariable("rootProcessInstanceId") String rootProcessInstanceId) {

        List<WProcessInstance> processInstances = processInstanceService.getProcessInstanceTree(rootProcessInstanceId);
        if (processInstances == null || processInstances.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(processInstances, HttpStatus.OK);
    }

    @GetMapping(value="/{processInstanceId}")
    @ResponseBody
    public ResponseEntity<WProcessInstance> getProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity<>(
                processInstanceService.getProcessInstance(processInstanceId),
                HttpStatus.OK);
    }

    @GetMapping(value="/{processInstanceId}/root")
    @ResponseBody
    public ResponseEntity<WProcessInstance> getRootProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity<>(
                processInstanceService.getRootProcessInstance(processInstanceId),
                HttpStatus.OK);
    }

    @GetMapping(value="/root")
    @ResponseBody
    public ResponseEntity<List<String>> findRootProcessInstances(
            @RequestParam(value = "actorId") String actorId,
            @RequestParam(value = "processInstanceIds") List<String> processInstanceIds,
            @RequestParam(value = "nomesMeves") boolean nomesMeves,
            @RequestParam(value = "nomesTasquesPersonals") boolean nomesTasquesPersonals,
            @RequestParam(value = "nomesTasquesGrup") boolean nomesTasquesGrup) {

        List<String> rootProcessInstances = processInstanceService.findRootProcessInstances(
                actorId,
                processInstanceIds,
                nomesMeves,
                nomesTasquesPersonals,
                nomesTasquesGrup);
        if (rootProcessInstances == null || rootProcessInstances.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(rootProcessInstances, HttpStatus.OK);
    }

    @PostMapping(value="/start")
    @ResponseBody
    public ResponseEntity<WProcessInstance> startProcessInstanceById(
            @RequestBody ProcessStart processStart) {
        return new ResponseEntity<>(
                processInstanceService.startProcessInstanceById(
                        processStart.getActorId(),
                        processStart.getProcessDefinitionId(),
                        variableRestToObjectMapConvert(processStart.getVariables())),
                HttpStatus.OK);
    }

    @PostMapping(value="/{processInstanceId}/signal/{transitionName}")
    @ResponseBody
    public ResponseEntity<Void> signalProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("transitionName") String transitionName) {
        processInstanceService.signalProcessInstance(processInstanceId, transitionName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value="/{processInstanceId}")
    @ResponseBody
    public ResponseEntity<Void> deleteProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        processInstanceService.deleteProcessInstance(processInstanceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/suspend")
    @ResponseBody
    public ResponseEntity<Void> suspendProcessInstances(
            @RequestBody String[] processInstanceIds) {
        processInstanceService.suspendProcessInstances(processInstanceIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/resume")
    @ResponseBody
    public ResponseEntity<Void> resumeProcessInstances(
            @RequestBody String[] processInstanceIds) {
        processInstanceService.resumeProcessInstances(processInstanceIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value="/{processInstanceId}/version/")
    @ResponseBody
    public ResponseEntity<Void> changeProcessInstanceVersion(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody Integer newVersion) {
        processInstanceService.changeProcessInstanceVersion(processInstanceId, newVersion);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    private Map<String, Object> variableRestToObjectMapConvert(List<VariableRest> variables) {
        var map = new HashMap<String, Object>();
        if (variables != null) {
            for(VariableRest variable: variables) {
                if (variable.getValor() != null) {
                    map.put(variable.getNom(), variable.getValor());
                }
            }
        }
        return map;
    }


    @Data
    public static class ProcessStart {
        private String actorId;
        private String processDefinitionId;
        private List<VariableRest> variables;
    }

}