package es.caib.helium.camunda.identity;

import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.identity.NativeUserQuery;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.TenantQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.identity.ReadOnlyIdentityProvider;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;

import java.util.ArrayList;
import java.util.List;

//@Service
public class HeliumIdentityProvider implements ReadOnlyIdentityProvider {

    @Override
    public User findUserById(String s) {

        // TODO: obtenir usuari per id
        return null;
    }

    @Override
    public UserQuery createUserQuery() {
        return new HeliumUserQuery(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    @Override
    public UserQuery createUserQuery(CommandContext commandContext) {
        return null;
    }

    @Override
    public NativeUserQuery createNativeUserQuery() {
        return null;
    }

    public long findUserCountByQueryCriteria(HeliumUserQuery query) {
        return findUserByQueryCriteria(query).size();
    }

    public List<User> findUserByQueryCriteria(HeliumUserQuery query) {

        List<User> users = new ArrayList<>();

        // TODO: Obtenir usuaris
        String id = query.getId();
        String firstName = query.getFirstName();
        String lastName = query.getLastName();
        String email = query.getEmail();
        String grupId = query.getGroupId();



        return users;
    }

    // TODO: Seycon no retorna password. Keycloak retorna password encriptat?
    //       Llavors hem de verificar-ho directament a keycloak
    @Override
    public boolean checkPassword(String userId, String password) {
        if(userId == null || password == null || userId.isEmpty() || password.isEmpty())
            return false;

        User user = findUserById(userId);

        if(user == null)
            return false;

        return user.getPassword().equals(password);
    }

    @Override
    public Group findGroupById(String s) {
        // TODO: obtenir grup per id
        return null;
    }

    @Override
    public GroupQuery createGroupQuery() {
        return new HeliumGroupQuery(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    @Override
    public GroupQuery createGroupQuery(CommandContext commandContext) {
        return new HeliumGroupQuery();
    }

    public long findGroupCountByQueryCriteria(HeliumGroupQuery query) {
        return findGroupByQueryCriteria(query).size();
    }

    public List<Group> findGroupByQueryCriteria(HeliumGroupQuery query) {

        List<Group> grups = new ArrayList<>();

        // TODO: Obtenir grups
        String id = query.getId();
        String name = query.getName();
        String type = query.getType();

        return grups;
    }

    // TenantId == EntornId
    @Override
    public Tenant findTenantById(String s) {
        // TODO: obtenir entorn per id
        return null;
    }

    @Override
    public TenantQuery createTenantQuery() {
        return new HeliumTenantQuery(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    @Override
    public TenantQuery createTenantQuery(CommandContext commandContext) {
        return new HeliumTenantQuery();
    }

    public long findTenantCountByQueryCriteria(HeliumTenantQuery query) {
        return findTenantByQueryCriteria(query).size();
    }

    public List<Tenant> findTenantByQueryCriteria(HeliumTenantQuery query) {

        List<Tenant> tenants = new ArrayList<>();

        // TODO: Obtenir entorns
        String id = query.getId();
        String name = query.getName();

        return tenants;
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }

}
