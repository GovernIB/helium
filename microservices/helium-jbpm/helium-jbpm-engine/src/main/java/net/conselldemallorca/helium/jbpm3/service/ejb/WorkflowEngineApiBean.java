package net.conselldemallorca.helium.jbpm3.service.ejb;

import net.conselldemallorca.helium.api.dto.ExpedientDto;
import net.conselldemallorca.helium.api.dto.LlistatIds;
import net.conselldemallorca.helium.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.api.dto.ResultatConsultaPaginada;
import net.conselldemallorca.helium.api.exception.HeliumJbpmException;
import net.conselldemallorca.helium.api.service.WDeployment;
import net.conselldemallorca.helium.api.service.WProcessDefinition;
import net.conselldemallorca.helium.api.service.WProcessInstance;
import net.conselldemallorca.helium.api.service.WTaskInstance;
import net.conselldemallorca.helium.api.service.WToken;
import net.conselldemallorca.helium.api.service.WorkflowEngineApi;
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
    public WDeployment desplegar(String nomArxiu, byte[] contingut) throws HeliumJbpmException {
        return delegate.desplegar(nomArxiu, contingut);
    }

    @Override
    public WDeployment getDesplegament(String deploymentId) throws HeliumJbpmException {
        return delegate.getDesplegament(deploymentId);
    }

    @Override
    public void esborrarDesplegament(String deploymentId) throws HeliumJbpmException {
        delegate.esborrarDesplegament(deploymentId);
    }

    @Override
    public Set<String> getResourceNames(String deploymentId) throws HeliumJbpmException {
        return delegate.getResourceNames(deploymentId);
    }

    @Override
    public byte[] getResourceBytes(String deploymentId, String resourceName) throws HeliumJbpmException {
        return delegate.getResourceBytes(deploymentId, resourceName);
    }

    @Override
    public void updateDeploymentActions(Long deploymentId, byte[] deploymentContent) throws HeliumJbpmException, Exception {
        delegate.updateDeploymentActions(deploymentId, deploymentContent);
    }

    @Override
    public void propagateDeploymentActions(String deploymentOrigenId, String deploymentDestiId) throws HeliumJbpmException {
        delegate.propagateDeploymentActions(deploymentOrigenId, deploymentDestiId);
    }

    @Override
    public WProcessDefinition getProcessDefinition(String processDefinitionId) throws HeliumJbpmException {
        return delegate.getProcessDefinition(processDefinitionId);
    }

    @Override
    public List<WProcessDefinition> getSubProcessDefinitions(String processDefinitionId) throws HeliumJbpmException {
        return delegate.getSubProcessDefinitions(processDefinitionId);
    }

    @Override
    public List<String> getTaskNamesFromDeployedProcessDefinition(String deploymentId, String processDefinitionId) throws HeliumJbpmException {
        return delegate.getTaskNamesFromDeployedProcessDefinition(deploymentId, processDefinitionId);
    }

    @Override
    public String getStartTaskName(String processDefinitionId) throws HeliumJbpmException {
        return delegate.getStartTaskName(processDefinitionId);
    }

    @Override
    public WProcessDefinition findProcessDefinitionWithProcessInstanceId(String processInstanceId) throws HeliumJbpmException {
        return delegate.findProcessDefinitionWithProcessInstanceId(processInstanceId);
    }

    @Override
    public void updateSubprocessDefinition(String pd1, String pd2) throws HeliumJbpmException {
        delegate.updateSubprocessDefinition(pd1, pd2);
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionId(String processDefinitionId) throws HeliumJbpmException {
        return delegate.findProcessInstancesWithProcessDefinitionId(processDefinitionId);
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionName(String processName) throws HeliumJbpmException {
        return delegate.findProcessInstancesWithProcessDefinitionName(processName);
    }

    @Override
    public List<WProcessInstance> findProcessInstancesWithProcessDefinitionNameAndEntorn(String processName, String entornId) throws HeliumJbpmException {
        return delegate.findProcessInstancesWithProcessDefinitionNameAndEntorn(processName, entornId);
    }

    @Override
    public List<WProcessInstance> getProcessInstanceTree(String rootProcessInstanceId) throws HeliumJbpmException {
        return delegate.getProcessInstanceTree(rootProcessInstanceId);
    }

    @Override
    public WProcessInstance getProcessInstance(String processInstanceId) throws HeliumJbpmException {
        return delegate.getProcessInstance(processInstanceId);
    }

    @Override
    public WProcessInstance getRootProcessInstance(String processInstanceId) throws HeliumJbpmException {
        return delegate.getRootProcessInstance(processInstanceId);
    }

    @Override
    public List<String> findRootProcessInstances(String actorId, List<String> processInstanceIds, boolean nomesMeves, boolean nomesTasquesPersonals, boolean nomesTasquesGrup) throws HeliumJbpmException {
        return delegate.findRootProcessInstances(actorId, processInstanceIds, nomesMeves, nomesTasquesPersonals, nomesTasquesGrup);
    }

    @Override
    public WProcessInstance startProcessInstanceById(String actorId, String processDefinitionId, Map<String, Object> variables) throws HeliumJbpmException {
        return delegate.startProcessInstanceById(actorId, processDefinitionId, variables);
    }

    @Override
    public void signalProcessInstance(String processInstanceId, String transitionName) throws HeliumJbpmException {
        delegate.signalProcessInstance(processInstanceId, transitionName);
    }

    @Override
    public void deleteProcessInstance(String processInstanceId) throws HeliumJbpmException {
        delegate.deleteProcessInstance(processInstanceId);
    }

    @Override
    public void suspendProcessInstances(String[] processInstanceIds) throws HeliumJbpmException {
        delegate.suspendProcessInstances(processInstanceIds);
    }

    @Override
    public void resumeProcessInstances(String[] processInstanceIds) throws HeliumJbpmException {
        delegate.resumeProcessInstances(processInstanceIds);
    }

    @Override
    public void changeProcessInstanceVersion(String processInstanceId, int newVersion) throws HeliumJbpmException {
        delegate.changeProcessInstanceVersion(processInstanceId, newVersion);
    }

    @Override
    public Map<String, Object> getProcessInstanceVariables(String processInstanceId) throws HeliumJbpmException {
        return delegate.getProcessInstanceVariables(processInstanceId);
    }

    @Override
    public Object getProcessInstanceVariable(String processInstanceId, String varName) throws HeliumJbpmException {
        return delegate.getProcessInstanceVariable(processInstanceId, varName);
    }

    @Override
    public void setProcessInstanceVariable(String processInstanceId, String varName, Object value) throws HeliumJbpmException {
        delegate.setProcessInstanceVariable(processInstanceId, varName, value);
    }

    @Override
    public void deleteProcessInstanceVariable(String processInstanceId, String varName) throws HeliumJbpmException {
        delegate.deleteProcessInstanceVariable(processInstanceId, varName);
    }

    @Override
    public WTaskInstance getTaskById(String taskId) throws HeliumJbpmException {
        return delegate.getTaskById(taskId);
    }

    @Override
    public List<WTaskInstance> findTaskInstancesByProcessInstanceId(String processInstanceId) throws HeliumJbpmException {
        return delegate.findTaskInstancesByProcessInstanceId(processInstanceId);
    }

    @Override
    public String getTaskInstanceIdByExecutionTokenId(String executionTokenId) throws HeliumJbpmException {
        return delegate.getTaskInstanceIdByExecutionTokenId(executionTokenId);
    }

    @Override
    public ResultatConsultaPaginada<WTaskInstance> tascaFindByFiltrePaginat(Long entornId, String actorId, String taskName, String titol, Long expedientId, String expedientTitol, String expedientNumero, Long expedientTipusId, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, boolean mostrarAssignadesUsuari, boolean mostrarAssignadesGrup, boolean nomesPendents, PaginacioParamsDto paginacioParams, boolean nomesCount) throws HeliumJbpmException {
        return delegate.tascaFindByFiltrePaginat(entornId, actorId, taskName, titol, expedientId, expedientTitol, expedientNumero, expedientTipusId, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, mostrarAssignadesUsuari, mostrarAssignadesGrup, nomesPendents, paginacioParams, nomesCount);
    }

    @Override
    public LlistatIds tascaIdFindByFiltrePaginat(String responsable, String tasca, String tascaSel, List<Long> idsPIExpedients, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, PaginacioParamsDto paginacioParams, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, boolean nomesAmbPendents) throws HeliumJbpmException {
        return delegate.tascaIdFindByFiltrePaginat(responsable, tasca, tascaSel, idsPIExpedients, dataCreacioInici, dataCreacioFi, prioritat, dataLimitInici, dataLimitFi, paginacioParams, nomesTasquesPersonals, nomesTasquesGrup, nomesAmbPendents);
    }

    @Override
    public WTaskInstance takeTaskInstance(String taskId, String actorId) throws HeliumJbpmException {
        return delegate.takeTaskInstance(taskId, actorId);
    }

    @Override
    public WTaskInstance releaseTaskInstance(String taskId) throws HeliumJbpmException {
        return delegate.releaseTaskInstance(taskId);
    }

    @Override
    public WTaskInstance startTaskInstance(String taskId) throws HeliumJbpmException {
        return delegate.startTaskInstance(taskId);
    }

    @Override
    public void endTaskInstance(String taskId, String outcome) throws HeliumJbpmException {
        delegate.endTaskInstance(taskId, outcome);
    }

    @Override
    public WTaskInstance cancelTaskInstance(String taskId) throws HeliumJbpmException {
        return delegate.cancelTaskInstance(taskId);
    }

    @Override
    public WTaskInstance suspendTaskInstance(String taskId) throws HeliumJbpmException {
        return delegate.suspendTaskInstance(taskId);
    }

    @Override
    public WTaskInstance resumeTaskInstance(String taskId) throws HeliumJbpmException {
        return delegate.resumeTaskInstance(taskId);
    }

    @Override
    public WTaskInstance reassignTaskInstance(String taskId, String expression, Long entornId) throws HeliumJbpmException {
        return delegate.reassignTaskInstance(taskId, expression, entornId);
    }

    @Override
    public void updateTaskInstanceInfoCache(String taskId, String titol, String infoCache) throws HeliumJbpmException {
        delegate.updateTaskInstanceInfoCache(taskId, titol, infoCache);
    }

    @Override
    public Map<String, Object> getTaskInstanceVariables(String taskId) throws HeliumJbpmException {
        return delegate.getTaskInstanceVariables(taskId);
    }

    @Override
    public Object getTaskInstanceVariable(String taskId, String varName) throws HeliumJbpmException {
        return delegate.getTaskInstanceVariable(taskId, varName);
    }

    @Override
    public void setTaskInstanceVariable(String taskId, String varName, Object valor) throws HeliumJbpmException {
        delegate.setTaskInstanceVariable(taskId, varName, valor);
    }

    @Override
    public void setTaskInstanceVariables(String taskId, Map<String, Object> variables, boolean deleteFirst) throws HeliumJbpmException {
        delegate.setTaskInstanceVariables(taskId, variables, deleteFirst);
    }

    @Override
    public void deleteTaskInstanceVariable(String taskId, String varName) throws HeliumJbpmException {
        delegate.deleteTaskInstanceVariable(taskId, varName);
    }

    @Override
    public WToken getTokenById(String tokenId) throws HeliumJbpmException {
        return delegate.getTokenById(tokenId);
    }

    @Override
    public Map<String, WToken> getActiveTokens(String processInstanceId) throws HeliumJbpmException {
        return delegate.getActiveTokens(processInstanceId);
    }

    @Override
    public Map<String, WToken> getAllTokens(String processInstanceId) throws HeliumJbpmException {
        return delegate.getAllTokens(processInstanceId);
    }

    @Override
    public void tokenRedirect(String tokenId, String nodeName, boolean cancelTasks, boolean enterNodeIfTask, boolean executeNode) throws HeliumJbpmException {
        delegate.tokenRedirect(tokenId, nodeName, cancelTasks, enterNodeIfTask, executeNode);
    }

    @Override
    public boolean tokenActivar(String tokenId, boolean activar) throws HeliumJbpmException {
        return delegate.tokenActivar(tokenId, activar);
    }

    @Override
    public void signalToken(String tokenId, String transitionName) throws HeliumJbpmException {
        delegate.signalToken(tokenId, transitionName);
    }

    @Override
    public Map<String, Object> evaluateScript(String processInstanceId, String script, Set<String> outputNames) throws HeliumJbpmException {
        return delegate.evaluateScript(processInstanceId, script, outputNames);
    }

    @Override
    public Object evaluateExpression(String taskInstanceInstanceId, String processInstanceId, String expression, Map<String, Object> valors) throws HeliumJbpmException {
        return delegate.evaluateExpression(taskInstanceInstanceId, processInstanceId, expression, valors);
    }

    @Override
    public List<String> listActions(String jbpmId) throws HeliumJbpmException {
        return delegate.listActions(jbpmId);
    }

    @Override
    public void executeActionInstanciaProces(String processInstanceId, String actionName, String processDefinitionPareId) throws HeliumJbpmException {
        delegate.executeActionInstanciaProces(processInstanceId, actionName, processDefinitionPareId);
    }

    @Override
    public void executeActionInstanciaTasca(String taskInstanceId, String actionName, String processDefinitionPareId) throws HeliumJbpmException {
        delegate.executeActionInstanciaTasca(taskInstanceId, actionName, processDefinitionPareId);
    }

    @Override
    public void suspendTimer(String timerId, Date dueDate) throws HeliumJbpmException {
        delegate.suspendTimer(timerId, dueDate);
    }

    @Override
    public void resumeTimer(String timerId, Date dueDate) throws HeliumJbpmException {
        delegate.resumeTimer(timerId, dueDate);
    }

    @Override
    public List<String> findAreesByFiltre(String filtre) throws HeliumJbpmException {
        return delegate.findAreesByFiltre(filtre);
    }

    @Override
    public List<String> findAreesByPersona(String personaCodi) throws HeliumJbpmException {
        return delegate.findAreesByPersona(personaCodi);
    }

    @Override
    public List<String> findRolsByPersona(String persona) throws HeliumJbpmException {
        return delegate.findRolsByPersona(persona);
    }

    @Override
    public List<String[]> findCarrecsByFiltre(String filtre) throws HeliumJbpmException {
        return delegate.findCarrecsByFiltre(filtre);
    }

    @Override
    public List<String> findPersonesByGrupAndCarrec(String areaCodi, String carrecCodi) throws HeliumJbpmException {
        return delegate.findPersonesByGrupAndCarrec(areaCodi, carrecCodi);
    }

    @Override
    public List<String> findCarrecsByPersonaAndGrup(String codiPersona, String codiArea) throws HeliumJbpmException {
        return delegate.findPersonesByGrupAndCarrec(codiPersona, codiArea);
    }

    @Override
    public List<String> findPersonesByCarrec(String codi) throws HeliumJbpmException {
        return delegate.findPersonesByCarrec(codi);
    }

    @Override
    public List<String> findPersonesByGrup(String rol) throws HeliumJbpmException {
        return delegate.findPersonesByGrup(rol);
    }

    @Override
    public List<String> findStartTaskOutcomes(String jbpmId, String taskName) throws HeliumJbpmException {
        return delegate.findStartTaskOutcomes(jbpmId, taskName);
    }

    @Override
    public List<String> findTaskInstanceOutcomes(String taskInstanceId) throws HeliumJbpmException {
        return delegate.findTaskInstanceOutcomes(taskInstanceId);
    }

    @Override
    public List<String> findArrivingNodeNames(String tokenId) throws HeliumJbpmException {
        return delegate.findArrivingNodeNames(tokenId);
    }

    @Override
    public ExpedientDto expedientFindByProcessInstanceId(String processInstanceId) throws HeliumJbpmException {
        return delegate.expedientFindByProcessInstanceId(processInstanceId);
    }

    @Override
    public ResultatConsultaPaginada<Long> expedientFindByFiltre(Long entornId, String actorId, Collection<Long> tipusIdPermesos, String titol, String numero, Long tipusId, Date dataCreacioInici, Date dataCreacioFi, Date dataFiInici, Date dataFiFi, Long estatId, Double geoPosX, Double geoPosY, String geoReferencia, boolean nomesIniciats, boolean nomesFinalitzats, boolean mostrarAnulats, boolean mostrarNomesAnulats, boolean nomesAlertes, boolean nomesErrors, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, boolean nomesTasquesMeves, PaginacioParamsDto paginacioParams, boolean nomesCount) throws HeliumJbpmException {
        return delegate.expedientFindByFiltre(entornId, actorId, tipusIdPermesos, titol, numero, tipusId, dataCreacioInici, dataCreacioFi, dataFiInici, dataFiFi, estatId, geoPosX, geoPosY, geoReferencia, nomesIniciats, nomesFinalitzats, mostrarAnulats, mostrarNomesAnulats, nomesAlertes, nomesErrors, nomesTasquesPersonals, nomesTasquesGrup, nomesTasquesMeves, paginacioParams, nomesCount);
    }

    @Override
    public void desfinalitzarExpedient(String processInstanceId) throws HeliumJbpmException {
        delegate.desfinalitzarExpedient(processInstanceId);
    }

    @Override
    public void finalitzarExpedient(String[] processInstanceIds, Date dataFinalitzacio) throws HeliumJbpmException {
        delegate.finalitzarExpedient(processInstanceIds, dataFinalitzacio);
    }

    @Override
    public void marcarFinalitzar(String taskId, Date marcadaFinalitzar, String outcome, String rols) throws HeliumJbpmException {
        delegate.marcarFinalitzar(taskId, marcadaFinalitzar, outcome, rols);
    }

    @Override
    public void marcarIniciFinalitzacioSegonPla(String taskId, Date iniciFinalitzacio) throws HeliumJbpmException {
        delegate.marcarIniciFinalitzacioSegonPla(taskId, iniciFinalitzacio);
    }

    @Override
    public void guardarErrorFinalitzacio(String taskId, String errorFinalitzacio) throws HeliumJbpmException {
        delegate.guardarErrorFinalitzacio(taskId, errorFinalitzacio);
    }

    @Override
    public List<Object[]> getTasquesSegonPlaPendents() throws HeliumJbpmException {
        return delegate.getTasquesSegonPlaPendents();
    }

    @Override
    public List<String> findDefinicionsProcesIdNoUtilitzadesByEntorn(Long entornId) throws HeliumJbpmException {
        return delegate.findDefinicionsProcesIdNoUtilitzadesByEntorn(entornId);
    }

    @Override
    public List<String> findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(Long expedientTipusId) throws HeliumJbpmException {
        return delegate.findDefinicionsProcesIdNoUtilitzadesByExpedientTipusId(expedientTipusId);
    }

    @Override
    public List<ExpedientDto> findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(Long expedientTipusId, Long processDefinitionId) throws HeliumJbpmException {
        return delegate.findExpedientsAfectatsPerDefinicionsProcesNoUtilitzada(expedientTipusId, processDefinitionId);
    }

    @Override
    public Object evaluateExpression(String expression, Class expectedClass, Map<String, Object> context) throws HeliumJbpmException {
        return delegate.evaluateExpression(expression, expectedClass, context);
    }

    @Override
    public void retrocedirAccio(String processInstanceId, String actionName, List<String> params, String processDefinitionPareId) throws HeliumJbpmException {
        delegate.retrocedirAccio(processInstanceId, actionName, params, processDefinitionPareId);
    }

    @Override
    public void setTaskInstanceActorId(String taskInstanceId, String actorId) throws HeliumJbpmException {
        delegate.setTaskInstanceActorId(taskInstanceId, actorId);
    }

    @Override
    public void setTaskInstancePooledActors(String taskInstanceId, String[] pooledActors) throws HeliumJbpmException {
        delegate.setTaskInstancePooledActors(taskInstanceId, pooledActors);
    }

    @Override
    public WProcessDefinition parse(ZipInputStream zipInputStream) throws HeliumJbpmException, Exception {
        return delegate.parse(zipInputStream);
    }
}
