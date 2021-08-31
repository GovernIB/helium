package es.caib.helium.camunda.identity;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.impl.GroupQueryImpl;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

import java.util.List;

public class HeliumGroupQuery extends GroupQueryImpl {

    public HeliumGroupQuery() {
        super();
    }

    public HeliumGroupQuery(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    @Override
    public long executeCount(CommandContext commandContext) {
        final HeliumIdentityProvider provider = getCustomIdentityProvider(commandContext);
        return provider.findGroupCountByQueryCriteria(this);
    }

    @Override
    public List<Group> executeList(CommandContext commandContext, Page page) {
        final HeliumIdentityProvider provider = getCustomIdentityProvider(commandContext);
        return provider.findGroupByQueryCriteria(this);
    }

    protected HeliumIdentityProvider getCustomIdentityProvider(CommandContext commandContext) {
        return (HeliumIdentityProvider) commandContext.getReadOnlyIdentityProvider();
    }
}
