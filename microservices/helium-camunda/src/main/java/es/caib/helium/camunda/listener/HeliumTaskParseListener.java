package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;

@Slf4j
public class HeliumTaskParseListener extends AbstractBpmnParseListener {

    @Override
    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        ActivityBehavior activityBehavior = activity.getActivityBehavior();
        if(activityBehavior instanceof UserTaskActivityBehavior){
            UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
            var taskDefinition = userTaskActivityBehavior.getTaskDefinition();
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_CREATE, TaskCreateListener.getInstance());
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_ASSIGNMENT, TaskAssignListener.getInstance());
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_UPDATE, TaskUpdateListener.getInstance());
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_COMPLETE, TaskCompleteListener.getInstance());
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_TIMEOUT, TaskTimeoutListener.getInstance());
            taskDefinition.addTaskListener(TaskListener.EVENTNAME_DELETE, TaskDeleteListener.getInstance());
        }
        log.info("parseUserTask" + userTaskElement.getTagName() + ", Activity: " + activity.getName());
    }

}
