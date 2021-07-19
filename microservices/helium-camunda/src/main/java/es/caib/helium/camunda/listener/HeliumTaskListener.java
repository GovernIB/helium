package es.caib.helium.camunda.listener;


import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

@Slf4j
public class HeliumTaskListener implements TaskListener {

    private static HeliumTaskListener instance = null;

    protected HeliumTaskListener() { }

    public static HeliumTaskListener getInstance() {
        if(instance == null) {
            instance = new HeliumTaskListener();
        }
        return instance;
    }

    public void notify(DelegateTask delegateTask) {
        String assignee = delegateTask.getAssignee();
        log.info("Hello " + assignee + "! Please start to work on your task " + delegateTask.getName());
    }

}
