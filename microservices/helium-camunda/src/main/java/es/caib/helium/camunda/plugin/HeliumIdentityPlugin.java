package es.caib.helium.camunda.plugin;

import es.caib.helium.camunda.identity.HeliumIdentityProviderFactory;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HeliumIdentityPlugin extends AbstractProcessEnginePlugin {

//    private final HeliumBridge heliumBridge;

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {

        HeliumIdentityProviderFactory heliumIdentityProviderFactory = new HeliumIdentityProviderFactory();
        processEngineConfiguration.setIdentityProviderSessionFactory(heliumIdentityProviderFactory);
    }

}
