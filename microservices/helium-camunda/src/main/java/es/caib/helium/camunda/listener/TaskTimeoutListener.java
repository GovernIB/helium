package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ActivityTypes;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;
import org.camunda.bpm.extension.reactor.spring.listener.ReactorTaskListener;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
//@CamundaSelector(type = ActivityTypes.TASK_USER_TASK, event = TaskListener.EVENTNAME_TIMEOUT)
public class TaskTimeoutListener extends ReactorTaskListener {

    public TaskTimeoutListener() {
        CamundaReactor.eventBus().register(this);
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been expired");
    }
}
