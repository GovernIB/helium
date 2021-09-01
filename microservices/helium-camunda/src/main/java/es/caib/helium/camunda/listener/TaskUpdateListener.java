package es.caib.helium.camunda.listener;

import es.caib.helium.client.expedient.tasca.TascaClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaskUpdateListener implements TaskListener {

    private final TascaClientService tascaClientService;

//    private static TaskUpdateListener instance = null;
//
//    protected TaskUpdateListener() { }
//
//    public static TaskUpdateListener getInstance() {
//        if(instance == null) {
//            instance = new TaskUpdateListener();
//        }
//        return instance;
//    }

    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been updated");
    }
}
