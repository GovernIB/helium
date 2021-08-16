package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

@Slf4j
public class TaskCompleteListener implements TaskListener {

    private static TaskCompleteListener instance = null;

    protected TaskCompleteListener() { }

    public static TaskCompleteListener getInstance() {
        if(instance == null) {
            instance = new TaskCompleteListener();
        }
        return instance;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been completed");
    }
}
