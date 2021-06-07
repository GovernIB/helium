package es.caib.helium.jbpm3.service.ejb;

import es.caib.helium.api.dto.ExpedientDto;
import es.caib.helium.api.dto.LlistatIds;
import es.caib.helium.api.dto.PaginacioParamsDto;
import es.caib.helium.api.dto.ResultatConsultaPaginada;
import es.caib.helium.api.service.WDeployment;
import es.caib.helium.api.service.WProcessDefinition;
import es.caib.helium.api.service.WProcessInstance;
import es.caib.helium.api.service.WTaskInstance;
import es.caib.helium.api.service.WToken;
import es.caib.helium.api.service.WorkflowEngineApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class WorkflowEngineApiBean implements WorkflowEngineApi {

    @Autowired
    WorkflowEngineApi delegate;

    @Override
    public WDeployment desplegar(String nomArxiu, byte[] contingut) {
        return delegate.desplegar(nomArxiu, contingut);
    }

    @Override
    public WDeployment getDesplegament(String deploymentId) {
        return delegate.getDesplegament(deploymentId);
    }

    @Override
    public void esborrarDesplegament(String deploymentId) {
        delegate.esborrarDesplegament(deploymentId);
    }

    @Override
    public Set<String> getResourceNames(String deploymentId) {
        return delegate.getResourceNames(deploymentId);
    }

    @Override
    public byte[] getResourceBytes(String deploymentId, String resourceName) {
        return delegate.getResourceBytes(deploymentId, resourceName);
    }

    @Override
    public void updateDeploymentActions(Long deploymentId, Map<String, byte[]> handlers) {
        delegate.updateDeploymentActions(deploymentId, handlers);
    }

    @Override
    public WProcessDefinition getProcessDefinition(String deploymentId, String processDefinitionId) {
        return delegate.getProcessDefinition(deploymentId, processDefinitionId);
    }

    @Override
    public List<WProcessDefinition> getSubProcessDefinitions(String deploymentId, String processDefinitionId) {
        return delegate.getSubProcessDefinitions(deploymentId, processDefinitionId);
    }

    @Override
    public List<String> getTaskNamesFromDeployedProcessDefinition(WDeployment dpd, String processDefinitionId) {
        return delegate.getTaskNamesFromDeployedProcessDefinition(dpd, processDefinitionId);
    }

    @Override
    public String getStartTaskName(String processDefinitionId) {
        return delegate.getStartTaskName(processDefinitionId);
    }

    @Override
    public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) {
        return delegate.findProcessDefinitionWithProcessInstanceId(processInstanceId);
    }

    @Override
    public void updateSubprocessDefinition(WProcessDefinition pd1, WProcessDefinition pd2) {
        delegate.updateSubprocessDefinition(pd1, pd2);
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) {
        return delegate.findProcessInstancesWithProcessDefinitionId(processDefinitionId);
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) {
        return delegate.findProcessInstancesWithProcessDefinitionName(processName);
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName, Long entornId) {
        return delegate.findProcessInstancesWithProcessDefinitionNameAndEntorn(processName, entornId);
    }

    @Override
    public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) {
        return delegate.getProcessInstanceTree(rootProcessInstanceId);
    }

    @Override
    public WProcessInstance getProcessInstance(String processInstanceId) {
        return delegate.getProcessInstance(processInstanceId);
    }

    @Override
    public WProcessInstance getRootProcessInstance(String processInstanceId) {
        return delegate.getRootProcessInstance(processInstanceId);
    }

    @Override
    public List<String> findRootProcessInstances(String actorId, List<String> processInstanceIds, boolean nomesMeves, boolean nomesTasquesPersonals, boolean nomesTasquesGrup) {
        return delegate.findRootProcessInstances(actorId, processInstanceIds, nomesMeves, nomesTasquesPersonals, nomesTasquesGrup);
    }

    @Override
    public WProcessInstance startProcessInstanceById(String actorId, String processDefinitionId, Map<String, Object> variables) {
        return delegate.startProcessInstanceById(actorId, processDefinitionId, variables);
    }

    @Override
    public void signalProcessInstance(String processInstanceId, String transitionName) {
        delegate.signalProcessInstance(processInstanceId, transitionName);
    }

    @Override
    public void deleteProcessInstance(String processInstanceId) {
        delegate.deleteProcessInstance(processInstanceId);
    }

    @Override
    public void suspendProcessInstances(String[] processInstanceIds) {
        delegate.suspendProcessInstances(processInstanceIds);
    }

    @Override
    public void resumeProcessInstances(String[] processInstanceIds) {
        delegate.resumeProcessInstances(processInstanceIds);
    }

    @Override
    public void changeProcessInstanceVersion(String processInstanceId, int newVersion) {
        delegate.changeProcessInstanceVersion(processInstanceId, newVersion);
    }

    @Override
    public Map<String, Object> getProcessInstanceVariables(String processInstanceId) {
        return delegate.getProcessInstanceVariables(processInstanceId);
    }

    @Override
    public Object getProcessInstanceVariable(String processInstanceId, String varName) {
        return delegate.getProcessInstanceVariable(processInstanceId, varName);
    }

    @Override
    public void setProcessInstanceVariable(String processInstanceId, String varName, Object value) {
        delegate.setProcessInstanceVariable(processInstanceId, varName, value);
    }

    @Override
    public void deleteProcessInstanceVariable(String processInstanceId, String varName) {
        delegate.deleteProcessInstanceVariable(processInstanceId, varName);
    }

    @Override
    public WTaskInstance getTaskById(String taskId) {
        return delegate.getTaskById(taskId);
    }

    @Override
    public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId) {
        return delegate.findTaskInstancesByProcessInstanceId(processInstanceId);
    }

    @Override
    public Long getTaskInstanceIdByExecutionTokenId(Long executionTokenId) {
        return delegate.getTaskInstanceIdByExecutionTokenId(executionTokenId);
    }

    @Override
    public ResultatConsultaPaginada<WTaskInstance> tascaFindByFiltrePaginat(Long entornId, String actorId, String taskName, String titol, Long expedientId, String expedientTitol, String expedientNumero, Long expedientTipusId, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, boolean mostrarAssignadesUsuari, boolean mostrarAssignadesGrup, boolean nomesPendents, PaginacioParamsDto paginacioParams, boolean nomesCount) {
        return delegate.tascaFindByFiltrePaginat(entornId, actorId, taskName, titol, expedientId, expedientTitol, expedientNumero, expedientTipusId, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, mostrarAssignadesUsuari, mostrarAssignadesGrup, nomesPendents, paginacioParams, nomesCount);
    }

    @Override
    public LlistatIds tascaIdFindByFiltrePaginat(String responsable, String tasca, String tascaSel, List<Long> idsPIExpedients, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, PaginacioParamsDto paginacioParams, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, boolean nomesAmbPendents) {
        return delegate.tascaIdFindByFiltrePaginat(responsable, tasca, tascaSel, idsPIExpedients, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, paginacioParams, nomesTasquesPersonals, nomesTasquesGrup, nomesAmbPendents);
    }

    @Override
    public void takeTaskInstance(String taskId, String actorId) {
        delegate.takeTaskInstance(taskId, actorId);
    }

    @Override
    public void releaseTaskInstance(String taskId) {
        delegate.releaseTaskInstance(taskId);
    }

    @Override
    public WTaskInstance startTaskInstance(String taskId) {
        return delegate.startTaskInstance(taskId);
    }

    @Override
    public void endTaskInstance(String taskId, String outcome) {
        delegate.endTaskInstance(taskId, outcome);
    }

    @Override
    public WTaskInstance cancelTaskInstance(String taskId) {
        return delegate.cancelTaskInstance(taskId);
    }

    @Override
    public WTaskInstance suspendTaskInstance(String taskId) {
        return delegate.suspendTaskInstance(taskId);
    }

    @Override
    public WTaskInstance resumeTaskInstance(String taskId) {
        return delegate.resumeTaskInstance(taskId);
    }

    @Override
    public WTaskInstance reassignTaskInstance(String taskId, String expression, Long entornId) {
        return delegate.reassignTaskInstance(taskId, expression, entornId);
    }

    @Override
    public void updateTaskInstanceInfoCache(String taskId, String titol, String infoCache) {
        delegate.updateTaskInstanceInfoCache(taskId, titol, infoCache);
    }

    @Override
    public Map<String, Object> getTaskInstanceVariables(String taskId) {
        return delegate.getTaskInstanceVariables(taskId);
    }

    @Override
    public Object getTaskInstanceVariable(String taskId, String varName) {
        return delegate.getTaskInstanceVariable(taskId, varName);
    }

    @Override
    public void setTaskInstanceVariable(String taskId, String varName, Object valor) {
        delegate.setTaskInstanceVariable(taskId, varName, valor);
    }

    @Override
    public void setTaskInstanceVariables(String taskId, Map<String, Object> variables, boolean deleteFirst) {
        delegate.setTaskInstanceVariables(taskId, variables, deleteFirst);
    }

    @Override
    public void deleteTaskInstanceVariable(String taskId, String varName) {
        delegate.deleteTaskInstanceVariable(taskId, varName);
    }

    @Override
    public WToken getTokenById(String tokenId) {
        return delegate.getTokenById(tokenId);
    }

    @Override
    public Map<String, WToken> getActiveTokens(String processInstanceId) {
        return delegate.getActiveTokens(processInstanceId);
    }

    @Override
    public Map<String, WToken> getAllTokens(String processInstanceId) {
        return delegate.getAllTokens(processInstanceId);
    }

    @Override
    public void tokenRedirect(long tokenId, String nodeName, boolean cancelTasks, boolean enterNodeIfTask, boolean executeNode) {
        delegate.tokenRedirect(tokenId, nodeName, cancelTasks, enterNodeIfTask, executeNode);
    }

    @Override
    public boolean tokenActivar(long tokenId, boolean activar) {
        return delegate.tokenActivar(tokenId, activar);
    }

    @Override
    public void signalToken(long tokenId, String transitionName) {
        delegate.signalToken(tokenId, transitionName);
    }

    @Override
    public Map<String, Object> evaluateScript(String processInstanceId, String script, Set<String> outputNames) {
        return delegate.evaluateScript(processInstanceId, script, outputNames);
    }

    @Override
    public Object evaluateExpression(String taskInstanceInstanceId, String processInstanceId, String expression, Map<String, Object> valors) {
        return delegate.evaluateExpression(taskInstanceInstanceId, processInstanceId, expression, valors);
    }

    @Override
    public List<String> listActions(String jbpmId) {
        return delegate.listActions(jbpmId);
    }

    @Override
    public void executeActionInstanciaProces(String processInstanceId, String actionName, String processDefinitionPareId) {
        delegate.executeActionInstanciaProces(processInstanceId, actionName, processDefinitionPareId);
    }

    @Override
    public void executeActionInstanciaTasca(String taskInstanceId, String actionName, String processDefinitionPareId) {
        delegate.executeActionInstanciaTasca(taskInstanceId, actionName, processDefinitionPareId);
    }

    @Override
    public void suspendTimer(long timerId, Date dueDate) {
        delegate.suspendTimer(timerId, dueDate);
    }

    @Override
    public void resumeTimer(long timerId, Date dueDate) {
        delegate.resumeTimer(timerId, dueDate);
    }

    @Override
    public List<String> findStartTaskOutcomes(String jbpmId, String taskName) {
        return delegate.findStartTaskOutcomes(jbpmId, taskName);
    }

    @Override
    public List<String> findTaskInstanceOutcomes(String taskInstanceId) {
        return delegate.findTaskInstanceOutcomes(taskInstanceId);
    }

    @Override
    public List<String> findArrivingNodeNames(String tokenId) {
        return delegate.findArrivingNodeNames(tokenId);
    }

    @Override
    public ExpedientDto expedientFindByProcessInstanceId(String processInstanceId) {
        return delegate.expedientFindByProcessInstanceId(processInstanceId);
    }

    @Override
    public ResultatConsultaPaginada<Long> expedientFindByFiltre(Long entornId, String actorId, Collection<Long> tipusIdPermesos, String titol, String numero, Long tipusId, Date dataCreacioInici, Date dataCreacioFi, Date dataFiInici, Date dataFiFi, Long estatId, Double geoPosX, Double geoPosY, String geoReferencia, boolean nomesIniciats, boolean nomesFinalitzats, boolean mostrarAnulats, boolean mostrarNomesAnulats, boolean nomesAlertes, boolean nomesErrors, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, boolean nomesTasquesMeves, PaginacioParamsDto paginacioParams, boolean nomesCount) {
        return delegate.expedientFindByFiltre(entornId, actorId, tipusIdPermesos, titol, numero, tipusId, dataCreacioInici, dataCreacioFi, dataFiInici, dataFiFi, estatId, geoPosX, geoPosY, geoReferencia, nomesIniciats, nomesFinalitzats, mostrarAnulats, mostrarNomesAnulats, nomesAlertes, nomesErrors, nomesTasquesPersonals, nomesTasquesGrup, nomesTasquesMeves, paginacioParams, nomesCount);
    }

    @Override
    public void desfinalitzarExpedient(String processInstanceId) {
        delegate.desfinalitzarExpedient(processInstanceId);
    }

    @Override
    public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio) {
        delegate.finalitzarExpedient(processInstanceIds, dataFinalitzacio);
    }

    @Override
    public void marcarFinalitzar(String taskId, Date marcadaFinalitzar, String outcome, String rols) {
        delegate.marcarFinalitzar(taskId, marcadaFinalitzar, outcome, rols);
    }

    @Override
    public void marcarIniciFinalitzacioSegonPla(String taskId, Date iniciFinalitzacio) {
        delegate.marcarIniciFinalitzacioSegonPla(taskId, iniciFinalitzacio);
    }

    @Override
    public void guardarErrorFinalitzacio(String taskId, String errorFinalitzacio) {
        delegate.guardarErrorFinalitzacio(taskId, errorFinalitzacio);
    }

    @Override
    public List<Object[]> getTasquesSegonPlaPendents() {
        return delegate.getTasquesSegonPlaPendents();
    }

    @Override
    public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(Long entornId) {
        return delegate.findDefinicionsProcesIdNoUtilitzadesByEntorn(entornId);
    }

    @Override
    public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(Long expedientTipusId) {
        return delegate.findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(expedientTipusId);
    }

    @Override
    public List<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(Long expedientTipusId, Long processDefinitionId) {
        return delegate.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(expedientTipusId, processDefinitionId);
    }

    @Override
    public Object evaluateExpression(String expression, Class expectedClass, Map<String, Object> context) {
        return delegate.evaluateExpression(expression, expectedClass, context);
    }

    @Override
    public void retrocedirAccio(String processInstanceId, String actionName, List<String> params, String processDefinitionPareId) {
        delegate.retrocedirAccio(processInstanceId, actionName, params, processDefinitionPareId);
    }

    @Override
    public void setTaskInstanceActorId(String taskInstanceId, String actorId) {
        delegate.setTaskInstanceActorId(taskInstanceId, actorId);
    }

    @Override
    public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) {
        delegate.setTaskInstancePooledActors(taskInstanceId, pooledActors);
    }

    @Override
    public WProcessDefinition parse(ZipInputStream zipInputStream) throws Exception {
        return delegate.parse(zipInputStream);
    }
}
