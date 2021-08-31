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
public class TaskAssignListener implements TaskListener {

    private final TascaClientService tascaClientService;

//    protected TaskAssignListener() { }
//
//    private static TaskAssignListener instance = null;
//
//    public static TaskAssignListener getInstance() {
//        if(instance == null) {
//            instance = new TaskAssignListener();
//        }
//        return instance;
//    }
    
    @Override
    public void notify(DelegateTask delegateTask) {
        // Publicar al MS Expedients i Tasques
        log.info("Tasca " + delegateTask.getName() + "has been assigned to " + delegateTask.getAssignee());
    }
}
