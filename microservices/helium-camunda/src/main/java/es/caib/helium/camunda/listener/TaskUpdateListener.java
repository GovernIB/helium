package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

@Slf4j
public class TaskUpdateListener implements TaskListener {

    private static TaskUpdateListener instance = null;

    protected TaskUpdateListener() { }

    public static TaskUpdateListener getInstance() {
        if(instance == null) {
            instance = new TaskUpdateListener();
        }
        return instance;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been updated");
    }
}
