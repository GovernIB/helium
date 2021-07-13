package es.caib.helium.client.engine.action;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.client.engine.model.ExpressionData;
import es.caib.helium.client.engine.model.ScriptData;
import es.caib.helium.client.engine.model.VariableRest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {

	private final String missatgeLog = "Cridant Engine Service - Action - ";
	
	private ActionFeginClient actionClient;

	@Override
	public List<VariableRest> evaluateScript(String processInstanceId, ScriptData scriptData) {
		
		log.debug(missatgeLog + " evaluant script pel processInstanceId " + processInstanceId + " script: " + scriptData.toString());
		return actionClient.evaluateScript(processInstanceId, scriptData);
	}

	@Override
	public Object evaluateExpression(String processInstanceId, ExpressionData expressionData) {
		
		log.debug(missatgeLog + " evaluant expression pel processInstanceId " + processInstanceId + " expression: " + expressionData.toString());
		return actionClient.evaluateExpression(processInstanceId, expressionData);
	}

	@Override
	public Object evaluateExpression(ExpressionData expressionData) {
		
		log.debug(missatgeLog + " evaluant expression expression: " + expressionData.toString());
		return actionClient.evaluateExpression(expressionData);
	}

	@Override
	public List<String> listActions(String processDefinition) {
		
		log.debug(missatgeLog + " llistant accions per la definicio de proces " + processDefinition);
		return actionClient.listActions(processDefinition);
	}

	@Override
	public void executeActionInstanciaProces(String processInstanceId, String actionName, String processDefinitionPareId) {

		log.debug(missatgeLog + " Executar accio " + actionName + " per el processInstanceId " + processInstanceId + " amb processDefinitionPareId " + processDefinitionPareId);
		actionClient.executeActionInstanciaProces(processInstanceId, actionName, processDefinitionPareId);
	}

	@Override
	public void executeActionInstanciaTasca(String taskInstanceId, String actionName, String processDefinitionPareId) {

		log.debug(missatgeLog + " Executar accio tasca " + actionName + " per el taskInstanceId " + taskInstanceId + " amb processDefinitionPareId " + processDefinitionPareId);
		actionClient.executeActionInstanciaTasca(taskInstanceId, actionName, processDefinitionPareId);
	}
	
}
