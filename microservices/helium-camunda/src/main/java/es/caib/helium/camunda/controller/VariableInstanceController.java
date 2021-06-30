package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.VariableRest;
import es.caib.helium.camunda.service.VariableInstanceService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static es.caib.helium.camunda.helper.VariableHelper.objectMapToVariableRestConvert;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(VariableInstanceController.API_PATH)
public class VariableInstanceController {

    public static final String API_PATH = "/api/v1/processInstances";

    private final VariableInstanceService variableInstanceService;


    @GetMapping(value="/{processInstanceId}/processInstanceVariables")
    @ResponseBody
    public ResponseEntity<List<VariableRest>> getProcessInstanceVariables(
            @PathVariable("processInstanceId") String processInstanceId) {
        Map<String, Object> variables = variableInstanceService.getProcessInstanceVariables(processInstanceId);

        if (variables == null || variables.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(objectMapToVariableRestConvert(variables), HttpStatus.OK);
    }

    @GetMapping(value="/{processInstanceId}/processInstanceVariables/{varName}")
    @ResponseBody
    public ResponseEntity<VariableRest> getProcessInstanceVariable(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable(value = "varName") String varName) {
        VariableRest variable = variableInstanceService.getProcessInstanceVariable(processInstanceId, varName);
        if (variable == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(variable, HttpStatus.OK);

    }

    @PostMapping(value="/{processInstanceId}/processInstanceVariables")
    @ResponseBody
    public ResponseEntity<Void> setProcessInstanceVariable(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody VariableRest variable) {
        variableInstanceService.setProcessInstanceVariable(processInstanceId, variable);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value="/{processInstanceId}/processInstanceVariables/{varName}")
    @ResponseBody
    public ResponseEntity<Void> deleteProcessInstanceVariable(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("varName") String varName) {
        variableInstanceService.deleteProcessInstanceVariable(processInstanceId, varName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}