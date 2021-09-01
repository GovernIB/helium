package es.caib.helium.camunda.plugin;

import es.caib.helium.camunda.listener.HeliumExecutionParseListener;
import es.caib.helium.camunda.listener.HeliumTaskParseListener;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class HeliumListenerPlugin extends AbstractProcessEnginePlugin {

    private final HeliumTaskParseListener heliumTaskParseListener;
    private final HeliumExecutionParseListener heliumExecutionParseListener;

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        List<BpmnParseListener> preParseListeners = processEngineConfiguration.getCustomPreBPMNParseListeners();
        if(preParseListeners == null) {
            preParseListeners = new ArrayList<BpmnParseListener>();
            processEngineConfiguration.setCustomPreBPMNParseListeners(preParseListeners);
        }
        preParseListeners.add(heliumTaskParseListener);
//        preParseListeners.add(new VariableParseListener());

        var postParseListeners = processEngineConfiguration.getCustomPostBPMNParseListeners();
        if (postParseListeners == null) {
            postParseListeners = new ArrayList<BpmnParseListener>();
            processEngineConfiguration.setCustomPostBPMNParseListeners(postParseListeners);
        }
        postParseListeners.add(heliumExecutionParseListener);
    }
}
