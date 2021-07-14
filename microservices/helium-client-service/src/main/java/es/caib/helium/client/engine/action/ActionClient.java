package es.caib.helium.client.engine.action;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import es.caib.helium.client.engine.model.ExpressionData;
import es.caib.helium.client.engine.model.ScriptData;
import es.caib.helium.client.engine.model.VariableRest;

@Service
public interface ActionClient {

	public List<VariableRest> evaluateScript(String processInstanceId, ScriptData scriptData);
	
	public Object evaluateExpression(String processInstanceId, ExpressionData expressionData);
	
	public Object evaluateExpression(@RequestBody ExpressionData expressionData);
	
	public List<String> listActions(String processDefinition);
	
	public void executeActionInstanciaProces(String processInstanceId, String actionName, String processDefinitionPareId);
	
	public void executeActionInstanciaTasca(String taskInstanceId, String actionName, String processDefinitionPareId);
}
