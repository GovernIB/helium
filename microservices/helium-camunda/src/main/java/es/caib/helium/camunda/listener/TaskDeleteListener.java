package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.extension.reactor.spring.listener.ReactorTaskListener;

@Slf4j
//@Component
//@CamundaSelector(type = ActivityTypes.TASK_USER_TASK, event = TaskListener.EVENTNAME_DELETE)
public class TaskDeleteListener extends ReactorTaskListener {

    public TaskDeleteListener() {
        CamundaReactor.eventBus().register(this);
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been deleted");
    }
}
