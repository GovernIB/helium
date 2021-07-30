package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

@Slf4j
public class TaskAssignListener implements TaskListener {

    private static TaskAssignListener instance = null;

    protected TaskAssignListener() { }

    public static TaskAssignListener getInstance() {
        if(instance == null) {
            instance = new TaskAssignListener();
        }
        return instance;
    }
    
    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been assigned to " + delegateTask.getAssignee());
    }
}
