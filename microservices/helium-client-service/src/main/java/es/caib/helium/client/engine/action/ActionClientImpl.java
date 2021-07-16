package es.caib.helium.client.engine.action;

import es.caib.helium.client.engine.model.ExpressionData;
import es.caib.helium.client.engine.model.ScriptData;
import es.caib.helium.client.engine.model.VariableRest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActionClientImpl implements ActionClient {

	private final String MISSATGE_LOG = "Cridant Engine Service - Action - ";
	
	private final ActionFeginClient actionClient;

	@Override
	public List<VariableRest> evaluateScript(String processInstanceId, ScriptData scriptData) {
		
		log.debug(MISSATGE_LOG + " evaluant script pel processInstanceId " + processInstanceId + " script: " + scriptData.toString());
		return actionClient.evaluateScript(processInstanceId, scriptData);
	}

	@Override
	public Object evaluateExpression(String processInstanceId, ExpressionData expressionData) {
		
		log.debug(MISSATGE_LOG + " evaluant expression pel processInstanceId " + processInstanceId + " expression: " + expressionData.toString());
		return actionClient.evaluateExpression(processInstanceId, expressionData);
	}

//	@Override
//	public Object evaluateExpression(ExpressionData expressionData) {
//
//		log.debug(MISSATGE_LOG + " evaluant expression expression: " + expressionData.toString());
//		return actionClient.evaluateExpression(expressionData);
//	}

	@Override
	public List<String> listActions(String processDefinition) {
		
		log.debug(MISSATGE_LOG + " llistant accions per la definicio de proces " + processDefinition);
		return actionClient.listActions(processDefinition);
	}

	@Override
	public void executeActionInstanciaProces(String processInstanceId, String actionName, String processDefinitionPareId) {

		log.debug(MISSATGE_LOG + " Executar accio " + actionName + " per el processInstanceId " + processInstanceId + " amb processDefinitionPareId " + processDefinitionPareId);
		actionClient.executeActionInstanciaProces(processInstanceId, actionName, processDefinitionPareId);
	}

	@Override
	public void executeActionInstanciaTasca(String taskInstanceId, String actionName, String processDefinitionPareId) {

		log.debug(MISSATGE_LOG + " Executar accio tasca " + actionName + " per el taskInstanceId " + taskInstanceId + " amb processDefinitionPareId " + processDefinitionPareId);
		actionClient.executeActionInstanciaTasca(taskInstanceId, actionName, processDefinitionPareId);
	}
	
}
