package es.caib.helium.camunda.identity;

import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.UserQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

import java.util.List;

public class HeliumUserQuery extends UserQueryImpl {

    public HeliumUserQuery() {
        super();
    }

    public HeliumUserQuery(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    @Override
    public long executeCount(CommandContext commandContext) {
        final HeliumIdentityProvider provider = getCustomIdentityProvider(commandContext);
        return provider.findUserCountByQueryCriteria(this);
    }

    @Override
    public List<User> executeList(CommandContext commandContext, Page page) {
        final HeliumIdentityProvider provider = getCustomIdentityProvider(commandContext);
        return provider.findUserByQueryCriteria(this);
    }

    protected HeliumIdentityProvider getCustomIdentityProvider(CommandContext commandContext) {
        return (HeliumIdentityProvider) commandContext.getReadOnlyIdentityProvider();
    }
}
