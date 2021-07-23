package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ActivityTypes;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.extension.reactor.bus.CamundaSelector;
import org.camunda.bpm.extension.reactor.spring.listener.ReactorTaskListener;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
//@Component
//@CamundaSelector(type = ActivityTypes.SUB_PROCESS, event = TaskListener.EVENTNAME_CREATE)
public class SubProcessCreateListener extends ReactorTaskListener {

    private static Logger logger = Logger.getLogger(SubProcessCreateListener.class.getName());

    public SubProcessCreateListener() {
        CamundaReactor.eventBus().register(this);
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Dades
        log.info("Subproces " + delegateTask.getName() + "has been created");
    }
}
