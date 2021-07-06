package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.service.TaskVariableService;
import es.caib.helium.client.engine.model.UpdateVariablesData;
import es.caib.helium.client.engine.model.VariableRest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(TaskVariableController.API_PATH)
public class TaskVariableController {

    public static final String API_PATH = "/api/v1/taskInstances";

    private final TaskVariableService taskVariableService;


    @GetMapping(value="/{taskId}/taskInstanceVariables")
    public ResponseEntity<List<VariableRest>> getTaskInstanceVariables(
            @PathVariable("taskId") String taskId) {

        var variables = taskVariableService.getTaskInstanceVariables(taskId);

        if (variables == null || variables.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(variables, HttpStatus.OK);
    }

    @GetMapping(value="/{taskId}/taskInstanceVariables/{varName}")
    public ResponseEntity<VariableRest> getTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName) {
        return new ResponseEntity<>(
                taskVariableService.getTaskInstanceVariable(taskId, varName),
                HttpStatus.OK);
    }

    @PostMapping(value="/{taskId}/taskInstanceVariables/{varName}")
    public ResponseEntity<Void> setTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName,
            @RequestBody VariableRest variable) {
        taskVariableService.setTaskInstanceVariable(
                taskId,
                variable);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value="/{taskId}/taskInstanceVariables")
    public ResponseEntity<Void> setTaskInstanceVariables(
            @PathVariable("taskId") String taskId,
            @RequestBody UpdateVariablesData variables) {
        taskVariableService.setTaskInstanceVariables(
                taskId,
                variables.getVariables(),
                variables.isDeleteFirst());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value="/{taskId}/taskInstanceVariables/{varName}")
    public ResponseEntity<Void> deleteTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName) {
        taskVariableService.deleteTaskInstanceVariable(taskId, varName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}