package es.caib.helium.camunda.controller;

import es.caib.helium.camunda.service.ActionService;
import es.caib.helium.client.engine.model.ExpressionData;
import es.caib.helium.client.engine.model.ScriptData;
import es.caib.helium.client.engine.model.VariableRest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(ActionController.API_PATH)
public class ActionController {

    public static final String API_PATH = "/api/v1";

    private final ActionService actionService;


    @PostMapping(value="/processInstances/{processInstanceId}/evaluateScript")
    public List<VariableRest> evaluateScript(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody ScriptData scriptData) {
        return actionService.evaluateScript(
                processInstanceId,
                scriptData.getScriptLanguage(),
                scriptData.getScript(),
                scriptData.getOutputNames());
    }

    @PostMapping(value="/processInstances/{processInstanceId}/evaluateExpression")
    public Object evaluateExpression(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody ExpressionData expressionData) {
        return actionService.evaluateExpression(
                expressionData.getTaskInstanceInstanceId(),
                processInstanceId,
                expressionData.getExpressionLanguage(),
                expressionData.getExpression(),
                expressionData.getValors());
    }

    @GetMapping(value="/evaluateExpression")
    public Object evaluateExpression(@RequestBody ExpressionData expressionData) {
        return actionService.evaluateExpression(
                expressionData.getExpressionLanguage(),
                expressionData.getExpression(),
                expressionData.getExpectedClass(),
                expressionData.getValors());
    }

    @GetMapping(value="/processDefinitions/{processDefinition}/actions")
    public List<String> listActions(
            @PathVariable("processDefinition") String processDefinition) {
        return actionService.listActions(processDefinition);
    }

    // TODO: Herencia??

    @PostMapping(value="/processInstances/{processInstanceId}/actions/{actionName}/execute")
    public void executeActionInstanciaProces(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("actionName") String actionName,
            @RequestParam(value = "processDefinitionPareId", required = false) String processDefinitionPareId) {
        actionService.executeActionInstanciaProces(
                processInstanceId,
                actionName);
    }

    @PostMapping(value="/taskInstances/{taskInstanceId}/actions/{actionName}/execute")
    public void executeActionInstanciaTasca(
            @PathVariable("taskInstanceId") String taskInstanceId,
            @PathVariable("actionName") String actionName,
            @RequestParam(value = "processDefinitionPareId", required = false) String processDefinitionPareId) {
        actionService.executeActionInstanciaTasca(
                taskInstanceId,
                actionName);
    }

}