package es.caib.helium.client.engine.action;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.engine.model.ExpressionData;
import es.caib.helium.client.engine.model.ScriptData;
import es.caib.helium.client.engine.model.VariableRest;

public interface ActionFeginClient {

	@RequestMapping(method = RequestMethod.POST, value = ActionApiPath.EVALUATE_SCRIPT)
	public List<VariableRest> evaluateScript(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody ScriptData scriptData);
	
	@RequestMapping(method = RequestMethod.POST, value = ActionApiPath.EVALUATE_EXPRESSION_BY_PROCES_INSTANCE_ID)
	public Object evaluateExpression(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody ExpressionData expressionData);
	
	@RequestMapping(method = RequestMethod.GET, value = ActionApiPath.EVALUATE_EXPRESSION)
	public Object evaluateExpression(@RequestBody ExpressionData expressionData);
	
	@RequestMapping(method = RequestMethod.GET, value = ActionApiPath.LIST_ACTIONS)
	public List<String> listActions(
            @PathVariable("processDefinition") String processDefinition);
	
	@RequestMapping(method = RequestMethod.POST, value = ActionApiPath.EXECUTE_ACTION_INSTANCIA_PROCES)
	public void executeActionInstanciaProces(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("actionName") String actionName,
            @RequestParam(value = "processDefinitionPareId", required = false) String processDefinitionPareId);
	
	@RequestMapping(method = RequestMethod.POST, value = ActionApiPath.EXECUTE_ACTION_INSTANCIA_TASCA)
	public void executeActionInstanciaTasca(
            @PathVariable("taskInstanceId") String taskInstanceId,
            @PathVariable("actionName") String actionName,
            @RequestParam(value = "processDefinitionPareId", required = false) String processDefinitionPareId);
}
