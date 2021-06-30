package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.model.VariableRest;
import es.caib.helium.camunda.service.ActionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ActionController.API_PATH)
public class ActionController {

    public static final String API_PATH = "/api/v1/actions";

    private final ActionService actionService;


    @PostMapping(value="/processInstances/{processInstanceId}/evaluateScript")
    @ResponseBody
    public List<VariableRest> evaluateScript(
            @PathVariable("processInstanceId") String processInstanceId,
            String scriptLanguage,
            String script,
            Set<String> outputNames) {
        return actionService.evaluateScript(
                processInstanceId,
                scriptLanguage,
                script,
                outputNames);
    }

    @PostMapping(value="/processInstances/{processInstanceId}/evaluateExpression")
    @ResponseBody
    public Object evaluateExpression(
            String taskInstanceInstanceId,
            @PathVariable("processInstanceId") String processInstanceId,
            String expressionLanguage,
            String expression,
            List<VariableRest> valors) {
        return actionService.evaluateExpression(
                taskInstanceInstanceId,
                processInstanceId,
                expressionLanguage,
                expression,
                valors);
    }

    @GetMapping(value="/evaluateExpression")
    @ResponseBody
    public Object evaluateExpression(
            String expressionLanguage,
            String expression,
            String expectedClass,
            List<VariableRest> context) {
        return actionService.evaluateExpression(
                expressionLanguage,
                expression,
                expectedClass,
                context);
    }

    @GetMapping(value="/processDefinitions/{jbpmId}/actions")
    @ResponseBody
    public List<String> listActions(
            @PathVariable("jbpmId") String jbpmId) {
        return actionService.listActions(jbpmId);
    }

    @PostMapping(value="/processInstances/{processInstanceId}/actions/{actionName}/execute")
    @ResponseBody
    public void executeActionInstanciaProces(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("actionName") String actionName,
            @RequestParam(value = "processDefinitionPareId", required = false) String processDefinitionPareId) {
        actionService.executeActionInstanciaProces(
                processInstanceId,
                actionName);
    }

    @PostMapping(value="/taskInstances/{taskInstanceId}/actions/{actionName}/execute")
    @ResponseBody
    public void executeActionInstanciaTasca(
            @PathVariable("taskInstanceId") String taskInstanceId,
            @PathVariable("actionName") String actionName,
            @RequestParam(value = "processDefinitionPareId", required = false) String processDefinitionPareId) {
        actionService.executeActionInstanciaTasca(
                taskInstanceId,
                actionName);
    }

}