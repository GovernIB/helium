package es.caib.helium.camunda.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class HeliumTaskParseListener extends AbstractBpmnParseListener {

    private final TaskCreateListener taskCreateListener;
    private final TaskAssignListener taskAssignListener;
    private final TaskUpdateListener taskUpdateListener;
    private final TaskCompleteListener taskCompleteListener;
    private final TaskTimeoutListener taskTimeoutListener;
    private final TaskDeleteListener taskDeleteListener;

    private final ProcessCreateListener processCreateListener;
    private final ProcessEndListener processEndListener;

    @Override
    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        ActivityBehavior activityBehavior = activity.getActivityBehavior();
        if(activityBehavior instanceof UserTaskActivityBehavior){
            UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
            var taskDefinition = userTaskActivityBehavior.getTaskDefinition();
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_CREATE, taskCreateListener);
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_ASSIGNMENT, taskAssignListener);
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_UPDATE, taskUpdateListener);
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_COMPLETE, taskCompleteListener);
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_TIMEOUT, taskTimeoutListener);
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_DELETE, taskDeleteListener);
        }
    }

}
