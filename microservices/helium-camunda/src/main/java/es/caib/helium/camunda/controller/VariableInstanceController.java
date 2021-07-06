package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.service.VariableInstanceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(VariableInstanceController.API_PATH)
public class VariableInstanceController {

    public static final String API_PATH = "/api/v1/processInstances";

    private final VariableInstanceService variableInstanceService;

    // TODO: Atacar al MS Dades
//    @GetMapping(value="/{processInstanceId}/processInstanceVariables")
//    @ResponseBody
//    public ResponseEntity<List<VariableRest>> getProcessInstanceVariables(
//            @PathVariable("processInstanceId") String processInstanceId) {
//        Map<String, Object> variables = variableInstanceService.getProcessInstanceVariables(processInstanceId);
//
//        if (variables == null || variables.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(objectMapToVariableRestConvert(variables), HttpStatus.OK);
//    }
//
//    @GetMapping(value="/{processInstanceId}/processInstanceVariables/{varName}")
//    @ResponseBody
//    public ResponseEntity<VariableRest> getProcessInstanceVariable(
//            @PathVariable("processInstanceId") String processInstanceId,
//            @PathVariable(value = "varName") String varName) {
//        VariableRest variable = variableInstanceService.getProcessInstanceVariable(processInstanceId, varName);
//        if (variable == null)
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        return new ResponseEntity<>(variable, HttpStatus.OK);
//
//    }
//
//    @PostMapping(value="/{processInstanceId}/processInstanceVariables")
//    @ResponseBody
//    public ResponseEntity<Void> setProcessInstanceVariable(
//            @PathVariable("processInstanceId") String processInstanceId,
//            @RequestBody VariableRest variable) {
//        variableInstanceService.setProcessInstanceVariable(processInstanceId, variable);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @DeleteMapping(value="/{processInstanceId}/processInstanceVariables/{varName}")
//    @ResponseBody
//    public ResponseEntity<Void> deleteProcessInstanceVariable(
//            @PathVariable("processInstanceId") String processInstanceId,
//            @PathVariable("varName") String varName) {
//        variableInstanceService.deleteProcessInstanceVariable(processInstanceId, varName);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

}