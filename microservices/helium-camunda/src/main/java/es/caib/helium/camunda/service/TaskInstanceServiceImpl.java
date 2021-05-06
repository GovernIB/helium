package es.caib.helium.camunda.service;

import es.caib.helium.camunda.model.DelegationInfo;
import es.caib.helium.camunda.model.LlistatIds;
import es.caib.helium.camunda.model.ResultatCompleteTask;
import es.caib.helium.camunda.model.ResultatConsultaPaginada;
import es.caib.helium.camunda.model.WTaskInstance;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public class TaskInstanceServiceImpl implements TaskInstanceService {

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
    public ResultatConsultaPaginada<WTaskInstance> tascaFindByFiltrePaginat(Long entornId, String actorId, String taskName, String titol, Long expedientId, String expedientTitol, String expedientNumero, Long expedientTipusId, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, boolean mostrarAssignadesUsuari, boolean mostrarAssignadesGrup, boolean nomesPendents, Pageable paginacio, boolean nomesCount) {
        return null;
    }

    @Override
    public LlistatIds tascaIdFindByFiltrePaginat(String responsable, String tasca, String tascaSel, List<Long> idsPIExpedients, Date dataCreacioInici, Date dataCreacioFi, Integer prioritat, Date dataLimitInici, Date dataLimitFi, Pageable paginacio, boolean nomesTasquesPersonals, boolean nomesTasquesGrup, boolean nomesAmbPendents) {
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
    public ResultatCompleteTask completeTaskInstance(WTaskInstance task, String outcome) {
        return null;
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
    public void delegateTaskInstance(WTaskInstance task, String actorId, String comentari, boolean supervisada) {

    }

    @Override
    public DelegationInfo getDelegationTaskInstanceInfo(String taskId, boolean includeActors) {
        return null;
    }

    @Override
    public void cancelDelegationTaskInstance(WTaskInstance task) {

    }

    @Override
    public void updateTaskInstanceInfoCache(String taskId, String titol, String infoCache) {

    }
}
