package es.caib.helium.camunda.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.ProcessEngineService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Component
public class EngineHelper {

    private static EngineHelper INSTANCE;

    private final ProcessEngineService processEngineService;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final ManagementService managementService;


    public ProcessEngineService getProcessEngineService() {
        return processEngineService;
    }

    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public RuntimeService getRuntimeService() {
        return runtimeService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public HistoryService getHistoryService() {
        return historyService;
    }

    public ManagementService getManagementService() {
        return managementService;
    }

    public static EngineHelper getInstance() {
        return INSTANCE;
    }

    @PostConstruct
    public void postConstruct() {
        INSTANCE = this;
    }
}
