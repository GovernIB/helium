package es.caib.helium.camunda.model.modeler;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

public class SortingDto {
    protected String sortBy;

    protected String sortOrder;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Map<String, Object> parameters;

    public String getSortBy() {
        return this.sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
