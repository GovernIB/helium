package es.caib.helium.logic.service;

import es.caib.helium.client.dada.DadaClient;
import es.caib.helium.client.engine.action.ActionClient;
import es.caib.helium.client.engine.areaCarrec.AreaCarrecClient;
import es.caib.helium.client.engine.deployment.DeploymentClient;
import es.caib.helium.client.engine.execution.ExecutionClient;
import es.caib.helium.client.engine.helper.VariableHelper;
import es.caib.helium.client.engine.model.*;
import es.caib.helium.client.engine.processDefiniton.ProcessDefinitionClient;
import es.caib.helium.client.engine.processInstance.ProcessInstanceClient;
import es.caib.helium.client.engine.task.TaskClient;
import es.caib.helium.client.engine.taskVariable.TaskVariableClient;
import es.caib.helium.client.engine.timer.TimerClient;
import es.caib.helium.client.expedient.expedient.ExpedientClientService;
import es.caib.helium.client.expedient.tasca.TascaClientService;
import es.caib.helium.client.model.CustomMultipartFile;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.LlistatIds;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.helium.logic.intf.dto.ResultatConsultaPaginada;
import es.caib.helium.logic.intf.exception.DeploymentException;
import es.caib.helium.logic.util.EntornActual;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkflowEngineApiImpl implements WorkflowEngineApi {

    private final DeploymentClient deploymentClient;
    private final ProcessDefinitionClient processDefinitionClient;
    private final ProcessInstanceClient processInstanceClient;
    private final DadaClient dadaClient;
    private final TaskClient taskClient;
    private final TaskVariableClient taskVariableClient;
    private final ExecutionClient executionClient;
    private final ActionClient actionClient;
    private final TimerClient timerClient;
    private final AreaCarrecClient areaCarrecClient;

    private final ExpedientClientService expedientClientService;
    private final TascaClientService tascaClientService;


    @Override
    public WDeployment desplegar(String nomArxiu, byte[] contingut) {
        try {
            return deploymentClient.createDeployment(
                    removeExtension(nomArxiu),
                    EntornActual.getEntornId().toString(),
                    new CustomMultipartFile(contingut, nomArxiu));
        } catch (Exception ex) {
            log.error("Error al realitzar el desplegament de l'arxiu " + nomArxiu, ex);
            throw new DeploymentException("Error al realitzar el desplegament", ex);
        }
    }

    @Override
    public WDeployment getDesplegament(String deploymentId) {
        return deploymentClient.getDesplegament(deploymentId);
    }

    @Override
    public void esborrarDesplegament(String deploymentId) {
        deploymentClient.esborrarDesplegament(deploymentId);
    }

    @Override
    public Set<String> getResourceNames(String deploymentId) {
        return deploymentClient.getResourceNames(deploymentId);
    }

    @Override
    public byte[] getResourceBytes(String deploymentId, String resourceName) {
        return deploymentClient.getResource(deploymentId, resourceName);
    }

    @Override
    public void updateDeploymentActions(
            String deploymentId,
            Map<String, byte[]> handlers,
            String deploymentFileName,
            byte[] deploymentFileContent) {

        List<MultipartFile> handlerFiles = new ArrayList<>();
        handlers.forEach((key, value) -> handlerFiles.add(new CustomMultipartFile(value, key)));
        deploymentClient.updateDeploymentActions(
                deploymentId,
                handlerFiles,
                new CustomMultipartFile(deploymentFileContent, deploymentFileName));
    }

    @Override
    public WProcessDefinition getProcessDefinition(String deploymentId, String processDefinitionId) {
        return processDefinitionClient.getProcessDefinition(deploymentId, processDefinitionId);
    }

    @Override
    public List<WProcessDefinition> getSubProcessDefinitions(String deploymentId, String processDefinitionId) {
        return processDefinitionClient.getSubProcessDefinitions(deploymentId, processDefinitionId);
    }

    @Override
    public List<String> getTaskNamesFromDeployedProcessDefinition(WDeployment dpd, String processDefinitionId) {
        return processDefinitionClient.getTaskNamesFromDeployedProcessDefinition(dpd.getId(), processDefinitionId);
    }

    @Override
    public String getStartTaskName(String processDefinitionId) {
        return processDefinitionClient.getStartTaskName(processDefinitionId);
    }

    @Override
    public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
        return processDefinitionClient.findProcessDefinitionWithProcessInstanceId(processInstanceId);
    }

    @Override
    public void updateSubprocessDefinition(WProcessDefinition pd1, WProcessDefinition pd2) {
        processDefinitionClient.updateSubprocessDefinition(pd1.getId(), pd2.getId());
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
        return processInstanceClient.findProcessInstancesWithProcessDefinitionId(processDefinitionId);
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName, Long entornId) {
        return processInstanceClient.findProcessInstancesWithProcessDefinitionNameAndEntorn(processName, entornId.toString());
    }

    @Override
    public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) {
        return processInstanceClient.getProcessInstanceTree(rootProcessInstanceId);
    }

    @Override
    public WProcessInstance getProcessInstance(String processInstanceId) {
        return processInstanceClient.getProcessInstance(processInstanceId);
    }

    @Override
    public WProcessInstance getRootProcessInstance(String processInstanceId) {
        // TODO: Es podria consultar l'identificador del processInstance Pare al MS de dades
        return processInstanceClient.getRootProcessInstance(processInstanceId);
    }

    // TODO: Podem obtenir les arrels de les instàncies de procés del MS de dades?
    @Override
    public List<String> findRootProcessInstances(
            String actorId,
            List<String> processInstanceIds,
            boolean nomesMeves,
            boolean nomesTasquesPersonals,
            boolean nomesTasquesGrup) {

        return null;
    }

    @Override
    public Long findExpedientIdByProcessInstanceId(String processInstanceId) {
        return dadaClient.getDadaExpedientIdByProcesId(processInstanceId);
    }

    @Override
    public WProcessInstance startProcessInstanceById(String actorId, String processDefinitionId, Map<String, Object> variables) {
        return processInstanceClient.startProcessInstanceById(
                ProcessStartData.builder()
                        .actorId(actorId)
                        .processDefinitionId(processDefinitionId)
                        .variables(VariableHelper.objectMapToVariableRestConvert(variables))
                        .build());
    }

    @Override
    public void signalProcessInstance(String processInstanceId, String transitionName) {
        processInstanceClient.signalProcessInstance(processInstanceId, transitionName);
    }

    @Override
    public void deleteProcessInstance(String processInstanceId) {
        processInstanceClient.deleteProcessInstance(processInstanceId);
    }

    @Override
    public void suspendProcessInstances(String[] processInstanceIds) {
        processInstanceClient.suspendProcessInstances(processInstanceIds);
    }

    @Override
    public void resumeProcessInstances(String[] processInstanceIds) {
        processInstanceClient.resumeProcessInstances(processInstanceIds);
    }

    @Override
    public void changeProcessInstanceVersion(String processInstanceId, int newVersion) {
        processInstanceClient.changeProcessInstanceVersion(processInstanceId, newVersion);
    }

    // TODO: Per consultar dades no necessitam l'expedientId
    @Override
    public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
//        return dadaClient.getDadesByProces(expedientId, processInstanceId);
        return null;
    }

    @Override
    public Object getProcessInstanceVariable(String processInstanceId, String varName) {
        return dadaClient.getDadaByProcesAndCodi(processInstanceId, varName);
    }

    // TODO: Si no volem passar l'expedientId, ham de tenir aquesta informació al MS de Dades,
    //      i s'ha de passar quan es crea una nova instància de procés
    @Override
    public void setProcessInstanceVariable(/*Long expedientId,*/ String processInstanceId, String varName, Object value) {
//        dadaClient.postDadesBy;
    }

    // TODO: Per consultar dades no necessitam l'expedientId
    @Override
    public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
//        dadaClient.deleteDadaByExpedientIdAndProcesIdAndCodi(expedientId, processInstanceId, varName);
    }

    @Override
    public WTaskInstance getTaskById(String taskId) {
        return taskClient.getTaskById(taskId);
    }

    @Override
    public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId) {
        return taskClient.findTaskInstancesByProcessInstanceId(processInstanceId);
    }

    @Override
    public String getTaskInstanceIdByExecutionTokenId(String executionTokenId) {
        return taskClient.getTaskInstanceIdByExecutionTokenId(executionTokenId);
    }

    // TODO: MS Expedients i tasques
    @Override
    public ResultatConsultaPaginada<WTaskInstance> tascaFindByFiltrePaginat(
            Long entornId,
            String actorId,
            String taskName,
            String titol,
            Long expedientId,
            String expedientTitol,
            String expedientNumero,
            Long expedientTipusId,
            Date dataCreacioInici,
            Date dataCreacioFi,
            Integer prioritat,  // TODO: Prioritat?
            Date dataLimitInici,
            Date dataLimitFi,
            boolean mostrarAssignadesUsuari,
            boolean mostrarAssignadesGrup,
            boolean nomesPendents,
            PaginacioParamsDto paginacioParams,
            boolean nomesCount) {
        Pageable pageable = getPageable(paginacioParams);

        // TODO: Rebem TascaDto, i hem de retornar WTaskInstance !!!!!!!!!!!!!!!!!!!!!
        //  Canviar el nom del TascaDto, i fer que implementi WTaskInstance
        var tasques = tascaClientService.findTasquesAmbFiltrePaginatV1(
                entornId,
                expedientTipusId,
                actorId,
                taskName,
                titol,
                expedientId,
                expedientTitol,
                expedientNumero,
                dataCreacioInici,
                dataCreacioFi,
                dataLimitInici,
                dataLimitFi,
                mostrarAssignadesUsuari,
                mostrarAssignadesGrup,
                nomesPendents,
                null,
                pageable,
                pageable.getSort()
        );
        if (tasques == null || tasques.isEmpty())
            return null;
//        return new ResultatConsultaPaginada<WTaskInstance>(
//                tasques.getTotalElements(),
//                tasques.getContent().);
        return null;
    }

    // TODO: Mirar d'on s'utilitza, per a modificar el mètode, que fa dues crides a MS Exp i Tasques
    @Override
    public LlistatIds tascaIdFindByFiltrePaginat(
            String responsable,
            String tasca,
            String tascaSel,
            List<Long> idsPIExpedients,
            Date dataCreacioInici,
            Date dataCreacioFi,
            Integer prioritat,  // TODO: Prioritat?
            Date dataLimitInici,
            Date dataLimitFi,
            PaginacioParamsDto paginacioParams,
            boolean nomesTasquesPersonals,
            boolean nomesTasquesGrup,
            boolean nomesAmbPendents) {

        String filtre = null; // TODO: tascaSel, idsPIExpedients
        var tasques = tascaClientService.findTasquesAmbFiltrePaginatV1(
                EntornActual.getEntornId(),
                null,
                responsable,
                tasca,
                null,
                null,
                null,
                null,
                dataCreacioInici,
                dataCreacioFi,
                dataLimitInici,
                dataLimitFi,
                nomesTasquesPersonals,
                nomesTasquesGrup,
                nomesAmbPendents,
                filtre,
                Pageable.unpaged(),
                Sort.unsorted()
        );
        if (tasques == null || tasques.isEmpty())
            return null;
        return LlistatIds.builder()
                .count((int) tasques.getTotalElements())
                .ids(tasques.getContent().stream().map(t -> t.getId()).collect(Collectors.toList()))
                .build();
    }

    @Override
    public void takeTaskInstance(String taskId, String actorId) {
        taskClient.takeTaskInstance(taskId, actorId);
    }

    @Override
    public void releaseTaskInstance(String taskId) {
        taskClient.releaseTaskInstance(taskId);
    }

    @Override
    public WTaskInstance startTaskInstance(String taskId) {
        return taskClient.startTaskInstance(taskId);
    }

    @Override
    public void endTaskInstance(String taskId, String outcome) {
        taskClient.endTaskInstance(taskId, outcome);
    }

    @Override
    public WTaskInstance cancelTaskInstance(String taskId) {
        return taskClient.cancelTaskInstance(taskId);
    }

    @Override
    public WTaskInstance suspendTaskInstance(String taskId) {
        return taskClient.startTaskInstance(taskId);
    }

    @Override
    public WTaskInstance resumeTaskInstance(String taskId) {
        return taskClient.resumeTaskInstance(taskId);
    }

    @Override
    public WTaskInstance reassignTaskInstance(String taskId, String expression, Long entornId) {
        return taskClient.reassignTaskInstance(
                taskId,
                ReassignTaskData.builder()
                        .entornId(entornId)
                        .expression(expression)
                        .build());
    }

    // Reassignar tasques
    @Override
    public void setTaskInstanceActorId(String taskInstanceId, String actorId) {
        taskClient.setTaskInstanceActorId(taskInstanceId, actorId);
    }

    @Override
    public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) {
        taskClient.setTaskInstancePooledActors(taskInstanceId, pooledActors);
    }

    @Override
    public void updateTaskInstanceInfoCache(String taskId, String titol, String infoCache) {
        taskClient.updateTaskInstanceInfoCache(
                taskId,
                InfoCacheData.builder()
                        .titol(titol)
                        .info(infoCache)
                        .build());
    }

    // Sequence Flow
    @Override
    public List<String> findStartTaskOutcomes(String definicioProces, String taskName) {
        return taskClient.findStartTaskOutcomes(definicioProces, taskName);
    }

    @Override
    public List<String> findTaskInstanceOutcomes(String taskInstanceId) {
        return taskClient.findTaskInstanceOutcomes(taskInstanceId);
    }

    // Variables
    @Override
    public Map<String, Object> getTaskInstanceVariables(String taskId) {
        return VariableHelper.variableRestToObjectMapConvert(
                taskVariableClient.getTaskInstanceVariables(taskId));
    }

    @Override
    public Object getTaskInstanceVariable(String taskId, String varName) {
        return VariableHelper.variableToObject(
                taskVariableClient.getTaskInstanceVariable(taskId, varName));
    }

    @Override
    public void setTaskInstanceVariable(String taskId, String varName, Object valor) {
        taskVariableClient.setTaskInstanceVariable(
                taskId,
                varName,
                VariableHelper.objectToVariable(varName, valor));
    }

    @Override
    public void setTaskInstanceVariables(String taskId, Map<String, Object> variables, boolean deleteFirst) {
        taskVariableClient.setTaskInstanceVariables(
                taskId,
                UpdateVariablesData.builder()
                        .deleteFirst(deleteFirst)
                        .variables(VariableHelper.objectMapToVariableRestConvert(variables))
                        .build()
        );
    }

    @Override
    public void deleteTaskInstanceVariable(String taskId, String varName) {
        taskVariableClient.deleteTaskInstanceVariable(taskId, varName);
    }

    @Override
    public WToken getTokenById(String tokenId) {
        return executionClient.getTokenById(tokenId);
    }

    @Override
    public Map<String, WToken> getActiveTokens(String processInstanceId) {
        return executionClient.getActiveTokens(processInstanceId);
    }

    @Override
    public Map<String, WToken> getAllTokens(String processInstanceId) {
        return executionClient.getAllTokens(processInstanceId);
    }

    @Override
    public void tokenRedirect(String tokenId, String nodeName, boolean cancelTasks, boolean enterNodeIfTask, boolean executeNode) {
        executionClient.tokenRedirect(
                tokenId,
                RedirectTokenData.builder()
                        .nodeName(nodeName)
                        .cancelTasks(cancelTasks)
                        .enterNodeIfTask(enterNodeIfTask)
                        .executeNode(executeNode)
                        .build());
    }

    @Override
    public boolean tokenActivar(String tokenId, boolean activar) {
        return executionClient.tokenActivar(tokenId, activar);
    }

    @Override
    public void signalToken(String tokenId, String transitionName) {
        executionClient.signalToken(tokenId, transitionName);
    }

    @Override
    public Map<String, Object> evaluateScript(String processInstanceId, String script, Set<String> outputNames) {
        return VariableHelper.variableRestToObjectMapConvert(
                actionClient.evaluateScript(
                        processInstanceId,
                        ScriptData.builder()
                                .script(script)
                                .outputNames(outputNames)
                                .build()));
    }

    @Override
    public Object evaluateExpression(String taskInstanceInstanceId, String processInstanceId, String expression, Map<String, Object> valors) {
        return actionClient.evaluateExpression(
                processInstanceId,
                ExpressionData.builder()
                        .taskInstanceInstanceId(taskInstanceInstanceId)
                        .expression(expression)
                        .expressionLanguage("javascript")
                        .valors(VariableHelper.objectMapToVariableRestConvert(valors))
                        .build());
    }

//    @Override
//    public Object evaluateExpression(String expression, String expectedClassName, Map<String, Object> context) {
//        return actionClient.evaluateExpression(
//                ExpressionData.builder()
//                        .expression(expression)
//                        .expressionLanguage("javascript")
//                        .expectedClass(expectedClassName)
//                        .valors(VariableHelper.objectMapToVariableRestConvert(context))
//                        .build());
//    }

    @Override
    public List<String> listActions(String processDefinition) {
        return actionClient.listActions(processDefinition);
    }

    @Override
    public void executeActionInstanciaProces(String processInstanceId, String actionName, String processDefinitionPareId) {
        actionClient.executeActionInstanciaProces(
                processInstanceId,
                actionName,
                processDefinitionPareId);
    }

    @Override
    public void executeActionInstanciaTasca(String taskInstanceId, String actionName, String processDefinitionPareId) {
        actionClient.executeActionInstanciaTasca(
                taskInstanceId,
                actionName,
                processDefinitionPareId);
    }

    @Override
    public void suspendTimer(String timerId, Date dueDate) {
        timerClient.suspendTimer(timerId, dueDate);
    }

    @Override
    public void resumeTimer(String timerId, Date dueDate) {
        timerClient.resumeTimer(timerId, dueDate);
    }

    @Override
    public List<String> findAreesByFiltre(String filtre) {
        return areaCarrecClient.findAreesByFiltre(filtre);
    }

    @Override
    public List<String> findAreesByPersona(String personaCodi) {
        return areaCarrecClient.findAreesByPersona(personaCodi);
    }

    @Override
    public List<String> findRolsByPersona(String persona) {
        return areaCarrecClient.findRolsByPersona(persona);
    }

    @Override
    public List<String[]> findCarrecsByFiltre(String filtre) {
        return areaCarrecClient.findCarrecsByFiltre(filtre);
    }

    @Override
    public List<String> findPersonesByGrupAndCarrec(String areaCodi, String carrecCodi) {
        return areaCarrecClient.findPersonesByGrupAndCarrec(areaCodi, carrecCodi);
    }

    @Override
    public List<String> findCarrecsByPersonaAndGrup(String codiPersona, String codiArea) {
        return areaCarrecClient.findCarrecsByPersonaAndGrup(codiPersona, codiArea);
    }

    @Override
    public List<String> findPersonesByCarrec(String codi) {
        return areaCarrecClient.findPersonesByCarrec(codi);
    }

    @Override
    public List<String> findPersonesByGrup(String rol) {
        return areaCarrecClient.findPersonesByGrup(rol);
    }





    // TODO: Retrocedir tokens
    @Override
    public List<String> findArrivingNodeNames(String tokenId) {
        return null;
    }




    // TODO: Mirar on es crida, i substituir per el MS corresponent
    //      No quadra el mètode utilitzat
    @Override
    public ResultatConsultaPaginada<Long> expedientFindByFiltre(
            Long entornId,
            String actorId,
            Collection<Long> tipusIdPermesos,
            String titol,
            String numero,
            Long tipusId,
            Date dataCreacioInici,
            Date dataCreacioFi,
            Date dataFiInici,
            Date dataFiFi,
            Long estatId,
            Double geoPosX,
            Double geoPosY,
            String geoReferencia,
            boolean nomesIniciats,
            boolean nomesFinalitzats,
            boolean mostrarAnulats,
            boolean mostrarNomesAnulats,
            boolean nomesAlertes,
            boolean nomesErrors,
            boolean nomesTasquesPersonals,
            boolean nomesTasquesGrup,
            boolean nomesTasquesMeves,
            PaginacioParamsDto paginacioParams,
            boolean nomesCount) {
        Pageable pageable = getPageable(paginacioParams);
        var expedients = expedientClientService.findExpedientsAmbFiltrePaginatV1(
                entornId,
                null,
                actorId,
                tipusId,
                titol,
                numero,
                dataCreacioInici,
                dataCreacioFi,
                dataFiInici,
                dataFiFi,
                null,
                estatId,
                nomesTasquesPersonals,
                nomesTasquesGrup,
                nomesAlertes,
                nomesErrors,
                null,
                pageable,
                pageable.getSort());

        if (expedients == null || expedients.isEmpty())
            return null;
        return new ResultatConsultaPaginada(
                expedients.getTotalElements(),
                expedients.getContent().stream().map(e -> e.getId()).collect(Collectors.toList()));
    }


    // Finalitzar expedients
    @Override
    public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio) {

    }

    @Override
    public void desfinalitzarExpedient(String processInstanceId) {

    }

    // Tasques en segon pla
    @Override
    public void marcarFinalitzar(String taskId, Date marcadaFinalitzar, String outcome, String rols) {

    }

    @Override
    public void marcarIniciFinalitzacioSegonPla(String taskId, Date iniciFinalitzacio) {

    }

    @Override
    public void guardarErrorFinalitzacio(String taskId, String errorFinalitzacio) {

    }

    @Override
    public List<Object[]> getTasquesSegonPlaPendents() {
        return null;
    }


    // Eliminació de Definicions de Procés
    @Override
    public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(Long entornId) {
        return null;
    }

    @Override
    public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(Long expedientTipusId) {
        return null;
    }

    @Override
    public List<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(Long expedientTipusId, Long processDefinitionId) {
        return null;
    }


    // Retroacció
    @Override
    public void retrocedirAccio(String processInstanceId, String actionName, List<String> params, String processDefinitionPareId) {

    }


    @Override
    public WProcessDefinition parse(ZipInputStream zipInputStream) throws Exception {
        return null;
    }



    private Pageable getPageable(PaginacioParamsDto paginacioParams) {
        Pageable pageable;

        if (paginacioParams.getOrdres() == null || paginacioParams.getOrdres().isEmpty()) {
            pageable =  PageRequest.of(
                    paginacioParams.getPaginaNum(),
                    paginacioParams.getPaginaTamany());
        } else {
            pageable =  PageRequest.of(
                    paginacioParams.getPaginaNum(),
                    paginacioParams.getPaginaTamany(),
                    Sort.by(
                            paginacioParams.getOrdres().stream()
                                    .map(o -> new Sort.Order(
                                            OrdreDireccioDto.ASCENDENT.equals(o.getDireccio()) ? Direction.ASC : Direction.DESC,
                                            o.getCamp()))
                                    .collect(Collectors.toList())));
        }
        return pageable;
    }

    private String removeExtension(String fileName) {
        int dotPos = fileName.lastIndexOf(".");
        if (dotPos == -1)
            return fileName;
        return fileName.substring(0, dotPos);
    }
}
