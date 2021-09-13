package net.conselldemallorca.helium.back.rest.controller;

import net.conselldemallorca.helium.api.dto.OptionalString;
import net.conselldemallorca.helium.back.rest.model.ExpressionData;
import net.conselldemallorca.helium.back.rest.model.InfoCacheData;
import net.conselldemallorca.helium.back.rest.model.ProcessStartData;
import net.conselldemallorca.helium.back.rest.model.ReassignTaskData;
import net.conselldemallorca.helium.back.rest.model.RedirectTokenData;
import net.conselldemallorca.helium.back.rest.model.ScriptData;
import net.conselldemallorca.helium.back.rest.model.UpdateVariablesData;
import net.conselldemallorca.helium.back.rest.model.VariableRest;
import net.conselldemallorca.helium.api.dto.ExpedientDto;
import net.conselldemallorca.helium.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.api.dto.ResultatConsultaPaginada;
import net.conselldemallorca.helium.api.service.WDeployment;
import net.conselldemallorca.helium.api.service.WProcessDefinition;
import net.conselldemallorca.helium.api.service.WProcessInstance;
import net.conselldemallorca.helium.api.service.WTaskInstance;
import net.conselldemallorca.helium.api.service.WToken;
import net.conselldemallorca.helium.api.service.WorkflowEngineApi;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

import static net.conselldemallorca.helium.back.rest.helper.VariableHelper.objectMapToVariableRestConvert;
import static net.conselldemallorca.helium.back.rest.helper.VariableHelper.objectToVariable;
import static net.conselldemallorca.helium.back.rest.helper.VariableHelper.variableRestToObjectMapConvert;


@Slf4j
@Controller
@RequestMapping("/api/v1")
public class WorkflowEngineController {

    @Autowired
    WorkflowEngineApi workflowEngineApi;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    // DEFINICIÓ DE PROCÉS
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    // Desplegaments
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/desplegaments", method = RequestMethod.POST,
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = { "application/json" })
	@ResponseBody
    public ResponseEntity<WDeployment> desplegar(
            @RequestPart(value = "deploymentName", required = false) String deploymentName,
            @RequestPart(value = "tenantId", required = false) String tenantId,
            @RequestPart("deploymentFile") MultipartFile deploymentFile) throws IOException {
        String fileName = deploymentFile.getOriginalFilename();
        byte[] contingut = deploymentFile.getBytes();
        return new ResponseEntity(
                workflowEngineApi.desplegar(
                        fileName,
                        contingut),
//                        deploymentFile.getName(),
//                        deploymentFile.getBytes()),
                HttpStatus.CREATED);
    }

    @RequestMapping(value="/desplegaments/{deploymentId}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<WDeployment> getDesplegament(@PathVariable("deploymentId") String deploymentId) {
        return new ResponseEntity(workflowEngineApi.getDesplegament(deploymentId), HttpStatus.OK);
    }

    @RequestMapping(value="/desplegaments/{deploymentId}", method = RequestMethod.DELETE)
	@ResponseBody
    public ResponseEntity<Void> esborrarDesplegament(@PathVariable("deploymentId") String deploymentId) {
        workflowEngineApi.esborrarDesplegament(deploymentId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/desplegaments/{deploymentId}/resourceNames", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<Set<String>> getResourceNames(@PathVariable("deploymentId") String deploymentId) {
        return new ResponseEntity(workflowEngineApi.getResourceNames(deploymentId), HttpStatus.OK);
    }

    @RequestMapping(value="/desplegaments/{deploymentId}/resources/{resourceName:.+}",
            method = RequestMethod.GET)
//            produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
	@ResponseBody
    public ResponseEntity<byte[]> getResourceBytes(
            @PathVariable("deploymentId") String deploymentId,
            @PathVariable("resourceName") String resourceName) {
        return new ResponseEntity(workflowEngineApi.getResourceBytes(deploymentId, resourceName), HttpStatus.OK);
    }

    @RequestMapping(value="/desplegaments/{deploymentId}/actions",
            method = RequestMethod.POST,
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	@ResponseBody
    public ResponseEntity<Void> updateDeploymentActions(
            @PathVariable("deploymentId") String deploymentId,
//            @RequestPart("handlers") List<MultipartFile> handlers,
            @RequestPart(value = "deploymentFile", required = false) MultipartFile deploymentFile) throws Exception {

//        Map<String, byte[]> handlerBytes = new HashMap<String, byte[]>();
//        if (handlers != null && !handlers.isEmpty()) {
//            for (MultipartFile handler: handlers) {
//                handlerBytes.put(handler.getName(), handler.getBytes());
//            }
//        }
        workflowEngineApi.updateDeploymentActions(Long.parseLong(deploymentId), deploymentFile.getBytes());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/desplegaments/{deploymentOrigenId}/propagate/actions/{deploymentDestiId}",
            method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Void> propagateDeploymentActions(
            @PathVariable("deploymentOrigenId") String deploymentOrigenId,
            @PathVariable("deploymentDestiId") String deploymentDestiId) {

        workflowEngineApi.propagateDeploymentActions(
                deploymentOrigenId,
                deploymentDestiId);
        return new ResponseEntity(HttpStatus.OK);
    }

    // Definicions de Procés
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/processDefinitions/{processDefinitionId}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<WProcessDefinition> getProcessDefinition(
//            @RequestParam(value = "deploymentId", required = false) String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId) {
        WProcessDefinition wProcessDefinition = workflowEngineApi.getProcessDefinition(processDefinitionId);
        return new ResponseEntity(wProcessDefinition, HttpStatus.OK);
    }

    @RequestMapping(value="/processDefinitions/{processDefinitionId}/subProcessDefinition", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<WProcessDefinition>> getSubProcessDefinitions(
//            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId) {
        return new ResponseEntity(workflowEngineApi.getSubProcessDefinitions(processDefinitionId), HttpStatus.OK);
    }

    @RequestMapping(value="/processDefinitions/{processDefinitionId}/taskNames", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<String>> getTaskNamesFromDeployedProcessDefinition(
            @RequestParam("deploymentId") String deploymentId,
            @PathVariable("processDefinitionId") String processDefinitionId) {
//        WDeployment dpd = workflowEngineApi.getDesplegament(deploymentId);
        return new ResponseEntity(workflowEngineApi.getTaskNamesFromDeployedProcessDefinition(deploymentId, processDefinitionId), HttpStatus.OK);
    }

    @RequestMapping(value="/processDefinitions/{processDefinitionId}/startTaskName", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<String> getStartTaskName(
            @PathVariable("processDefinitionId") String processDefinitionId) {
        return new ResponseEntity(workflowEngineApi.getStartTaskName(processDefinitionId), HttpStatus.OK);
    }

    @RequestMapping(value="/processDefinitions/byProcessInstance/{processInstanceId}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<WProcessDefinition> findProcessDefinitionWithProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity(workflowEngineApi.findProcessDefinitionWithProcessInstanceId(processInstanceId), HttpStatus.OK);
    }

    @RequestMapping(value="/processDefinitions", method = RequestMethod.PUT)
	@ResponseBody
    public ResponseEntity<Void> updateSubprocessDefinition(
            @RequestParam("processDefinitionId1") String processDefinitionId1,
            @RequestParam("processDefinitionId2") String processDefinitionId2) {

        WProcessDefinition pd1 = workflowEngineApi.getProcessDefinition(processDefinitionId1);
        WProcessDefinition pd2 = workflowEngineApi.getProcessDefinition(processDefinitionId2);
        workflowEngineApi.updateSubprocessDefinition(processDefinitionId1, processDefinitionId2);
        return new ResponseEntity(HttpStatus.OK);
    }

    // DEFINICIÓ DE TASQUES
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////


    // INSTÀNCIA DE PROCÉS
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/processInstances/byProcessDefinition/{processDefinitionId}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<WProcessInstance>> findProcessInstancesWithProcessDefinitionId(
            @PathVariable("processDefinitionId") String processDefinitionId) {
        return new ResponseEntity(workflowEngineApi.findProcessInstancesWithProcessDefinitionId(processDefinitionId), HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/byProcessDefinitionName/{processName}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<WProcessInstance>> findProcessInstancesWithProcessDefinitionNameAndEntorn(
            @PathVariable("processName") String processName,
            @RequestParam(value = "entornId", required = false) String entornId) {
        if (entornId != null) {
            return new ResponseEntity(workflowEngineApi.findProcessInstancesWithProcessDefinitionNameAndEntorn(processName, entornId), HttpStatus.OK);
        } else {
            return new ResponseEntity(workflowEngineApi.findProcessInstancesWithProcessDefinitionName(processName), HttpStatus.OK);
        }
    }

    @RequestMapping(value="/processInstances/{rootProcessInstanceId}/tree", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<WProcessInstance>> getProcessInstanceTree(
            @PathVariable("rootProcessInstanceId") String rootProcessInstanceId) {
        return new ResponseEntity(workflowEngineApi.getProcessInstanceTree(rootProcessInstanceId), HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<WProcessInstance> getProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity(workflowEngineApi.getProcessInstance(processInstanceId), HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/root", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<WProcessInstance> getRootProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity(workflowEngineApi.getRootProcessInstance(processInstanceId), HttpStatus.OK);
    }

    // TODO: Tasca utilitzada per a consulta d'expedients
    //       Això ha d'anar al MS d'expedients i tasques o al MS de dades!!
//    @RequestMapping(value="/processInstances/root", method = RequestMethod.GET)
//    @ResponseBody
//    public ResponseEntity<List<String>> findRootProcessInstances(
//            @RequestParam(value = "actorId") String actorId,
//            @RequestParam(value = "processInstanceIds") List<String> processInstanceIds,
//            @RequestParam(value = "nomesMeves") boolean nomesMeves,
//            @RequestParam(value = "nomesTasquesPersonals") boolean nomesTasquesPersonals,
//            @RequestParam(value = "nomesTasquesGrup") boolean nomesTasquesGrup) {
//        return new ResponseEntity(workflowEngineApi.findRootProcessInstances(
//                actorId,
//                processInstanceIds,
//                nomesMeves,
//                nomesTasquesPersonals,
//                nomesTasquesGrup), HttpStatus.OK);
//    }

    @RequestMapping(value="/processInstances/start", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<WProcessInstance> startProcessInstanceById(
            @RequestBody ProcessStartData processStartData) {
        return new ResponseEntity(workflowEngineApi.startProcessInstanceById(
                processStartData.getActorId(),
                processStartData.getProcessDefinitionId(),
                variableRestToObjectMapConvert(processStartData.getVariables())), HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/signal", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> signalProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody OptionalString transitionName) {
        workflowEngineApi.signalProcessInstance(processInstanceId, transitionName != null ? transitionName.getValue() : null);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}", method = RequestMethod.DELETE)
	@ResponseBody
    public ResponseEntity<Void> deleteProcessInstance(
            @PathVariable("processInstanceId") String processInstanceId) {
        workflowEngineApi.deleteProcessInstance(processInstanceId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/suspend", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> suspendProcessInstances(
            @RequestBody String[] processInstanceIds) {
        workflowEngineApi.suspendProcessInstances(processInstanceIds);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/resume", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> resumeProcessInstances(
            @RequestBody String[] processInstanceIds) {
        workflowEngineApi.resumeProcessInstances(processInstanceIds);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/version/", method = RequestMethod.PUT)
	@ResponseBody
    public ResponseEntity<Void> changeProcessInstanceVersion(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody Integer newVersion) {
        workflowEngineApi.changeProcessInstanceVersion(processInstanceId, newVersion);
        return new ResponseEntity(HttpStatus.OK);
    }


    // VARIABLES DE PROCÉS
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    // TODO: Atacar al MS Dades
//    @RequestMapping(value="/processInstances/{processInstanceId}/processInstanceVariables", method = RequestMethod.GET)
//	@ResponseBody
//    public ResponseEntity<List<VariableRest>> getProcessInstanceVariables(
//            @PathVariable("processInstanceId") String processInstanceId) {
//        Map<String, Object> variables = workflowEngineApi.getProcessInstanceVariables(processInstanceId);
//        return new ResponseEntity(objectMapToVariableRestConvert(variables), HttpStatus.OK);
//    }
//
//    @RequestMapping(value="/processInstances/{processInstanceId}/processInstanceVariables/{varName}", method = RequestMethod.GET)
//	@ResponseBody
//    public ResponseEntity<VariableRest> getProcessInstanceVariable(
//            @PathVariable("processInstanceId") String processInstanceId,
//            @PathVariable(value = "varName") String varName) {
//        Object valor = workflowEngineApi.getProcessInstanceVariable(processInstanceId, varName);
//        if (valor != null)
//            return new ResponseEntity(new VariableRest(varName, valor), HttpStatus.OK);
//        return new ResponseEntity(null, HttpStatus.OK);
//    }
//
//    @RequestMapping(value="/processInstances/{processInstanceId}/processInstanceVariables", method = RequestMethod.POST)
//	@ResponseBody
//    public ResponseEntity<Void> setProcessInstanceVariable(
//            @PathVariable("processInstanceId") String processInstanceId,
//            @RequestBody VariableRest variable) {
//        workflowEngineApi.setProcessInstanceVariable(processInstanceId, variable.getNom(), variable.convertirValor());
//        return new ResponseEntity(HttpStatus.OK);
//    }
//
//    @RequestMapping(value="/processInstances/{processInstanceId}/processInstanceVariables/{varName}", method = RequestMethod.DELETE)
//	@ResponseBody
//    public ResponseEntity<Void> deleteProcessInstanceVariable(
//            @PathVariable("processInstanceId") String processInstanceId,
//            @PathVariable("varName") String varName) {
//        workflowEngineApi.deleteProcessInstanceVariable(processInstanceId, varName);
//        return new ResponseEntity(HttpStatus.OK);
//    }


    // INSTÀNCIA DE TASQUES
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/taskInstances/{taskId}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<WTaskInstance> getTaskById(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity(workflowEngineApi.getTaskById(taskId), HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/byProcessInstance/{processInstanceId}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<WTaskInstance>> findTaskInstancesByProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity(workflowEngineApi.findTaskInstancesByProcessInstanceId(processInstanceId), HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/byExecution/{executionTokenId}/id", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<String> getTaskInstanceIdByExecutionTokenId(
            @PathVariable("executionTokenId") String executionTokenId) {
        return new ResponseEntity(workflowEngineApi.getTaskInstanceIdByExecutionTokenId(executionTokenId), HttpStatus.OK);
    }

    // TODO: Consultes a realitzar al MS d'expedients i tasques
//    @RequestMapping(value="/taskInstances/byFiltrePaginat", method = RequestMethod.GET)
//    @ResponseBody
//    public ResponseEntity<ResultatConsultaPaginada<WTaskInstance>> tascaFindByFiltrePaginat(
//            @RequestParam(value = "entornId", required = false) Long entornId,
//            @RequestParam(value = "actorId", required = false) String actorId,
//            @RequestParam(value = "taskName", required = false) String taskName,
//            @RequestParam(value = "titol", required = false) String titol,
//            @RequestParam(value = "expedientId", required = false) Long expedientId,
//            @RequestParam(value = "expedientTitol", required = false) String expedientTitol,
//            @RequestParam(value = "expedientNumero", required = false) String expedientNumero,
//            @RequestParam(value = "expedientTipusId", required = false) Long expedientTipusId,
//            @RequestParam(value = "dataCreacioInici", required = false) Date dataCreacioInici,
//            @RequestParam(value = "dataCreacioFi", required = false) Date dataCreacioFi,
//            @RequestParam(value = "prioritat", required = false) Integer prioritat,
//            @RequestParam(value = "dataLimitInici", required = false) Date dataLimitInici,
//            @RequestParam(value = "dataLimitFi", required = false) Date dataLimitFi,
//            @RequestParam(value = "mostrarAssignadesUsuari", required = false) boolean mostrarAssignadesUsuari,
//            @RequestParam(value = "mostrarAssignadesGrup", required = false) boolean mostrarAssignadesGrup,
//            @RequestParam(value = "nomesPendents", required = false) boolean nomesPendents,
//            @RequestParam(value = "paginacioParams") PaginacioParamsDto paginacioParams,
//            @RequestParam(value = "nomesCount", required = false) boolean nomesCount) {
//        return new ResponseEntity(workflowEngineApi.tascaFindByFiltrePaginat(
//                entornId,
//                actorId,
//                taskName,
//                titol,
//                expedientId,
//                expedientTitol,
//                expedientNumero,
//                expedientTipusId,
//                dataCreacioInici,
//                dataCreacioFi,
//                prioritat,
//                dataLimitInici,
//                dataLimitFi,
//                mostrarAssignadesUsuari,
//                mostrarAssignadesGrup,
//                nomesPendents,
//                paginacioParams,
//                nomesCount), HttpStatus.OK);
//    }
//
//    @RequestMapping(value="/taskInstances/byFiltrePaginat/ids", method = RequestMethod.GET)
//    @ResponseBody
//    public ResponseEntity<LlistatIds> tascaIdFindByFiltrePaginat(
//            @RequestParam(value = "responsable", required = false) String responsable,
//            @RequestParam(value = "tasca", required = false) String tasca,
//            @RequestParam(value = "tascaSel", required = false) String tascaSel,
//            @RequestParam(value = "idsPIExpedients", required = false) List<Long> idsPIExpedients,
//            @RequestParam(value = "dataCreacioInici", required = false) Date dataCreacioInici,
//            @RequestParam(value = "dataCreacioFi", required = false) Date dataCreacioFi,
//            @RequestParam(value = "prioritat", required = false) Integer prioritat,
//            @RequestParam(value = "dataLimitInici", required = false) Date dataLimitInici,
//            @RequestParam(value = "dataLimitFi", required = false) Date dataLimitFi,
//            @RequestParam(value = "paginacioParams") PaginacioParamsDto paginacioParams,
//            @RequestParam(value = "nomesTasquesPersonals", required = false) boolean nomesTasquesPersonals,
//            @RequestParam(value = "nomesTasquesGrup", required = false) boolean nomesTasquesGrup,
//            @RequestParam(value = "nomesAmbPendents", required = false) boolean nomesAmbPendents) {
//        return new ResponseEntity(workflowEngineApi.tascaIdFindByFiltrePaginat(
//                responsable,
//                tasca,
//                tascaSel,
//                idsPIExpedients,
//                dataCreacioInici,
//                dataCreacioFi,
//                prioritat,
//                dataLimitInici,
//                dataLimitFi,
//                paginacioParams,
//                nomesTasquesPersonals,
//                nomesTasquesGrup,
//                nomesAmbPendents), HttpStatus.OK);
//    }

    @RequestMapping(value="/taskInstances/{taskId}/take/{actorId}", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> takeTaskInstance(
            @PathVariable("taskId") String taskId,
            @PathVariable("actorId") String actorId) {
        workflowEngineApi.takeTaskInstance(taskId, actorId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/release", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> releaseTaskInstance(
            @PathVariable("taskId") String taskId) {
        workflowEngineApi.releaseTaskInstance(taskId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/start", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<WTaskInstance> startTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity(workflowEngineApi.startTaskInstance(taskId), HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/end", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> endTaskInstance(
            @PathVariable("taskId") String taskId,
            @RequestParam(value = "outcome", required = false) String outcome) {
        workflowEngineApi.endTaskInstance(taskId, outcome);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/cancel", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<WTaskInstance> cancelTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity(workflowEngineApi.cancelTaskInstance(taskId), HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/suspend", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<WTaskInstance> suspendTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity(workflowEngineApi.suspendTaskInstance(taskId), HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/resume", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<WTaskInstance> resumeTaskInstance(
            @PathVariable("taskId") String taskId) {
        return new ResponseEntity(workflowEngineApi.resumeTaskInstance(taskId), HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/reassign", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<WTaskInstance> reassignTaskInstance(
            @PathVariable("taskId") String taskId,
            @RequestBody ReassignTaskData reassignTask) {
        return new ResponseEntity(workflowEngineApi.reassignTaskInstance(
                taskId,
                reassignTask.getExpression(),
                reassignTask.getEntornId()), HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskInstanceId}/reassign/user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> setTaskInstanceActorId(
            @PathVariable("taskInstanceId") String taskInstanceId,
            @RequestBody String actorId) {
        workflowEngineApi.setTaskInstanceActorId(
                taskInstanceId,
                actorId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskInstanceId}/reassign/group", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> setTaskInstancePooledActors(
            @PathVariable("taskInstanceId") String taskInstanceId,
            @RequestBody String[] pooledActors) {
        workflowEngineApi.setTaskInstancePooledActors(taskInstanceId, pooledActors);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}", method = RequestMethod.PUT)
	@ResponseBody
    public ResponseEntity<Void> updateTaskInstanceInfoCache(
            @PathVariable("taskId") String taskId,
            @RequestBody InfoCacheData info) {
        workflowEngineApi.updateTaskInstanceInfoCache(taskId, info.getTitol(), info.getInfo());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/byProcessDefinition/{processDefinitionId}/tasks/{taskName}/leavingTransitions", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> findStartTaskOutcomes(
            @PathVariable("processDefinitionId") String processDefinitionId,
            @PathVariable("taskName") String taskName) {
        return new ResponseEntity(workflowEngineApi.findStartTaskOutcomes(processDefinitionId, taskName), HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskInstanceId}/leavingTransitions", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> findTaskInstanceOutcomes(
            @PathVariable("taskInstanceId") String taskInstanceId) {
        return new ResponseEntity(workflowEngineApi.findTaskInstanceOutcomes(taskInstanceId), HttpStatus.OK);
    }

    // VARIABLES DE TASQUES
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/taskInstances/{taskId}/taskInstanceVariables", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<VariableRest>> getTaskInstanceVariables(
            @PathVariable("taskId") String taskId) {
        Map<String, Object> variables = workflowEngineApi.getTaskInstanceVariables(taskId);
        return new ResponseEntity(objectMapToVariableRestConvert(variables), HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/taskInstanceVariables/{varName}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<VariableRest> getTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName) {
        return new ResponseEntity(
                objectToVariable(varName, workflowEngineApi.getTaskInstanceVariable(taskId, varName)),
                HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/taskInstanceVariables/{varName}", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> setTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName,
            @RequestBody VariableRest variable) {
        workflowEngineApi.setTaskInstanceVariable(
                taskId,
                varName,
                variable.getValor());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/taskInstanceVariables", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> setTaskInstanceVariables(
            @PathVariable("taskId") String taskId,
            @RequestBody UpdateVariablesData variables) {
        workflowEngineApi.setTaskInstanceVariables(
                taskId,
                variableRestToObjectMapConvert(variables.getVariables()),
                variables.isDeleteFirst());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/taskInstanceVariables/{varName}", method = RequestMethod.DELETE)
	@ResponseBody
    public ResponseEntity<Void> deleteTaskInstanceVariable(
            @PathVariable("taskId") String taskId,
            @PathVariable("varName") String varName) {
        workflowEngineApi.deleteTaskInstanceVariable(taskId, varName);
        return new ResponseEntity(HttpStatus.OK);
    }


    // FILS D'EXECUCIÓ (Token / Execution path)
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/executions/{tokenId}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<WToken> getTokenById(
            @PathVariable("tokenId") String tokenId) {
        return new ResponseEntity(workflowEngineApi.getTokenById(tokenId), HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/executions/active", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<Map<String, WToken>> getActiveTokens(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity(workflowEngineApi.getActiveTokens(processInstanceId), HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/executions", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<Map<String, WToken>> getAllTokens(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity(workflowEngineApi.getAllTokens(processInstanceId), HttpStatus.OK);
    }

    @RequestMapping(value="/executions/{tokenId}", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> tokenRedirect(
            @PathVariable("tokenId") String tokenId,
            @RequestBody RedirectTokenData redirectToken) {
        workflowEngineApi.tokenRedirect(tokenId,
                redirectToken.getNodeName(),
                redirectToken.isCancelTasks(),
                redirectToken.isEnterNodeIfTask(),
                redirectToken.isExecuteNode());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/executions/{tokenId}/activar/{activar}", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Boolean> tokenActivar(
            @PathVariable("tokenId") String tokenId,
            @PathVariable("activar") boolean activar) {
        return new ResponseEntity(workflowEngineApi.tokenActivar(tokenId, activar), HttpStatus.OK);
    }

    @RequestMapping(value="/executions/{tokenId}/signal", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> signalToken(
            @PathVariable("tokenId") String tokenId,
            @RequestBody String transitionName) {
        workflowEngineApi.signalToken(tokenId, transitionName);
        return new ResponseEntity(HttpStatus.OK);
    }


    // ACCIONS
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/processInstances/{processInstanceId}/evaluateScript", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<List<VariableRest>> evaluateScript(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody ScriptData scriptData) {
        return new ResponseEntity(objectMapToVariableRestConvert(
                workflowEngineApi.evaluateScript(
                        processInstanceId,
                        scriptData.getScript(),
                        scriptData.getOutputNames())), HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/evaluateExpression", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Object> evaluateExpression(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody ExpressionData expressionData) {
        return new ResponseEntity(
                workflowEngineApi.evaluateExpression(
                        expressionData.getTaskInstanceInstanceId(),
                        processInstanceId,
                        expressionData.getExpression(),
                        variableRestToObjectMapConvert(expressionData.getValors())),
                HttpStatus.OK);
    }

//    @RequestMapping(value="/evaluateExpression", method = RequestMethod.GET)
//    @ResponseBody
//    public ResponseEntity<Object> evaluateExpression(
//            @RequestBody ExpressionData expressionData) throws ClassNotFoundException {
//        return new ResponseEntity(
//                workflowEngineApi.evaluateExpression(
//                        expressionData.getExpression(),
//                        Class.forName(expressionData.getExpectedClass()),
//                        variableRestToObjectMapConvert(expressionData.getValors())),
//                HttpStatus.OK);
//    }

    // TODO: Fi


    @RequestMapping(value="/processDefinitions/{jbpmId}/actions", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<String>> listActions(
            @PathVariable("jbpmId") String jbpmId) {
        return new ResponseEntity(workflowEngineApi.listActions(jbpmId), HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/actions/{actionName}/execute", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> executeActionInstanciaProces(
            @PathVariable("processInstanceId") String processInstanceId,
            @PathVariable("actionName") String actionName,
            @RequestParam(value = "processDefinitionPareId", required = false) String processDefinitionPareId) {
        workflowEngineApi.executeActionInstanciaProces(
                processInstanceId,
                actionName,
                processDefinitionPareId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskInstanceId}/actions/{actionName}/execute", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> executeActionInstanciaTasca(
            @PathVariable("taskInstanceId") String taskInstanceId,
            @PathVariable("actionName") String actionName,
            @RequestParam(value = "processDefinitionPareId", required = false) String processDefinitionPareId) {
        workflowEngineApi.executeActionInstanciaTasca(
                taskInstanceId,
                actionName,
                processDefinitionPareId);
        return new ResponseEntity(HttpStatus.OK);
    }


    // TIMERS
    ////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/timers/{timerId}/suspend", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> suspendTimer(
            @PathVariable("timerId") String timerId,
            @RequestBody Date dueDate) {
        workflowEngineApi.suspendTimer(timerId, dueDate);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/timers/{timerId}/resume", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> resumeTimer(
            @PathVariable("timerId") String timerId,
            @RequestBody Date dueDate) {
        workflowEngineApi.resumeTimer(timerId, dueDate);
        return new ResponseEntity(HttpStatus.OK);
    }


    // AREES I CARRECS
    ////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "/arees/byFiltre/{filtre}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> findAreesByFiltre(
            @PathVariable("filtre") String filtre) {
        return new ResponseEntity(workflowEngineApi.findAreesByFiltre(filtre), HttpStatus.OK);
    }

    @RequestMapping(value = "/arees/{personaCodi}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> findAreesByPersona(
            @PathVariable("personaCodi") String personaCodi) {
        return new ResponseEntity(
                workflowEngineApi.findAreesByPersona(personaCodi),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/rols/{personaCodi}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> findRolsByPersona(
            @PathVariable("personaCodi") String personaCodi) {
        return new ResponseEntity(
                workflowEngineApi.findRolsByPersona(personaCodi),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/carrecs/byFiltre/{filtre}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String[]>> findCarrecsByFiltre(
            @PathVariable("filtre") String filtre) {
        return new ResponseEntity(
                workflowEngineApi.findCarrecsByFiltre(filtre),
                HttpStatus.OK);
    }
    @RequestMapping(value = "/persones/{grupCodi}/{carrecCodi}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> findPersonesByGrupAndCarrec(
            @PathVariable("grupCodi") String grupCodi,
            @PathVariable("carrecCodi") String carrecCodi) {
        return new ResponseEntity(
                workflowEngineApi.findPersonesByGrupAndCarrec(grupCodi, carrecCodi),
                HttpStatus.OK);
    }
    @RequestMapping(value = "/carrecs/{grupCodi}/{personaCodi}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> findCarrecsByPersonaAndGrup(
            @PathVariable("personaCodi") String personaCodi,
            @PathVariable("grupCodi") String grupCodi) {
        return new ResponseEntity(
                workflowEngineApi.findCarrecsByPersonaAndGrup(personaCodi, grupCodi),
                HttpStatus.OK);
    }
    @RequestMapping(value = "/persones/byCarrec/{carrecCodi}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> findPersonesByCarrec(
            @PathVariable("carrecCodi") String carrecCodi) {
        return new ResponseEntity(
                workflowEngineApi.findPersonesByCarrec(carrecCodi),
                HttpStatus.OK);
    }
    @RequestMapping(value = "/persones/byGrup/{grupCodi}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<String>> findPersonesByGrup(
            @PathVariable("grupCodi") String grupCodi) {
        return new ResponseEntity(
                workflowEngineApi.findPersonesByGrup(grupCodi),
                HttpStatus.OK);
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    @RequestMapping(value="/executions/{tokenId}/arrivingNodes", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<String>> findArrivingNodeNames(
            @PathVariable("tokenId") String tokenId) {
        return new ResponseEntity(workflowEngineApi.findArrivingNodeNames(tokenId), HttpStatus.OK);
    }

    @RequestMapping(value="/expedients/byProcessInstance/{processInstanceId}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<ExpedientDto> expedientFindByProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId) {
        return new ResponseEntity(workflowEngineApi.expedientFindByProcessInstanceId(processInstanceId), HttpStatus.OK);
    }

    @RequestMapping(value="/expedients/ids", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ResultatConsultaPaginada<Long>> expedientFindByFiltre(
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
        return new ResponseEntity(workflowEngineApi.expedientFindByFiltre(
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
                nomesCount), HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/desfinalitzar", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> desfinalitzarExpedient(
            @PathVariable("processInstanceId") String processInstanceId) {
        workflowEngineApi.desfinalitzarExpedient(processInstanceId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/finalitzar", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> finalitzarExpedient(
            @RequestBody ProcessEnd processEnd) {
        workflowEngineApi.finalitzarExpedient(
                processEnd.getProcessInstanceIds(),
                processEnd.getDataFinalitzacio());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/marcarFinalitzar", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> marcarFinalitzar(
            @PathVariable("taskId") String taskId,
            MarcarFinalitzar marcarFinalitzar) {
        workflowEngineApi.marcarFinalitzar(
                taskId,
                marcarFinalitzar.getMarcadaFinalitzar(),
                marcarFinalitzar.getOutcome(),
                marcarFinalitzar.getRols());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/marcarIniciFinalitzarSegonPla", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> marcarIniciFinalitzacioSegonPla(
            @PathVariable("taskId") String taskId,
            @RequestBody Date iniciFinalitzacio) {
        workflowEngineApi.marcarIniciFinalitzacioSegonPla(taskId, iniciFinalitzacio);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/{taskId}/errorFinalitzacio", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Void> guardarErrorFinalitzacio(
            @PathVariable("taskId") String taskId,
            @RequestBody String errorFinalitzacio) {
        workflowEngineApi.guardarErrorFinalitzacio(taskId, errorFinalitzacio);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/taskInstances/bySegonPlaPendents", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<TascaSegonPla>> getTasquesSegonPlaPendents() {
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
        return new ResponseEntity(tasquesSegonPla, HttpStatus.OK);
    }

    @RequestMapping(value="/processDefinitions/byEntorn/{entornId}/noUtilitzades", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<String>> findDefinicionsProcesIdNoUtilitzadesByEntorn(
            @PathVariable("entornId") Long entornId) {
        return new ResponseEntity(workflowEngineApi.findDefinicionsProcesIdNoUtilitzadesByEntorn(entornId), HttpStatus.OK);
    }

    @RequestMapping(value="/processDefinitions/byExpedientTipus/{expedientTipusId}/noUtilitzades", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<String>> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(
            @PathVariable("expedientTipusId") Long expedientTipusId) {
        return new ResponseEntity(workflowEngineApi.findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(expedientTipusId), HttpStatus.OK);
    }

    @RequestMapping(value="/processDefinitions/{processDefinitionId}/expedients/byExpedientTipus/{expedientTipusId}/noUtilitzades", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<List<ExpedientDto>> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(
            @PathVariable("expedientTipusId") Long expedientTipusId,
            @PathVariable("processDefinitionId") Long processDefinitionId) {
        return new ResponseEntity(workflowEngineApi.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(expedientTipusId, processDefinitionId), HttpStatus.OK);
    }

    @RequestMapping(value="/processInstances/{processInstanceId}/retrocedirAccio", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<Void> retrocedirAccio(
            @PathVariable("processInstanceId") String processInstanceId,
            @RequestBody RetrocedirAccio retrocedirAccio) {
        workflowEngineApi.retrocedirAccio(
                processInstanceId,
                retrocedirAccio.getActionName(),
                retrocedirAccio.getParams(),
                retrocedirAccio.getProcessDefinitionPareId());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/processDefinitions/parse", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<WProcessDefinition> parse(
            @RequestPart("zipFile") MultipartFile zipFile) throws Exception {
        return new ResponseEntity(workflowEngineApi.parse(new ZipInputStream(zipFile.getInputStream())), HttpStatus.OK);
    }



//    private List<VariableRest> objectMapToVariableRestConvert(Map<String, Object> variables) {
//        List<VariableRest> variablesRest = new ArrayList<VariableRest>();
//        if (variables != null) {
//            for (Map.Entry<String, Object> variable: variables.entrySet()) {
//                variablesRest.add(new VariableRest(variable.getKey(), variable.getValue()));
//            }
//        }
//        return variablesRest;
//    }
//
//    private Map<String, Object> variableRestToObjectMapConvert(List<VariableRest> variables) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        if (variables != null) {
//            for(VariableRest variable: variables) {
//                if (variable.getValor() != null) {
//                    map.put(variable.getNom(), variable.getValor());
//                }
//            }
//        }
//        return map;
//    }

//    @Data
//    public static class ProcessStart {
//        private String actorId;
//        private String processDefinitionId;
//        private List<VariableRest> variables;
//    }

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
    public static class RetrocedirAccio {
        private String actionName;
        private List<String> params;
        private String processDefinitionPareId;
    }

}
