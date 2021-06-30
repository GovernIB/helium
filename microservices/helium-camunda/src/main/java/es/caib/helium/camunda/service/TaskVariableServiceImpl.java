package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.VariableRest;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static es.caib.helium.camunda.helper.VariableHelper.objectMapToVariableRestConvert;
import static es.caib.helium.camunda.helper.VariableHelper.objectToVariable;
import static es.caib.helium.camunda.helper.VariableHelper.variableRestToObjectMapConvert;
import static es.caib.helium.camunda.helper.VariableHelper.variableToObject;

@Service
@RequiredArgsConstructor
public class TaskVariableServiceImpl implements TaskVariableService {

    private final TaskService taskService;


    @Override
    public List<VariableRest> getTaskInstanceVariables(String taskId) {
        return objectMapToVariableRestConvert(taskService.getVariablesLocal(taskId));
    }

    @Override
    public VariableRest getTaskInstanceVariable(String taskId, String varName) {
        var valor = taskService.getVariableLocal(taskId, varName);
        if (valor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Tasca: " + taskId + ", variable: " + varName);
        }
        return objectToVariable(varName, valor);
    }

    @Override
    public void setTaskInstanceVariable(String taskId, VariableRest variable) {
        taskService.setVariableLocal(taskId, variable.getNom(), variableToObject(variable));
    }

    @Override
    public void setTaskInstanceVariables(String taskId, List<VariableRest> variables, boolean deleteFirst) {
        taskService.setVariablesLocal(taskId, variableRestToObjectMapConvert(variables));
    }

    @Override
    public void deleteTaskInstanceVariable(String taskId, String varName) {
        taskService.removeVariableLocal(taskId, varName);
    }
}
