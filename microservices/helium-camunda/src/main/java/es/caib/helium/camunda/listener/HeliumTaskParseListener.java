package es.caib.helium.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.impl.variable.VariableDeclaration;

@Slf4j
public class HeliumTaskParseListener extends AbstractBpmnParseListener {

    @Override
    public void parseProcess(Element processElement, ProcessDefinitionEntity processDefinition) {
        super.parseProcess(processElement, processDefinition);
        log.info("parseProcess: " + processElement.getTagName());
    }

    @Override
    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        ActivityBehavior activityBehavior = activity.getActivityBehavior();
        if(activityBehavior instanceof UserTaskActivityBehavior){
            UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
            userTaskActivityBehavior
                    .getTaskDefinition()
                    .addTaskListener(ExecutionListener.EVENTNAME_END, HeliumTaskListener.getInstance());
        }
        log.info("parseUserTask" + userTaskElement.getTagName() + ", Activity: " + activity.getName());
    }

    @Override
    public void parseSubProcess(Element subProcessElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseSubProcess(subProcessElement, scope, activity);
        log.info("parseSubProcess" + subProcessElement.getTagName() + ", Activity: " + activity.getName());
    }

    @Override
    public void parseCallActivity(Element callActivityElement, ScopeImpl scope, ActivityImpl activity) {
        super.parseCallActivity(callActivityElement, scope, activity);
        log.info("parseCallActivity" + callActivityElement.getTagName() + ", Activity: " + activity.getName());
    }

    @Override
    public void parseProperty(Element propertyElement, VariableDeclaration variableDeclaration, ActivityImpl activity) {
        super.parseProperty(propertyElement, variableDeclaration, activity);
        log.info("parseProperty" + propertyElement.getTagName() + ", Activity: " + activity.getName() + ", variableDeclaration: " + variableDeclaration.getName());
    }
}
