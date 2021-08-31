package es.caib.helium.camunda.identity;

import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;

//@Service
public class HeliumIdentityProviderFactory implements SessionFactory {

    @Override
    public Class<?> getSessionType() {
        return ReadOnlyIdentityProvider.class;
    }

    @Override
    public Session openSession() {
        return new HeliumIdentityProvider();
    }

}
