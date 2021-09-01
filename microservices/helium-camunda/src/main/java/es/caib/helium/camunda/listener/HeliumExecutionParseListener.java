package es.caib.helium.camunda.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class HeliumExecutionParseListener extends AbstractBpmnParseListener {

    private final TaskCreateListener taskCreateListener;
    private final TaskAssignListener taskAssignListener;
    private final TaskUpdateListener taskUpdateListener;
    private final TaskCompleteListener taskCompleteListener;
    private final TaskTimeoutListener taskTimeoutListener;
    private final TaskDeleteListener taskDeleteListener;

    private final ProcessCreateListener processCreateListener;
    private final ProcessEndListener processEndListener;

    @Override
    public void parseStartEvent(Element startEventElement, ScopeImpl scope, ActivityImpl startEventActivity) {
        startEventActivity.addListener(ExecutionListener.EVENTNAME_START, processCreateListener);
    }

    @Override
    public void parseEndEvent(Element endEventElement, ScopeImpl scope, ActivityImpl endEventactivity) {
        endEventactivity.addListener(ExecutionListener.EVENTNAME_END, processEndListener);
    }

}
