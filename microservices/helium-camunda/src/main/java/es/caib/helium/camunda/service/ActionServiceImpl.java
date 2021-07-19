package es.caib.helium.camunda.service;

import es.caib.helium.client.engine.helper.VariableHelper;
import es.caib.helium.client.engine.model.VariableRest;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.pvm.runtime.ExecutionImpl;
import org.camunda.bpm.engine.impl.scripting.ScriptFactory;
import org.camunda.bpm.engine.impl.scripting.SourceExecutableScript;
import org.camunda.bpm.engine.impl.scripting.engine.ScriptingEngines;
import org.camunda.bpm.engine.impl.scripting.env.ScriptingEnvironment;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.springframework.stereotype.Service;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {

    private final ProcessEngine processEngine;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final RepositoryService repositoryService;

    @Override
    public List<VariableRest> evaluateScript(
            String processInstanceId,
            String scriptFormat,
            String strScript,
            Set<String> outputNames) {

        var variables = new ArrayList<VariableRest>();

        ExecutionImpl execution = (ExecutionImpl) runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
//                .active()
                .list()
                .get(0);

        Object result = executeScript(scriptFormat, strScript, execution);

        if (result != null && outputNames != null && !outputNames.isEmpty()) {
            var variableRest = VariableHelper.objectToVariable(outputNames.stream().findFirst().get(), result);
            variables.add(variableRest);
        }

        return variables;


        //        ExecutableScript script = ScriptUtil.getScriptFromSource(
//                language,
//                strScript,
//                ScriptUtil.getScriptFactory());
//        script.execute();
//        var ex = (ProcessInstanceWithVariablesImpl)runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
//        BpmnExecutionContext executionContext = new BpmnExecutionContext();
//        Context.
//                .getProcessEngineConfiguration()
//                .getScriptFactory()
//                .getDelegateInterceptor()
//                .handleInvocation();

//        var engineConfiguration = (ProcessEngineConfigurationImpl)processEngine.getProcessEngineConfiguration()
//        ScriptFactory scriptFactory = engineConfiguration.getScriptFactory(); //new ScriptFactory();
//        ExecutableScript script = scriptFactory.createScriptFromSource("", strScript);
//        VariableScope variableScope = ??
//        script.

    }

    private Object executeScript(
            String scriptLanguage,
            String strScript,
            VariableScope execution) {
        final String language = (scriptLanguage == null || scriptLanguage.isEmpty()) ? JAVASCRIPT_SCRIPTING_LANGUAGE : scriptLanguage;
        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl)processEngine.getProcessEngineConfiguration();

        var result = processEngineConfiguration.getCommandExecutorTxRequired()
                .execute(new Command<Object>() {
                    @Override
                    public Object execute(CommandContext commandContext) {
                        ScriptingEngines scriptingEngines = processEngineConfiguration.getScriptingEngines();
                        ScriptEngine scriptEngine = scriptingEngines.getScriptEngineForLanguage(language);
                        ScriptFactory scriptFactory = processEngineConfiguration.getScriptFactory();

                        SourceExecutableScript script = (SourceExecutableScript) scriptFactory.createScriptFromSource(language, strScript);

                        ScriptingEnvironment scriptingEnvironment = processEngineConfiguration.getScriptingEnvironment();
                        Bindings bindings = scriptingEngines.createBindings(scriptEngine, execution);
                        return scriptingEnvironment.execute(script, execution, bindings, scriptEngine);
                    }
                });
        return result;
    }

    @Override
    public Object evaluateExpression(
            String taskInstanceInstanceId,
            String processInstanceId,
            String expressionLanguage,
            String expression,
            List<VariableRest> valors) {

        VariableScope variableScope = null;
        var variables = VariableHelper.variableRestToObjectMapConvert(valors);

        if (taskInstanceInstanceId != null) {
            var task = taskService.createTaskQuery()
                    .taskId(taskInstanceInstanceId)
                    .singleResult();
            if (task != null) {
                variableScope = (ExecutionImpl) runtimeService.createExecutionQuery()
                        .executionId(task.getExecutionId())
                        .singleResult();
            }
            if (variableScope == null) {
                variableScope = (ExecutionImpl) runtimeService.createExecutionQuery()
                        .processInstanceId(processInstanceId)
//                .active()
                        .list()
                        .get(0);
            }
        }
        if (!variables.isEmpty()) {
            variableScope.setVariables(variables);
        }

        return executeScript(
                expressionLanguage,
                expression,
                variableScope);
    }

    @Override
    public Object evaluateExpression(
            String expressionLanguage,
            String expression,
//            String expectedClass,
            List<VariableRest> context) {

        var variables = VariableHelper.variableRestToObjectMapConvert(context);
        VariableScope variableScope = new ExecutionImpl();
        variableScope.setVariables(variables);
        return executeScript(
                expressionLanguage,
                expression,
                variableScope);
    }

    @Override
    public List<String> listActions(String processDefinitionId) {
//        repositoryService.getBpmnModelInstance(processDefinitionId)
//                .getModelElementsByType(ExtensionElements.class);
        // TODO:  A Camunda les accions seran ServiceTask o ScriptTaks
        //        Modificar la consulta

        List<String> accions = new ArrayList<>();
        BpmnModelInstance modelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        var serviceTasks = modelInstance.getModelElementsByType(ServiceTask.class);
        var scriptTasks = modelInstance.getModelElementsByType(ScriptTask.class);
        if (serviceTasks != null) {
            accions.addAll(serviceTasks.stream().map(t -> t.getName()).collect(Collectors.toList()));
        }
        if (scriptTasks != null) {
            accions.addAll(scriptTasks.stream().map(t -> t.getName()).collect(Collectors.toList()));
        }

//        var actions = new ArrayList<String>();
//        var jobs = managementService.createJobDefinitionQuery()
//                .processDefinitionId(processDefinitionId)
//                .list();
//
//        if (jobs != null && !jobs.isEmpty()) {
//            jobs.stream().forEach(j -> actions.add(j.getId()));
//        }

        return accions;
    }


    // TODO:  A Camunda les accions seran ServiceTask o ScriptTaks
    @Override
    public void executeActionInstanciaProces(String processInstanceId, String actionName) {
        // Enviar missatge per a que s'executi una ServiceTask o ScriptTask
//        var processInstance = runtimeService.createProcessInstanceQuery()
//                .processInstanceId(processInstanceId)
//                .singleResult();
//        var subscription = runtimeService.createEventSubscriptionQuery()
//                .processInstanceId(processInstanceId)
//                .eventType("message")
//                .eventName(actionName)
//                .singleResult();
        runtimeService.createMessageCorrelation(actionName)
                .processInstanceId(processInstanceId)
                .correlateAll();
    }

    @Override
    public void executeActionInstanciaTasca(String taskInstanceId, String actionName) {
        // Enviar missatge per a que s'executi una ServiceTask o ScriptTask
        var subscription = runtimeService.createEventSubscriptionQuery()
//                .processInstanceId(processInstanceId)
                .activityId(taskInstanceId)
                .eventType("message")
                .eventName(actionName)
                .singleResult();

        var processVariables = new HashMap<String, Object>();
        processVariables.put("taskId", taskInstanceId);
        runtimeService.messageEventReceived(subscription.getEventName(), subscription.getExecutionId(), null);
    }

    @Override
    public void retrocedirAccio(String processInstanceId, String actionName, List<String> params) {
        // TODO: Revisar-ho per retroacció
    }
}
