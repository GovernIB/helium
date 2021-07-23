package es.caib.helium.camunda.model.modeler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.DeploymentQuery;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ModelerDeploymentQueryDto extends AbstractQueryDto<DeploymentQuery> {

    private static final String SORT_BY_ID_VALUE = "id";
    private static final String SORT_BY_NAME_VALUE = "name";
    private static final String SORT_BY_DEPLOYMENT_TIME_VALUE = "deploymentTime";
    private static final String SORT_BY_TENANT_ID = "tenantId";
    private static final List<String> VALID_SORT_BY_VALUES = new ArrayList<>();

    private String id;
    private String name;
    private String nameLike;
    private String source;
    private Boolean withoutSource;
    private Date before;
    private Date after;
    private List<String> tenantIds;
    private Boolean withoutTenantId;
    private Boolean includeDeploymentsWithoutTenantId;

    static {
        VALID_SORT_BY_VALUES.add("id");
        VALID_SORT_BY_VALUES.add("name");
        VALID_SORT_BY_VALUES.add("deploymentTime");
        VALID_SORT_BY_VALUES.add("tenantId");
    }

    public ModelerDeploymentQueryDto(ObjectMapper objectMapper, MultivaluedMap<String, String> queryParameters) {
        super(objectMapper, queryParameters);
    }

    @CamundaQueryParam("id")
    public void setId(String id) {
        this.id = id;
    }

    @CamundaQueryParam("name")
    public void setName(String name) {
        this.name = name;
    }

    @CamundaQueryParam("nameLike")
    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    @CamundaQueryParam("source")
    public void setSource(String source) {
        this.source = source;
    }

    @CamundaQueryParam(value = "withoutSource", converter = BooleanConverter.class)
    public void setWithoutSource(Boolean withoutSource) {
        this.withoutSource = withoutSource;
    }

    @CamundaQueryParam(value = "before", converter = DateConverter.class)
    public void setDeploymentBefore(Date deploymentBefore) {
        this.before = deploymentBefore;
    }

    @CamundaQueryParam(value = "after", converter = DateConverter.class)
    public void setDeploymentAfter(Date deploymentAfter) {
        this.after = deploymentAfter;
    }

    @CamundaQueryParam(value = "tenantIdIn", converter = StringListConverter.class)
    public void setTenantIdIn(List<String> tenantIds) {
        this.tenantIds = tenantIds;
    }

    @CamundaQueryParam(value = "withoutTenantId", converter = BooleanConverter.class)
    public void setWithoutTenantId(Boolean withoutTenantId) {
        this.withoutTenantId = withoutTenantId;
    }

    @CamundaQueryParam(value = "includeDeploymentsWithoutTenantId", converter = BooleanConverter.class)
    public void setIncludeDeploymentsWithoutTenantId(Boolean includeDeploymentsWithoutTenantId) {
        this.includeDeploymentsWithoutTenantId = includeDeploymentsWithoutTenantId;
    }

    protected boolean isValidSortByValue(String value) {
        return VALID_SORT_BY_VALUES.contains(value);
    }

    protected DeploymentQuery createNewQuery(ProcessEngine engine) {
        return engine.getRepositoryService().createDeploymentQuery();
    }

    protected void applyFilters(DeploymentQuery query) {
        if (this.withoutSource != null && this.withoutSource.booleanValue() && this.source != null)
            throw new InvalidRequestException(Response.Status.BAD_REQUEST, "The query parameters \"withoutSource\" and \"source\" cannot be used in combination.");
        if (this.id != null)
            query.deploymentId(this.id);
        if (this.name != null)
            query.deploymentName(this.name);
        if (this.nameLike != null)
            query.deploymentNameLike(this.nameLike);
        if (Boolean.TRUE.equals(this.withoutSource))
            query.deploymentSource(null);
        if (this.source != null)
            query.deploymentSource(this.source);
        if (this.before != null)
            query.deploymentBefore(this.before);
        if (this.after != null)
            query.deploymentAfter(this.after);
        if (this.tenantIds != null && !this.tenantIds.isEmpty())
            query.tenantIdIn(this.tenantIds.<String>toArray(new String[this.tenantIds.size()]));
        if (Boolean.TRUE.equals(this.withoutTenantId))
            query.withoutTenantId();
        if (Boolean.TRUE.equals(this.includeDeploymentsWithoutTenantId))
            query.includeDeploymentsWithoutTenantId();
    }

    protected void applySortBy(DeploymentQuery query, String sortBy, Map<String, Object> parameters, ProcessEngine engine) {
        if (sortBy.equals("id")) {
            query.orderByDeploymentId();
        } else if (sortBy.equals("name")) {
            query.orderByDeploymentName();
        } else if (sortBy.equals("deploymentTime")) {
            query.orderByDeploymentTime();
        } else if (sortBy.equals("tenantId")) {
            query.orderByTenantId();
        }
    }
}
