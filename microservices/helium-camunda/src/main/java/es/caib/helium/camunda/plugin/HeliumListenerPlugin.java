package es.caib.helium.camunda.plugin;

import es.caib.helium.camunda.listener.HeliumTaskParseListener;
import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;

import java.util.ArrayList;
import java.util.List;

public class HeliumListenerPlugin extends AbstractProcessEnginePlugin {

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        List<BpmnParseListener> preParseListeners = processEngineConfiguration.getCustomPreBPMNParseListeners();
        if(preParseListeners == null) {
            preParseListeners = new ArrayList<BpmnParseListener>();
            processEngineConfiguration.setCustomPreBPMNParseListeners(preParseListeners);
        }
        preParseListeners.add(new HeliumTaskParseListener());

//        preParseListeners.add(new VariableParseListener());
//        super.preInit(processEngineConfiguration);

        var postParseListeners = processEngineConfiguration.getCustomPostBPMNParseListeners();
        if (postParseListeners == null) {
            postParseListeners = new ArrayList<BpmnParseListener>();
            processEngineConfiguration.setCustomPostBPMNParseListeners(postParseListeners);
        }
//        postParseListeners.add(new HeliumPostTaskParseListener());
    }
}
