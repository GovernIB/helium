package net.conselldemallorca.helium.v3.core.service;

import net.conselldemallorca.helium.core.api.LlistatIds;
import net.conselldemallorca.helium.core.api.ResultatConsultaPaginada;
import net.conselldemallorca.helium.core.api.WDeployment;
import net.conselldemallorca.helium.core.api.WProcessDefinition;
import net.conselldemallorca.helium.core.api.WProcessInstance;
import net.conselldemallorca.helium.core.api.WTaskInstance;
import net.conselldemallorca.helium.core.api.WToken;
import net.conselldemallorca.helium.core.api.WorkflowEngineApi;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

@Service
public class WorkflowEngineApiImpl implements WorkflowEngineApi {

    @Override
    public WDeployment desplegar(String nomArxiu, byte[] contingut) {

//        private static void uploadWordDocument(byte[] fileContents, final String filename) {
//            RestTemplate restTemplate = new RestTemplate();
//            String fooResourceUrl = "http://localhost:8080/spring-rest/foos"; // Dummy URL.
//            MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
//
//            map.add("name", filename);
//            map.add("filename", filename);
//
//            // Here we
//            ByteArrayResource contentsAsResource = new ByteArrayResource(fileContents) {
//                @Override
//                public String getFilename() {
//                    return filename; // Filename has to be returned in order to be able to post.
//                }
//            };
//
//            map.add("file", contentsAsResource);
//
//            // Now you can send your file along.
//            String result = restTemplate.postForObject(fooResourceUrl, map, String.class);
//
//            // Proceed as normal with your results.
//        }

        return null;
    }

    @Override
    public WDeployment getDesplegament(String deploymentId) {
        return null;
    }

    @Override
    public void esborrarDesplegament(String deploymentId) {

    }

    @Override
    public Set<String> getResourceNames(String deploymentId) {
        return null;
    }

    @Override
    public byte[] getResourceBytes(String deploymentId, String resourceName) {
        return new byte[0];
    }

    @Override
    public void updateDeploymentActions(Long deploymentId, Map<String, byte[]> handlers) {

    }

    @Override
    public WProcessDefinition getProcessDefinition(String deploymentId, String processDefinitionId) {
        return null;
    }

    @Override
    public List<WProcessDefinition> getSubProcessDefinitions(String deploymentId, String processDefinitionId) {
        return null;
    }

    @Override
    public List<String> getTaskNamesFromDeployedProcessDefinition(WDeployment dpd, String processDefinitionId) {
        return null;
    }

    @Override
    public String getStartTaskName(String processDefinitionId) {
        return null;
    }

    @Override
    public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
        return null;
    }

    @Override
    public void updateSubprocessDefinition(WProcessDefinition pd1, WProcessDefinition pd2) {

    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
        return null;
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) {
        return null;
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName, Long entornId) {
        return null;
    }

    @Override
    public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) {
        return null;
    }

    @Override
    public WProcessInstance getProcessInstance(String processInstanceId) {
        return null;
    }

    @Override
    public WProcessInstance getRootProcessInstance(String processInstanceId) {
        return null;
    }

    @Override
    public List<String> findRootProcessInstances(String actorId, List<String> processInstanceIds, boolean nomesMeves, boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
        return null;
    }

    @Override
    public WProcessInstance startProcessInstanceById(String actorId, String processDefinitionId, Map<String, Object> variables) {
        return null;
    }

    @Override
    public void signalProcessInstance(String processInstanceId, String transitionName) {

    }

    @Override
    public void deleteProcessInstance(String processInstanceId) {

    }

    @Override
    public void suspendProcessInstances(String[] processInstanceIds) {

    }

    @Override
    public void resumeProcessInstances(String[] processInstanceIds) {

    }

    @Override
    public void changeProcessInstanceVersion(String processInstanceId, int newVersion) {

    }

    @Override
    public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
        return null;
    }

    @Override
    public Object getProcessInstanceVariable(String processInstanceId, String varName) {
        return null;
    }

    @Override
    public void setProcessInstanceVariable(String processInstanceId, String varName, Object value) {

    }

    @Override
    public void deleteProcessInstanceVariable(String processInstanceId, String varName) {

    }

    @Override
    public WTaskInstance getTaskById(String taskId) {
        return null;
    }

    @Override
    public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId) {
        return null;
    }

    @Override
    public Long getTaskInstanceIdByExecutionTokenId(Long executionTokenId) {
        return null;
    }

    @Override
    public ResultatConsultaPaginada<WTaskInstance> tascaFindByFiltrePaginat(Long entornId, String actorId, String taskName, String titol, Long expedientId, String expedientTitol, String expedientNumero, Long expedientTipusId, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, boolean mostrarAssignadesUsuari, boolean mostrarAssignadesGrup, boolean nomesPendents, PaginacioParamsDto paginacioParams, boolean nomesCount) {
        return null;
    }

    @Override
    public LlistatIds tascaIdFindByFiltrePaginat(String responsable, String tasca, String tascaSel, List<Long> idsPIExpedients, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, PaginacioParamsDto paginacioParams, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, boolean nomesAmbPendents) {
        return null;
    }

    @Override
    public void takeTaskInstance(String taskId, String actorId) {

    }

    @Override
    public void releaseTaskInstance(String taskId) {

    }

    @Override
    public WTaskInstance startTaskInstance(String taskId) {
        return null;
    }

    @Override
    public void endTaskInstance(String taskId, String outcome) {

    }

    @Override
    public WTaskInstance cancelTaskInstance(String taskId) {
        return null;
    }

    @Override
    public WTaskInstance suspendTaskInstance(String taskId) {
        return null;
    }

    @Override
    public WTaskInstance resumeTaskInstance(String taskId) {
        return null;
    }

    @Override
    public WTaskInstance reassignTaskInstance(String taskId, String expression, Long entornId) {
        return null;
    }

    @Override
    public void updateTaskInstanceInfoCache(String taskId, String titol, String infoCache) {

    }

    @Override
    public Map<String, Object> getTaskInstanceVariables(String taskId) {
        return null;
    }

    @Override
    public Object getTaskInstanceVariable(String taskId, String varName) {
        return null;
    }

    @Override
    public void setTaskInstanceVariable(String taskId, String varName, Object valor) {

    }

    @Override
    public void setTaskInstanceVariables(String taskId, Map<String, Object> variables, boolean deleteFirst) {

    }

    @Override
    public void deleteTaskInstanceVariable(String taskId, String varName) {

    }

    @Override
    public WToken getTokenById(String tokenId) {
        return null;
    }

    @Override
    public Map<String, WToken> getActiveTokens(String processInstanceId) {
        return null;
    }

    @Override
    public Map<String, WToken> getAllTokens(String processInstanceId) {
        return null;
    }

    @Override
    public void tokenRedirect(long tokenId, String nodeName, boolean cancelTasks, boolean enterNodeIfTask, boolean executeNode) {

    }

    @Override
    public boolean tokenActivar(long tokenId, boolean activar) {
        return false;
    }

    @Override
    public void signalToken(long tokenId, String transitionName) {

    }

    @Override
    public Map<String, Object> evaluateScript(String processInstanceId, String script, Set<String> outputNames) {
        return null;
    }

    @Override
    public Object evaluateExpression(String taskInstanceInstanceId, String processInstanceId, String expression, Map<String, Object> valors) {
        return null;
    }

    @Override
    public List<String> listActions(String jbpmId) {
        return null;
    }

    @Override
    public void executeActionInstanciaProces(String processInstanceId, String actionName, String processDefinitionPareId) {

    }

    @Override
    public void executeActionInstanciaTasca(String taskInstanceId, String actionName, String processDefinitionPareId) {

    }

    @Override
    public void suspendTimer(long timerId, Date dueDate) {

    }

    @Override
    public void resumeTimer(long timerId, Date dueDate) {

    }

    @Override
    public List<String> findStartTaskOutcomes(String jbpmId, String taskName) {
        return null;
    }

    @Override
    public List<String> findTaskInstanceOutcomes(String taskInstanceId) {
        return null;
    }

    @Override
    public List<String> findArrivingNodeNames(String tokenId) {
        return null;
    }

    @Override
    public ExpedientDto expedientFindByProcessInstanceId(String processInstanceId) {
        return null;
    }

    @Override
    public ResultatConsultaPaginada<Long> expedientFindByFiltre(Long entornId, String actorId, Collection<Long> tipusIdPermesos, String titol, String numero, Long tipusId, Date dataCreacioInici, Date dataCreacioFi, Date dataFiInici, Date dataFiFi, Long estatId, Double geoPosX, Double geoPosY, String geoReferencia, boolean nomesIniciats, boolean nomesFinalitzats, boolean mostrarAnulats, boolean mostrarNomesAnulats, boolean nomesAlertes, boolean nomesErrors, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, boolean nomesTasquesMeves, PaginacioParamsDto paginacioParams, boolean nomesCount) {
        return null;
    }

    @Override
    public void desfinalitzarExpedient(String processInstanceId) {

    }

    @Override
    public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio) {

    }

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

    @Override
    public Object evaluateExpression(String expression, Class expectedClass, Map<String, Object> context) {
        return null;
    }

    @Override
    public void retrocedirAccio(String processInstanceId, String actionName, List<String> params, String processDefinitionPareId) {

    }

    @Override
    public void setTaskInstanceActorId(String taskInstanceId, String actorId) {

    }

    @Override
    public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) {

    }

    @Override
    public WProcessDefinition parse(ZipInputStream zipInputStream) throws Exception {
        return null;
    }
}
