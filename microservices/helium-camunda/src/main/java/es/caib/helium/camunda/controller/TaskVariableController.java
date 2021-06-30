package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.VariableRest;
import es.caib.helium.camunda.service.TaskVariableService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
    @ResponseBody
    public ResponseEntity<List<VariableRest>> getTaskInstanceVariables(
            @PathVariable("taskId") String taskId) {

        var variables = taskVariableService.getTaskInstanceVariables(taskId);

        if (variables == null || variables.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(variables, HttpStatus.OK);
    }

    @GetMapping(value="/{taskId}/taskInstanceVariables/{varName}")
    @ResponseBody
    public ResponseEntity<VariableRest> getTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName) {
        return new ResponseEntity<>(
                taskVariableService.getTaskInstanceVariable(taskId, varName),
                HttpStatus.OK);
    }

    @PostMapping(value="/{taskId}/taskInstanceVariables/{varName}")
    @ResponseBody
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
    @ResponseBody
    public ResponseEntity<Void> setTaskInstanceVariables(
            @PathVariable("taskId") String taskId,
            @RequestBody UpdateVars variables) {
        taskVariableService.setTaskInstanceVariables(
                taskId,
                variables.getVariables(),
                variables.isDeleteFirst());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value="/{taskId}/taskInstanceVariables/{varName}")
    @ResponseBody
    public ResponseEntity<Void> deleteTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName) {
        taskVariableService.deleteTaskInstanceVariable(taskId, varName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Data
    public static class UpdateVars {
        private boolean deleteFirst;
        private List<VariableRest> variables;
    }

}