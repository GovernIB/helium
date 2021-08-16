package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

@Slf4j
public class TaskTimeoutListener implements TaskListener {

    private static TaskTimeoutListener instance = null;

    protected TaskTimeoutListener() { }

    public static TaskTimeoutListener getInstance() {
        if(instance == null) {
            instance = new TaskTimeoutListener();
        }
        return instance;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been expired");
    }
}
