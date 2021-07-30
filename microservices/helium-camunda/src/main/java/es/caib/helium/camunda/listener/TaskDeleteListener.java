package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

@Slf4j
public class TaskDeleteListener implements TaskListener {

    private static TaskDeleteListener instance = null;

    protected TaskDeleteListener() { }

    public static TaskDeleteListener getInstance() {
        if(instance == null) {
            instance = new TaskDeleteListener();
        }
        return instance;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been deleted");
    }
}
