package es.caib.helium.back.rest.controller;

import es.caib.helium.api.dto.ExpedientDto;
import es.caib.helium.api.dto.LlistatIds;
import es.caib.helium.api.dto.PaginacioParamsDto;
import es.caib.helium.api.dto.Registre;
import es.caib.helium.api.dto.ResultatConsultaPaginada;
import es.caib.helium.api.dto.Termini;
import es.caib.helium.api.dto.TipusVar;
import es.caib.helium.api.service.WDeployment;
import es.caib.helium.api.service.WProcessDefinition;
import es.caib.helium.api.service.WProcessInstance;
import es.caib.helium.api.service.WTaskInstance;
import es.caib.helium.api.service.WToken;
import es.caib.helium.api.service.WorkflowEngineApi;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

@Slf4j
@Controller
@RequestMapping("/engine/api")
public class WorkflowEngineController {

    @Autowired
    WorkflowEngineApi workflowEngineApi;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    // DEFINICIÓ DE PROCÉS
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    // Desplegaments
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/desplegaments", method = RequestMethod.POST)
	@ResponseBody
    public WDeployment desplegar(
            @RequestPart("file") MultipartFile file) throws IOException {
//            String nomArxiu,
//            byte[] contingut) {
        return workflowEngineApi.desplegar(
                file.getName(),
                file.getBytes());
    }

    @RequestMapping(value="/desplegaments/{deploymentId}", method = RequestMethod.GET)
	@ResponseBody
    public WDeployment getDesplegament(@PathVariable("deploymentId") String deploymentId) {
        return workflowEngineApi.getDesplegament(deploymentId);
    }

    @RequestMapping(value="/desplegaments/{deploymentId}", method = RequestMethod.DELETE)
	@ResponseBody
    public void esborrarDesplegament(@PathVariable("deploymentId") String deploymentId) {
        workflowEngineApi.esborrarDesplegament(deploymentId);
    }

    @RequestMapping(value="/desplegaments/{deploymentId}/resourceNames", method = RequestMethod.GET)
	@ResponseBody
    public Set<String> getResourceNames(@PathVariable("deploymentId") String deploymentId) {
        return workflowEngineApi.getResourceNames(deploymentId);
    }

    @RequestMapping(value="/desplegaments/{deploymentId}/resources/{resourceName}", method = RequestMethod.GET)
	@ResponseBody
    public byte[] getResourceBytes(
            @PathVariable("deploymentId") String deploymentId,
            @PathVariable("resourceName") String resourceName) {
        return workflowEngineApi.getResourceBytes(deploymentId, resourceName);
    }

    @RequestMapping(value="/desplegaments/{deploymentId}/actions", method = RequestMethod.PUT)
	@ResponseBody
    public void updateDeploymentActions(
            @PathVariable("deploymentId") String deploymentId,
            @RequestPart("handlers") List<MultipartFile> handlers) throws IOException {

        Map<String, byte[]> handlerBytes = new HashMap<String, byte[]>();
        if (handlers != null && !handlers.isEmpty()) {
            for (MultipartFile handler: handlers) {
                handlerBytes.put(handler.getName(), handler.getBytes());
            }
        }
        workflowEngineApi.updateDeploymentActions(Long.parseLong(deploymentId), handlerBytes);
    }

    // Definicions de Procés
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/processDefinitions/{processDefinitionId}", method = RequestMethod.GET)
	@ResponseBody
    public WProcessDefinition getProcessDefinition(
            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId) {
        return workflowEngineApi.getProcessDefinition(deploymentId, processDefinitionId);
    }

    @RequestMapping(value="/processDefinitions/{processDefinitionId}/subProcessDefinition", method = RequestMethod.GET)
	@ResponseBody
    public List<WProcessDefinition> getSubProcessDefinitions(
            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId) {
        return workflowEngineApi.getSubProcessDefinitions(deploymentId, processDefinitionId);
    }

    @RequestMapping(value="/processDefinitions/{processDefinitionId}/taskNames", method = RequestMethod.GET)
	@ResponseBody
    public List<String> getTaskNamesFromDeployedProcessDefinition(
            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId) {
        WDeployment dpd = workflowEngineApi.getDesplegament(deploymentId);
        return workflowEngineApi.getTaskNamesFromDeployedProcessDefinition(dpd, processDefinitionId);
    }

    @RequestMapping(value="/processDefinitions/{processDefinitionId}/startTaskName", method = RequestMethod.GET)
	@ResponseBody
    public String getStartTaskName(
            @PathVariable("processDefinitionId") String processDefinitionId) {
        return workflowEngineApi.getStartTaskName(processDefinitionId);
    }

    @RequestMapping(value="/processDefinitions/byProcessInstance/{processInstanceId}", method = RequestMethod.GET)
	@ResponseBody
    public WProcessDefinition findProcessDefinitionWithProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId) {
        return workflowEngineApi.findProcessDefinitionWithProcessInstanceId(processInstanceId);
    }

    @RequestMapping(value="/processDefinitions", method = RequestMethod.PUT)
	@ResponseBody
    public void updateSubprocessDefinition(
            @RequestParam("processDefinitionId1") String processDefinitionId1,
            @RequestParam("processDefinitionId2") String processDefinitionId2) {

        WProcessDefinition pd1 = workflowEngineApi.getProcessDefinition(null, processDefinitionId1);
        WProcessDefinition pd2 = workflowEngineApi.getProcessDefinition(null, processDefinitionId2);
        workflowEngineApi.updateSubprocessDefinition(pd1, pd2);
    }

    // DEFINICIÓ DE TASQUES
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////


    // INSTÀNCIA DE PROCÉS
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/processInstances/byProcessDefinition/{processDefinitionId}", method = RequestMethod.GET)
	@ResponseBody
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(
            @PathVariable("processDefinitionId") String processDefinitionId) {
        return workflowEngineApi.findProcessInstancesWithProcessDefinitionId(processDefinitionId);
    }

    @RequestMapping(value="/processInstances/byProcessDefinitionName/{processName}", method = RequestMethod.GET)
	@ResponseBody
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(
            @PathVariable("processName") String processName,
            @RequestParam(value = "entornId", required = false) Long entornId) {
        if (entornId != null) {
            return workflowEngineApi.findProcessInstancesWithProcessDefinitionNameAndEntorn(processName, entornId);
        } else {
            return workflowEngineApi.findProcessInstancesWithProcessDefinitionName(processName);
        }
    }

    @RequestMapping(value="/processInstances/{rootProcessInstanceId}/tree", method = RequestMethod.GET)
	@ResponseBody
    public List<WProcessInstance> getProcessInstanceTree(
            @PathVariable("rootProcessInstanceId") String rootProcessInstanceId) {
        return workflowEngineApi.getProcessInstanceTree(rootProcessInstanceId);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}", method = RequestMethod.GET)
	@ResponseBody
    public WProcessInstance getProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        return workflowEngineApi.getProcessInstance(processInstanceId);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/root", method = RequestMethod.GET)
	@ResponseBody
    public WProcessInstance getRootProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        return workflowEngineApi.getRootProcessInstance(processInstanceId);
    }

    @RequestMapping(value="/processInstances/root", method = RequestMethod.GET)
    @ResponseBody
    public List<String> findRootProcessInstances(
            @RequestParam(value = "actorId") String actorId,
            @RequestParam(value = "processInstanceIds") List<String> processInstanceIds,
            @RequestParam(value = "nomesMeves") boolean nomesMeves,
            @RequestParam(value = "nomesTasquesPersonals") boolean nomesTasquesPersonals,
            @RequestParam(value = "nomesTasquesGrup") boolean nomesTasquesGrup) {
        return workflowEngineApi.findRootProcessInstances(
                actorId,
                processInstanceIds,
                nomesMeves,
                nomesTasquesPersonals,
                nomesTasquesGrup);
    }

    @RequestMapping(value="/processInstances/start", method = RequestMethod.POST)
	@ResponseBody
    public WProcessInstance startProcessInstanceById(
            @RequestBody ProcessStart processStart) {
        return workflowEngineApi.startProcessInstanceById(
                processStart.getActorId(),
                processStart.getProcessDefinitionId(),
                variableRestToObjectMaConvert(processStart.getVariables()));
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/signal/{transitionName}", method = RequestMethod.POST)
	@ResponseBody
    public void signalProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("transitionName") String transitionName) {
        workflowEngineApi.signalProcessInstance(processInstanceId, transitionName);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}", method = RequestMethod.DELETE)
	@ResponseBody
    public void deleteProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        workflowEngineApi.deleteProcessInstance(processInstanceId);
    }

    @RequestMapping(value="/processInstances/suspend", method = RequestMethod.POST)
	@ResponseBody
    public void suspendProcessInstances(
            @RequestBody String[] processInstanceIds) {
        workflowEngineApi.suspendProcessInstances(processInstanceIds);
    }

    @RequestMapping(value="/processInstances/resume", method = RequestMethod.POST)
	@ResponseBody
    public void resumeProcessInstances(
            @RequestBody String[] processInstanceIds) {
        workflowEngineApi.resumeProcessInstances(processInstanceIds);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/version/", method = RequestMethod.PUT)
	@ResponseBody
    public void changeProcessInstanceVersion(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody Integer newVersion) {
        workflowEngineApi.changeProcessInstanceVersion(processInstanceId, newVersion);
    }


    // VARIABLES DE PROCÉS
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/processInstances/{processInstanceId}/processInstanceVariables", method = RequestMethod.GET)
	@ResponseBody
    public List<VariableRest> getProcessInstanceVariables(
            @PathVariable("processInstanceId") String processInstanceId) {
        Map<String, Object> variables = workflowEngineApi.getProcessInstanceVariables(processInstanceId);
        return objectMapToVariableRestConvert(variables);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/processInstanceVariables/{varName}", method = RequestMethod.GET)
	@ResponseBody
    public VariableRest getProcessInstanceVariable(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable(value = "varName") String varName) {
        Object valor = workflowEngineApi.getProcessInstanceVariable(processInstanceId, varName);
        if (valor != null)
            return new VariableRest(varName, valor);
        return null;
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/processInstanceVariables", method = RequestMethod.POST)
	@ResponseBody
    public void setProcessInstanceVariable(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody VariableRest variable) {
        workflowEngineApi.setProcessInstanceVariable(processInstanceId, variable.getNom(), variable.convertirValor());
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/processInstanceVariables/{varName}", method = RequestMethod.DELETE)
	@ResponseBody
    public void deleteProcessInstanceVariable(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("varName") String varName) {
        workflowEngineApi.deleteProcessInstanceVariable(processInstanceId, varName);
    }


    // INSTÀNCIA DE TASQUES
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/taskInstances/{taskId}", method = RequestMethod.GET)
	@ResponseBody
    public WTaskInstance getTaskById(
            @PathVariable("taskId") String taskId) {
        return workflowEngineApi.getTaskById(taskId);
    }

    @RequestMapping(value="/taskInstances/byProcessInstance/{processInstanceId}", method = RequestMethod.GET)
	@ResponseBody
    public List<WTaskInstance> findTaskInstancesByProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId) {
        return workflowEngineApi.findTaskInstancesByProcessInstanceId(processInstanceId);
    }

    @RequestMapping(value="/taskInstances/byExecution/{executionTokenId}/id", method = RequestMethod.GET)
	@ResponseBody
    public Long getTaskInstanceIdByExecutionTokenId(
            @PathVariable("executionTokenId") Long executionTokenId) {
        return workflowEngineApi.getTaskInstanceIdByExecutionTokenId(executionTokenId);
    }

    @RequestMapping(value="/taskInstances/byFiltrePaginat", method = RequestMethod.GET)
    @ResponseBody
    public ResultatConsultaPaginada<WTaskInstance> tascaFindByFiltrePaginat(
            @RequestParam(value = "entornId", required = false) Long entornId,
            @RequestParam(value = "actorId", required = false) String actorId,
            @RequestParam(value = "taskName", required = false) String taskName,
            @RequestParam(value = "titol", required = false) String titol,
            @RequestParam(value = "expedientId", required = false) Long expedientId,
            @RequestParam(value = "expedientTitol", required = false) String expedientTitol,
            @RequestParam(value = "expedientNumero", required = false) String expedientNumero,
            @RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
            @RequestParam(value = "dataCreacioInici", required = false) Date dataCreacioInici,
            @RequestParam(value = "dataCreacioFi", required = false) Date dataCreacioFi,
            @RequestParam(value = "prioritat", required = false) Integer prioritat,
            @RequestParam(value = "dataLimitInici", required = false) Date dataLimitInici,
            @RequestParam(value = "dataLimitFi", required = false) Date dataLimitFi,
            @RequestParam(value = "mostrarAssignadesUsuari", required = false) boolean mostrarAssignadesUsuari,
            @RequestParam(value = "mostrarAssignadesGrup", required = false) boolean mostrarAssignadesGrup,
            @RequestParam(value = "nomesPendents", required = false) boolean nomesPendents,
            @RequestParam(value = "paginacioParams") PaginacioParamsDto paginacioParams,
            @RequestParam(value = "nomesCount", required = false) boolean nomesCount) {
        return workflowEngineApi.tascaFindByFiltrePaginat(
                entornId,
                actorId,
                taskName,
                titol,
                expedientId,
                expedientTitol,
                expedientNumero,
                expedientTipusId,
                dataCreacioInici,
                dataCreacioFi,
                prioritat,
                dataLimitInici,
                dataLimitFi,
                mostrarAssignadesUsuari,
                mostrarAssignadesGrup,
                nomesPendents,
                paginacioParams,
                nomesCount);
    }

    @RequestMapping(value="/taskInstances/byFiltrePaginat/ids", method = RequestMethod.GET)
    @ResponseBody
    public LlistatIds tascaIdFindByFiltrePaginat(
            @RequestParam(value = "responsable", required = false) String responsable,
            @RequestParam(value = "tasca", required = false) String tasca,
            @RequestParam(value = "tascaSel", required = false) String tascaSel,
            @RequestParam(value = "idsPIExpedients", required = false) List<Long> idsPIExpedients,
            @RequestParam(value = "dataCreacioInici", required = false) Date dataCreacioInici,
            @RequestParam(value = "dataCreacioFi", required = false) Date dataCreacioFi,
            @RequestParam(value = "prioritat", required = false) Integer prioritat,
            @RequestParam(value = "dataLimitInici", required = false) Date dataLimitInici,
            @RequestParam(value = "dataLimitFi", required = false) Date dataLimitFi,
            @RequestParam(value = "paginacioParams") PaginacioParamsDto paginacioParams,
            @RequestParam(value = "nomesTasquesPersonals", required = false) boolean nomesTasquesPersonals,
            @RequestParam(value = "nomesTasquesGrup", required = false) boolean nomesTasquesGrup,
            @RequestParam(value = "nomesAmbPendents", required = false) boolean nomesAmbPendents) {
        return workflowEngineApi.tascaIdFindByFiltrePaginat(
                responsable,
                tasca,
                tascaSel,
                idsPIExpedients,
                dataCreacioInici,
                dataCreacioFi,
                prioritat,
                dataLimitInici,
                dataLimitFi,
                paginacioParams,
                nomesTasquesPersonals,
                nomesTasquesGrup,
                nomesAmbPendents);
    }

    @RequestMapping(value="/taskInstances/{taskId}/take/{actorId}", method = RequestMethod.POST)
	@ResponseBody
    public void takeTaskInstance(
            @PathVariable("taskId") String taskId,
            @PathVariable("actorId") String actorId) {
        workflowEngineApi.takeTaskInstance(taskId, actorId);
    }

    @RequestMapping(value="/taskInstances/{taskId}/release", method = RequestMethod.POST)
	@ResponseBody
    public void releaseTaskInstance(
            @PathVariable("taskId") String taskId) {
        workflowEngineApi.releaseTaskInstance(taskId);
    }

    @RequestMapping(value="/taskInstances/{taskId}/start", method = RequestMethod.POST)
	@ResponseBody
    public WTaskInstance startTaskInstance(
            @PathVariable("taskId") String taskId) {
        return workflowEngineApi.startTaskInstance(taskId);
    }

    @RequestMapping(value="/taskInstances/{taskId}/end", method = RequestMethod.POST)
	@ResponseBody
    public void endTaskInstance(
            @PathVariable("taskId") String taskId,
            @RequestParam(value = "outcome", required = false) String outcome) {
        workflowEngineApi.endTaskInstance(taskId, outcome);
    }

    @RequestMapping(value="/taskInstances/{taskId}/cancel", method = RequestMethod.POST)
	@ResponseBody
    public WTaskInstance cancelTaskInstance(
            @PathVariable("taskId") String taskId) {
        return workflowEngineApi.cancelTaskInstance(taskId);
    }

    @RequestMapping(value="/taskInstances/{taskId}/suspend", method = RequestMethod.POST)
	@ResponseBody
    public WTaskInstance suspendTaskInstance(
            @PathVariable("taskId") String taskId) {
        return workflowEngineApi.suspendTaskInstance(taskId);
    }

    @RequestMapping(value="/taskInstances/{taskId}/resume", method = RequestMethod.POST)
	@ResponseBody
    public WTaskInstance resumeTaskInstance(
            @PathVariable("taskId") String taskId) {
        return workflowEngineApi.resumeTaskInstance(taskId);
    }

    @RequestMapping(value="/taskInstances/{taskId}/reassign", method = RequestMethod.POST)
	@ResponseBody
    public WTaskInstance reassignTaskInstance(
            @PathVariable("taskId") String taskId,
            ReassignTask reassignTask) {
        return workflowEngineApi.reassignTaskInstance(
                taskId,
                reassignTask.getExpression(),
                reassignTask.getEntornId());
    }

    @RequestMapping(value="/taskInstances/{taskId}", method = RequestMethod.PUT)
	@ResponseBody
    public void updateTaskInstanceInfoCache(
            @PathVariable("taskId") String taskId,
            @RequestBody InfoCache info) {
        workflowEngineApi.updateTaskInstanceInfoCache(taskId, info.getTitol(), info.getInfo());
    }


    // VARIABLES DE TASQUES
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/taskInstances/{taskId}/taskInstanceVariables", method = RequestMethod.GET)
	@ResponseBody
    public List<VariableRest> getTaskInstanceVariables(
            @PathVariable("taskId") String taskId) {
        Map<String, Object> variables = workflowEngineApi.getTaskInstanceVariables(taskId);
        return objectMapToVariableRestConvert(variables);
    }

    @RequestMapping(value="/taskInstances/{taskId}/taskInstanceVariables/{varName}", method = RequestMethod.GET)
	@ResponseBody
    public Object getTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName) {
        return workflowEngineApi.getTaskInstanceVariable(taskId, varName);
    }

    @RequestMapping(value="/taskInstances/{taskId}/taskInstanceVariables/{varName}", method = RequestMethod.POST)
	@ResponseBody
    public void setTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName,
            @RequestBody VariableRest variable) {
        workflowEngineApi.setTaskInstanceVariable(
                taskId,
                varName,
                variable.getValor());
    }

    @RequestMapping(value="/taskInstances/{taskId}/taskInstanceVariables", method = RequestMethod.POST)
	@ResponseBody
    public void setTaskInstanceVariables(
            @PathVariable("taskId") String taskId,
            @RequestBody UpdateVars variables) {
        workflowEngineApi.setTaskInstanceVariables(
                taskId,
                variableRestToObjectMaConvert(variables.getVariables()),
                variables.isDeleteFirst());
    }

    @RequestMapping(value="/taskInstances/{taskId}/taskInstanceVariables/{varName}", method = RequestMethod.DELETE)
	@ResponseBody
    public void deleteTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName) {
        workflowEngineApi.deleteTaskInstanceVariable(taskId, varName);
    }


    // FILS D'EXECUCIÓ (Token / Execution path)
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/executions/{tokenId}", method = RequestMethod.GET)
	@ResponseBody
    public WToken getTokenById(
            @PathVariable("tokenId") String tokenId) {
        return workflowEngineApi.getTokenById(tokenId);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/tokens/active", method = RequestMethod.GET)
	@ResponseBody
    public Map<String, WToken> getActiveTokens(
            @PathVariable("processInstanceId") String processInstanceId) {
        return workflowEngineApi.getActiveTokens(processInstanceId);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/tokens", method = RequestMethod.GET)
	@ResponseBody
    public Map<String, WToken> getAllTokens(
            @PathVariable("processInstanceId") String processInstanceId) {
        return workflowEngineApi.getAllTokens(processInstanceId);
    }

    @RequestMapping(value="/executions/{tokenId}", method = RequestMethod.POST)
	@ResponseBody
    public void tokenRedirect(
            @PathVariable("tokenId") Long tokenId,
            String nodeName,
            boolean cancelTasks,
            boolean enterNodeIfTask,
            boolean executeNode) {
        workflowEngineApi.tokenRedirect(tokenId,
                nodeName,
                cancelTasks,
                enterNodeIfTask,
                executeNode);
    }

    @RequestMapping(value="/executions/{tokenId}/activar/{activar}", method = RequestMethod.POST)
	@ResponseBody
    public boolean tokenActivar(
            @PathVariable("tokenId") Long tokenId,
            @PathVariable("activar") boolean activar) {
        return workflowEngineApi.tokenActivar(tokenId, activar);
    }

    @RequestMapping(value="/executions/{tokenId}/signal", method = RequestMethod.POST)
	@ResponseBody
    public void signalToken(
            @PathVariable("tokenId") Long tokenId,
            String transitionName) {
        workflowEngineApi.signalToken(tokenId, transitionName);
    }


    // ACCIONS
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/processInstances/{processInstanceId}/evaluateScript", method = RequestMethod.POST)
	@ResponseBody
    public List<VariableRest> evaluateScript(
            @PathVariable("processInstanceId") String processInstanceId,
            String script,
            Set<String> outputNames) {
        return objectMapToVariableRestConvert(
                workflowEngineApi.evaluateScript(
                        processInstanceId,
                        script,
                        outputNames));
    }

    // TODO: Eliminar Object
    @RequestMapping(value="/processInstances/{processInstanceId}/evaluateExpression", method = RequestMethod.POST)
	@ResponseBody
    public Object evaluateExpression(
            String taskInstanceInstanceId,
            @PathVariable("processInstanceId") String processInstanceId,
            String expression,
            Map<String, Object> valors) {
        return workflowEngineApi.evaluateExpression(
                taskInstanceInstanceId,
                processInstanceId,
                expression,
                valors);
    }

    @RequestMapping(value="/evaluateExpression", method = RequestMethod.GET)
    @ResponseBody
    public Object evaluateExpression(
            String expression,
            Class<?> expectedClass,
            Map<String, Object> context) {
        return workflowEngineApi.evaluateExpression(expression, expectedClass, context);
    }

    // TODO: Fi


    @RequestMapping(value="/processDefinitions/{jbpmId}/actions", method = RequestMethod.GET)
	@ResponseBody
    public List<String> listActions(
            @PathVariable("jbpmId") String jbpmId) {
        return workflowEngineApi.listActions(jbpmId);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/actions/{actionName}/execute", method = RequestMethod.POST)
	@ResponseBody
    public void executeActionInstanciaProces(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("actionName") String actionName,
            @RequestParam(value = "processDefinitionPareId", required = false) String processDefinitionPareId) {
        workflowEngineApi.executeActionInstanciaProces(
                processInstanceId,
                actionName,
                processDefinitionPareId);
    }

    @RequestMapping(value="/taskInstances/{taskInstanceId}/actions/{actionName}/execute", method = RequestMethod.POST)
	@ResponseBody
    public void executeActionInstanciaTasca(
            @PathVariable("taskInstanceId") String taskInstanceId,
            @PathVariable("actionName") String actionName,
            @RequestParam(value = "processDefinitionPareId", required = false) String processDefinitionPareId) {
        workflowEngineApi.executeActionInstanciaTasca(
                taskInstanceId,
                actionName,
                processDefinitionPareId);
    }


    // TIMERS
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/timers/{timerId}/suspend", method = RequestMethod.POST)
	@ResponseBody
    public void suspendTimer(
            @PathVariable("timerId") Long timerId,
            @RequestBody Date dueDate) {
        workflowEngineApi.suspendTimer(timerId, dueDate);
    }

    @RequestMapping(value="/timers/{timerId}/resume", method = RequestMethod.POST)
	@ResponseBody
    public void resumeTimer(
            @PathVariable("timerId") Long timerId,
            @RequestBody Date dueDate) {
        workflowEngineApi.resumeTimer(timerId, dueDate);
    }







    @RequestMapping(value="/processDefinitions/{processDefinitionId}/tasks/{taskName}/leavingTransitions", method = RequestMethod.GET)
	@ResponseBody
    public List<String> findStartTaskOutcomes(
            @PathVariable("processDefinitionId") String processDefinitionId,
            @PathVariable("taskName") String taskName) {
        return workflowEngineApi.findStartTaskOutcomes(processDefinitionId, taskName);
    }

    @RequestMapping(value="/taskInstances/{taskInstanceId}/leavingTransitions", method = RequestMethod.GET)
	@ResponseBody
    public List<String> findTaskInstanceOutcomes(
            @PathVariable("taskInstanceId") String taskInstanceId) {
        return workflowEngineApi.findTaskInstanceOutcomes(taskInstanceId);
    }

    @RequestMapping(value="/executions/{tokenId}/arrivingNodes", method = RequestMethod.GET)
	@ResponseBody
    public List<String> findArrivingNodeNames(
            @PathVariable("tokenId") String tokenId) {
        return workflowEngineApi.findArrivingNodeNames(tokenId);
    }

    @RequestMapping(value="/expedients/byProcessInstance/{processInstanceId}", method = RequestMethod.GET)
	@ResponseBody
    public ExpedientDto expedientFindByProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId) {
        return workflowEngineApi.expedientFindByProcessInstanceId(processInstanceId);
    }

    @RequestMapping(value="/expedients/ids", method = RequestMethod.GET)
    @ResponseBody
    public ResultatConsultaPaginada<Long> expedientFindByFiltre(
            @RequestParam(value = "entornId") Long entornId,
            @RequestParam(value = "actorId", required = false) String actorId,
            @RequestParam(value = "tipusIdPermesos", required = false) Collection<Long> tipusIdPermesos,
            @RequestParam(value = "titol", required = false) String titol,
            @RequestParam(value = "numero", required = false) String numero,
            @RequestParam(value = "tipusId", required = false) Long tipusId,
            @RequestParam(value = "dataCreacioInici", required = false) Date dataCreacioInici,
            @RequestParam(value = "dataCreacioFi", required = false) Date dataCreacioFi,
            @RequestParam(value = "dataFiInici", required = false) Date dataFiInici,
            @RequestParam(value = "dataFiFi", required = false) Date dataFiFi,
            @RequestParam(value = "estatId", required = false) Long estatId,
            @RequestParam(value = "geoPosX", required = false) Double geoPosX,
            @RequestParam(value = "geoPosY", required = false) Double geoPosY,
            @RequestParam(value = "geoReferencia", required = false) String geoReferencia,
            @RequestParam(value = "nomesIniciats", required = false) boolean nomesIniciats,
            @RequestParam(value = "nomesFinalitzats", required = false) boolean nomesFinalitzats,
            @RequestParam(value = "mostrarAnulats", required = false) boolean mostrarAnulats,
            @RequestParam(value = "mostrarNomesAnulats", required = false) boolean mostrarNomesAnulats,
            @RequestParam(value = "nomesAlertes", required = false) boolean nomesAlertes,
            @RequestParam(value = "nomesErrors", required = false) boolean nomesErrors,
            @RequestParam(value = "nomesTasquesPersonals", required = false) boolean nomesTasquesPersonals,
            @RequestParam(value = "nomesTasquesGrup", required = false) boolean nomesTasquesGrup,
            @RequestParam(value = "nomesTasquesMeves", required = false) boolean nomesTasquesMeves,
            @RequestParam(value = "paginacioParams") PaginacioParamsDto paginacioParams,
            @RequestParam(value = "nomesCount", required = false) boolean nomesCount) {
        return workflowEngineApi.expedientFindByFiltre(
                entornId,
                actorId,
                tipusIdPermesos,
                titol,
                numero,
                tipusId,
                dataCreacioInici,
                dataCreacioFi,
                dataFiInici,
                dataFiFi,
                estatId,
                geoPosX,
                geoPosY,
                geoReferencia,
                nomesIniciats,
                nomesFinalitzats,
                mostrarAnulats,
                mostrarNomesAnulats,
                nomesAlertes,
                nomesErrors,
                nomesTasquesPersonals,
                nomesTasquesGrup,
                nomesTasquesMeves,
                paginacioParams,
                nomesCount);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/desfinalitzar", method = RequestMethod.POST)
	@ResponseBody
    public void desfinalitzarExpedient(
            @PathVariable("processInstanceId") String processInstanceId) {
        workflowEngineApi.desfinalitzarExpedient(processInstanceId);
    }

    @RequestMapping(value="/processInstances/finalitzar", method = RequestMethod.POST)
	@ResponseBody
    public void finalitzarExpedient(
            @RequestBody ProcessEnd processEnd) {
        workflowEngineApi.finalitzarExpedient(
                processEnd.getProcessInstanceIds(),
                processEnd.getDataFinalitzacio());
    }

    @RequestMapping(value="/taskInstances/{taskId}/marcarFinalitzar", method = RequestMethod.POST)
	@ResponseBody
    public void marcarFinalitzar(
            @PathVariable("taskId") String taskId,
            MarcarFinalitzar marcarFinalitzar) {
        workflowEngineApi.marcarFinalitzar(
                taskId,
                marcarFinalitzar.getMarcadaFinalitzar(),
                marcarFinalitzar.getOutcome(),
                marcarFinalitzar.getRols());
    }

    @RequestMapping(value="/taskInstances/{taskId}/marcarIniciFinalitzarSegonPla", method = RequestMethod.POST)
	@ResponseBody
    public void marcarIniciFinalitzacioSegonPla(
            @PathVariable("taskId") String taskId,
            @RequestBody Date iniciFinalitzacio) {
        workflowEngineApi.marcarIniciFinalitzacioSegonPla(taskId, iniciFinalitzacio);
    }

    @RequestMapping(value="/taskInstances/{taskId}/errorFinalitzacio", method = RequestMethod.POST)
	@ResponseBody
    public void guardarErrorFinalitzacio(
            @PathVariable("taskId") String taskId,
            @RequestBody String errorFinalitzacio) {
        workflowEngineApi.guardarErrorFinalitzacio(taskId, errorFinalitzacio);
    }

    @RequestMapping(value="/taskInstances/bySegonPlaPendents", method = RequestMethod.GET)
	@ResponseBody
    public List<TascaSegonPla> getTasquesSegonPlaPendents() {
        List<Object[]> tasques = workflowEngineApi.getTasquesSegonPlaPendents();
        List<TascaSegonPla> tasquesSegonPla = new ArrayList<TascaSegonPla>();
        if (tasques != null) {
            for (Object[] t: tasques) {
                tasquesSegonPla.add(TascaSegonPla.builder()
                        .taskInstanceId((Long) t[0])
                        .dataMarcaFinalitzar((Date) t[1])
                        .dataIniciFinalitzacio((Date) t[2])
                        .errorFinalitzacio((String) t[3]).build());
            }
        }
        return tasquesSegonPla;
    }

    @RequestMapping(value="/processDefinitions/byEntorn/{entornId}/noUtilitzades", method = RequestMethod.GET)
	@ResponseBody
    public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(
            @PathVariable("entornId") Long entornId) {
        return workflowEngineApi.findDefinicionsProcesIdNoUtilitzadesByEntorn(entornId);
    }

    @RequestMapping(value="/processDefinitions/byExpedientTipus/{expedientTipusId}/noUtilitzades", method = RequestMethod.GET)
	@ResponseBody
    public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(
            @PathVariable("expedientTipusId") Long expedientTipusId) {
        return workflowEngineApi.findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(expedientTipusId);
    }

    @RequestMapping(value="/processDefinitions/{processDefinitionId}/expedients/byExpedientTipus/{expedientTipusId}/noUtilitzades", method = RequestMethod.GET)
	@ResponseBody
    public List<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
            @PathVariable("expedientTipusId") Long expedientTipusId,
            @PathVariable("processDefinitionId") Long processDefinitionId) {
        return workflowEngineApi.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(expedientTipusId, processDefinitionId);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/retrocedirAccio", method = RequestMethod.GET)
	@ResponseBody
    public void retrocedirAccio(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody RetrocedirAccio retrocedirAccio) {
        workflowEngineApi.retrocedirAccio(
                processInstanceId,
                retrocedirAccio.getActionName(),
                retrocedirAccio.getParams(),
                retrocedirAccio.getProcessDefinitionPareId());
    }

    @RequestMapping(value="/taskInstances/{taskInstanceId}/actor", method = RequestMethod.POST)
	@ResponseBody
    public void setTaskInstanceActorId(
            @PathVariable("taskInstanceId") String taskInstanceId,
            @RequestBody String actorId) {
        workflowEngineApi.setTaskInstanceActorId(
                taskInstanceId,
                actorId);
    }

    @RequestMapping(value="/taskInstances/{taskInstanceId}/pooledActors", method = RequestMethod.POST)
	@ResponseBody
    public void setTaskInstancePooledActors(
            @PathVariable("taskInstanceId") String taskInstanceId,
            @RequestBody String[] pooledActors) {
        workflowEngineApi.setTaskInstancePooledActors(taskInstanceId, pooledActors);
    }

    @RequestMapping(value="/processDefinitions/parse", method = RequestMethod.GET)
	@ResponseBody
    public WProcessDefinition parse(
            @RequestPart("zipFile") MultipartFile zipFile) throws Exception {
        return workflowEngineApi.parse(new ZipInputStream(zipFile.getInputStream()));
    }



    private List<VariableRest> objectMapToVariableRestConvert(Map<String, Object> variables) {
        List<VariableRest> variablesRest = new ArrayList<VariableRest>();
        if (variables != null) {
            for (Map.Entry<String, Object> variable: variables.entrySet()) {
                variablesRest.add(new VariableRest(variable.getKey(), variable.getValue()));
            }
        }
        return variablesRest;
    }

    private Map<String, Object> variableRestToObjectMaConvert(List<VariableRest> variables) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (variables != null) {
            for(VariableRest variable: variables) {
                if (variable.getValor() != null) {
                    map.put(variable.getNom(), variable.getValor());
                }
            }
        }
        return map;
    }

    @Data
    public static class ProcessStart {
        private String actorId;
        private String processDefinitionId;
        private List<VariableRest> variables;
    }

    @Data
    public static class ProcessEnd {
        String[] processInstanceIds;
        private Date dataFinalitzacio;
    }

    @Data
    public static class MarcarFinalitzar {
        private Date marcadaFinalitzar;
        private String outcome;
        private String rols;
    }
    @Data @Builder
    public static class TascaSegonPla {
        private Long taskInstanceId;
        private Date dataMarcaFinalitzar;
        private Date dataIniciFinalitzacio;
        private String errorFinalitzacio;
    }

    @Data
    public static class ReassignTask {
        private String expression;
        private Long entornId;
    }

    @Data
    public static class InfoCache {
        private String titol;
        private String info;
    }

    @Data
    public static class RetrocedirAccio {
        private String actionName;
        private List<String> params;
        private String processDefinitionPareId;
    }

    @Data
    public static class UpdateVars {
        private boolean deleteFirst;
        private List<VariableRest> variables;
    }

    @Data
    public static class VariableRest {
        private String nom;
        private TipusVar tipus;
        private String valor;

        public Object convertirValor() {
            try {
                if (tipus != null) {
                    switch (tipus) {
                        case INTEGER:
                        case LONG:
                            return Long.parseLong(valor);
                        case FLOAT:
                            return Double.parseDouble(valor);
                        case BOOLEAN:
                            return Boolean.valueOf(valor);
                        case DATE:
                            String[] dataSplit = valor.split("/");
                            Calendar data = new GregorianCalendar();
                            data.set(Integer.parseInt(dataSplit[2]),Integer.parseInt(dataSplit[1]) - 1,Integer.parseInt(dataSplit[0]));
                            return data.getTime();
                        case PREU:
                            return BigDecimal.valueOf(Double.parseDouble(valor));
                        case TERMINI:
                            return Termini.valueFromString(valor);
                        default:
                            return valor;
                    }
                } else {
                    return valor;
                }
            } catch (Exception ex) {
                log.error("Error al convertir [valor: " + valor + ", tipus: " + tipus + "]");
                return valor;
            }
        }

        public VariableRest(String nom, Object valor) {
            this.nom = nom;
            this.tipus = extractTipus(valor);
            this.valor = convertir(valor, tipus);
        }

        private TipusVar extractTipus(Object valor) {
            if (valor instanceof Integer)
                return TipusVar.INTEGER;
            if (valor instanceof Long)
                return TipusVar.LONG;
            if (valor instanceof Date)
                return TipusVar.DATE;
            if (valor instanceof Float || valor instanceof Double)
                return TipusVar.FLOAT;
            if (valor instanceof BigDecimal)
                return TipusVar.PREU;
            if (valor instanceof Termini)
                return TipusVar.TERMINI;
            if (valor instanceof Boolean)
                return TipusVar.BOOLEAN;
            if (valor instanceof Registre)
                return TipusVar.REGISTRE;
            return TipusVar.STRING;
        }

        private String convertir(Object valor, TipusVar tipus) {
            try {
                if (tipus != null) {
                    switch (tipus) {
                        case INTEGER:
                        case LONG:
                        case FLOAT:
                        case BOOLEAN:
                            return String.valueOf(valor);
                        case DATE:
                            return DATE_FORMAT.format((Date)valor);
                        case TERMINI:
                            return Termini.valueFromTermini((Termini)valor);
                        case REGISTRE:

                        // TODO: Variables tipus REGISTRE!!!
                        case PREU:
                        default:
                            return valor.toString();
                    }
                } else {
                    return valor.toString();
                }
            } catch (Exception ex) {
                log.error("Error al convertir [objecte: " + valor.toString() + ", tipus: " + tipus + "]");
                return valor.toString();
            }
        }
    }

}
