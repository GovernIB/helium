package es.caib.helium.camunda.service;

import es.caib.helium.camunda.mapper.ExecutionMapper;
import es.caib.helium.camunda.model.WToken;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.Execution;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExecutionServiceImpl implements ExecutionService {

//    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final ExecutionMapper executionMapper;
    private final TaskService taskService;
    private final RepositoryService repositoryService;

    @Override
    public WToken getTokenById(String tokenId) {
        Execution execution = getExecution(tokenId);
        return executionMapper.toWToken(execution);
    }

    @Override
    public Map<String, WToken> getActiveTokens(String processInstanceId) {
        var executionMap = new HashMap<String, WToken>();
        var executions = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();
        if (executions != null) {
            executions.stream().forEach(e -> executionMap.put(e.getId(), executionMapper.toWToken(e)));
        }
        return executionMap;
    }

    @Override
    public Map<String, WToken> getAllTokens(String processInstanceId) {
        var executionMap = new HashMap<String, WToken>();
        var executions = runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .list();
        if (executions != null) {
            executions.stream().forEach(e -> executionMap.put(e.getId(), executionMapper.toWToken(e)));
        }
        return executionMap;
    }

    @Override
    public void tokenRedirect(
            String tokenId,
            String nodeName,
            boolean cancelTasks,
            boolean enterNodeIfTask,
            boolean executeNode) {

        var execution = getExecution(tokenId);
        var modification = runtimeService.createProcessInstanceModification(execution.getProcessInstanceId());

        // Si el token té fills actius els desactiva
        // TODO: No sé si en Camunda té sentit. Si es cancelen les tasques dels tokens, aquest queda finalitzat

        // Cancel·la les tasques si s'ha de fer
        if (cancelTasks) {
            var tasquesActives = runtimeService.createExecutionQuery()
                    .executionId(execution.getId())
                    .active()
                    .list();
            if (tasquesActives != null && !tasquesActives.isEmpty()) {
                tasquesActives.stream().forEach(t -> modification.cancelAllForActivity(t.getId()));
            }
        }

        // Fa la redirecció
        // TODO: enterNodeIfTask i executeNode -> crec que no tenen sentit en Camunda
        modification.startBeforeActivity(nodeName);
        modification.execute();
    }

    @Override
    public boolean tokenActivar(String tokenId, boolean activar) {
        // TODO: ??? Per a que s'utilitza? Té sentit en Camunda?
        return false;
    }

    @Override
    public void signalToken(String tokenId, String signalName) {
        // TODO: transitionName == signalName
        runtimeService.createSignalEvent(signalName)
                .executionId(tokenId)
                .send();
//        runtimeService.signal(tokenId, transitionName, new Date(), null);
    }


    private Execution getExecution(String tokenId) {
        var execution = runtimeService.createExecutionQuery()
                .executionId(tokenId)
                .singleResult();
        if (execution == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found. Token: " + tokenId);
        }
        return execution;
    }
}
