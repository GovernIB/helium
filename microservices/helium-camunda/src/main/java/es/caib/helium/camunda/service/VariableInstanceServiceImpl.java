package es.caib.helium.camunda.service;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

import es.caib.helium.client.engine.model.VariableRest;
import lombok.RequiredArgsConstructor;

import static es.caib.helium.client.engine.helper.VariableHelper.objectToVariable;
import static es.caib.helium.client.engine.helper.VariableHelper.variableToObject;

@Service
@RequiredArgsConstructor
public class VariableInstanceServiceImpl implements VariableInstanceService {

    private final HistoryService historyService;
    private final RuntimeService runtimeService;

    @Override
    public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
        Map<String, Object> varMap = new HashMap<>();
        var variables = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
//        var vars = runtimeService
//                .createVariableInstanceQuery()
//                .processInstanceIdIn(processInstanceId)
//                .list();
        if (variables != null) {
            variables.stream().forEach(v -> varMap.put(v.getName(), v.getValue()));
        }
        return varMap;
    }

    @Override
    public VariableRest getProcessInstanceVariable(String processInstanceId, String varName) {
        var variable = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName(varName)
                .singleResult();
        if (variable == null) {
            return null;
        }
        return objectToVariable(varName, variable.getValue());
    }

    @Override
    public void setProcessInstanceVariable(String processInstanceId, VariableRest variable) {
        runtimeService.setVariable(processInstanceId, variable.getNom(), variableToObject(variable));
    }

    @Override
    public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
        runtimeService.removeVariable(processInstanceId, varName);
    }
}
