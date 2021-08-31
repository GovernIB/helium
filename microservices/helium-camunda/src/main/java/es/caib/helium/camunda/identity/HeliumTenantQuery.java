package es.caib.helium.camunda.identity;

import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.TenantQueryImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;

import java.util.List;

public class HeliumTenantQuery extends TenantQueryImpl {

    public HeliumTenantQuery() {
        super();
    }

    public HeliumTenantQuery(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    @Override
    public long executeCount(CommandContext commandContext) {
        final HeliumIdentityProvider provider = getCustomIdentityProvider(commandContext);
        return provider.findTenantCountByQueryCriteria(this);
    }

    @Override
    public List<Tenant> executeList(CommandContext commandContext, Page page) {
        final HeliumIdentityProvider provider = getCustomIdentityProvider(commandContext);
        return provider.findTenantByQueryCriteria(this);
    }

    protected HeliumIdentityProvider getCustomIdentityProvider(CommandContext commandContext) {
        return (HeliumIdentityProvider) commandContext.getReadOnlyIdentityProvider();
    }

}
