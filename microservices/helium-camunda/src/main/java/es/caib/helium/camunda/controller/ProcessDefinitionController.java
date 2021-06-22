package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.WProcessDefinition;
import es.caib.helium.camunda.service.ProcessDefinitionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ProcessDefinitionController.API_PATH)
public class ProcessDefinitionController {

    public static final String API_PATH = "/api/v1/processDefinitions";

    private final ProcessDefinitionService processDefinitionService;


    @GetMapping(value="/{processDefinitionId}")
    public ResponseEntity<WProcessDefinition> getProcessDefinition(
            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId) {
        return new ResponseEntity<>(
                processDefinitionService.getProcessDefinition(deploymentId, processDefinitionId),
                HttpStatus.OK);
    }

    @GetMapping(value="/{processDefinitionId}/subProcessDefinition")
    @ResponseBody
    public ResponseEntity<List<WProcessDefinition>> getSubProcessDefinitions(
            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId) {

        List<WProcessDefinition> processDefinitions = processDefinitionService.getSubProcessDefinitions(deploymentId, processDefinitionId);
        if (processDefinitions == null || processDefinitions.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(processDefinitions, HttpStatus.OK);
    }

    @GetMapping(value="/{processDefinitionId}/taskNames")
    @ResponseBody
    public ResponseEntity<List<String>> getTaskNamesFromDeployedProcessDefinition(
            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId) {

        List<String> taskNames = processDefinitionService.getTaskNamesFromDeployedProcessDefinition(deploymentId, processDefinitionId);
        if (taskNames == null || taskNames.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(taskNames, HttpStatus.OK);
    }

    @GetMapping(value="/{processDefinitionId}/startTaskName")
    @ResponseBody
    public ResponseEntity<String> getStartTaskName(
            @PathVariable("processDefinitionId") String processDefinitionId) {
        return new ResponseEntity<>(
                processDefinitionService.getStartTaskName(processDefinitionId),
                HttpStatus.OK);
    }

    @GetMapping(value="/byProcessInstance/{processInstanceId}")
    @ResponseBody
    public ResponseEntity<WProcessDefinition> findProcessDefinitionWithProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity<>(
                processDefinitionService.findProcessDefinitionWithProcessInstanceId(processInstanceId),
                HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Void> updateSubprocessDefinition(
            @RequestParam("processDefinitionId1") String processDefinitionId1,
            @RequestParam("processDefinitionId2") String processDefinitionId2) {

//        WProcessDefinition pd1 = processDefinitionService.getProcessDefinition(null, processDefinitionId1);
//        WProcessDefinition pd2 = processDefinitionService.getProcessDefinition(null, processDefinitionId2);
//        processDefinitionService.updateSubprocessDefinition(pd1, pd2);
//
//        return new ResponseEntity<>(HttpStatus.OK);

        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Mètode no disponible en Camunda");
    }

}