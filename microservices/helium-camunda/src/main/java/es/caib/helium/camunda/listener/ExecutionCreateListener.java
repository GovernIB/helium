package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.extension.reactor.spring.listener.ReactorExecutionListener;

import java.util.logging.Logger;

@Slf4j
//@Component
//@CamundaSelector(
//        event = ExecutionListener.EVENTNAME_START)
public class ExecutionCreateListener extends ReactorExecutionListener {

    private static Logger logger = Logger.getLogger(ExecutionCreateListener.class.getName());

    public ExecutionCreateListener() {
        CamundaReactor.eventBus().register(this);
    }

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        // Publicar al MS Dades
        log.info("Execution (token) " + delegateExecution.getCurrentActivityName() + "has been created");
    }
}
