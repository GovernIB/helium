package es.caib.helium.camunda.model.modeler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.Direction;
import org.camunda.bpm.engine.query.Query;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractQueryDto<T extends Query<?, ?>> extends AbstractSearchQueryDto {
    public static final String SORT_ORDER_ASC_VALUE = "asc";

    public static final String SORT_ORDER_DESC_VALUE = "desc";

    public static final List<String> VALID_SORT_ORDER_VALUES = new ArrayList<>();

    protected String sortBy;

    protected String sortOrder;

    protected List<SortingDto> sortings;

    static {
        VALID_SORT_ORDER_VALUES.add("asc");
        VALID_SORT_ORDER_VALUES.add("desc");
    }

    protected Map<String, String> expressions = new HashMap<>();

    public AbstractQueryDto(ObjectMapper objectMapper, MultivaluedMap<String, String> queryParameters) {
        super(objectMapper, queryParameters);
    }

    @CamundaQueryParam("sortBy")
    public void setSortBy(String sortBy) {
        if (!isValidSortByValue(sortBy))
            throw new InvalidRequestException(Response.Status.BAD_REQUEST, "sortBy parameter has invalid value: " + sortBy);
        this.sortBy = sortBy;
    }

    @CamundaQueryParam("sortOrder")
    public void setSortOrder(String sortOrder) {
        if (!VALID_SORT_ORDER_VALUES.contains(sortOrder))
            throw new InvalidRequestException(Response.Status.BAD_REQUEST, "sortOrder parameter has invalid value: " + sortOrder);
        this.sortOrder = sortOrder;
    }

    public void setSorting(List<SortingDto> sorting) {
        this.sortings = sorting;
    }

    public List<SortingDto> getSorting() {
        return this.sortings;
    }

    protected boolean sortOptionsValid() {
        return ((this.sortBy != null && this.sortOrder != null) || (this.sortBy == null && this.sortOrder == null));
    }

    public T toQuery(ProcessEngine engine) {
        T query = createNewQuery(engine);
        applyFilters(query);
        if (!sortOptionsValid())
            throw new InvalidRequestException(Response.Status.BAD_REQUEST, "Only a single sorting parameter specified. sortBy and sortOrder required");
        applySortingOptions(query, engine);
        return query;
    }

    protected void applySortingOptions(T query, ProcessEngine engine) {
        if (this.sortBy != null)
            applySortBy(query, this.sortBy, null, engine);
        if (this.sortOrder != null)
            applySortOrder(query, this.sortOrder);
        if (this.sortings != null)
            for (SortingDto sorting : this.sortings) {
                String sortingOrder = sorting.getSortOrder();
                String sortingBy = sorting.getSortBy();
                if (sortingBy != null)
                    applySortBy(query, sortingBy, sorting.getParameters(), engine);
                if (sortingOrder != null)
                    applySortOrder(query, sortingOrder);
            }
    }

    protected void applySortOrder(T query, String sortOrder) {
        if (sortOrder != null)
            if (sortOrder.equals("asc")) {
                query.asc();
            } else if (sortOrder.equals("desc")) {
                query.desc();
            }
    }

    public static String sortOrderValueForDirection(Direction direction) {
        if (Direction.ASCENDING.equals(direction))
            return "asc";
        if (Direction.DESCENDING.equals(direction))
            return "desc";
        throw new RestException("Unknown query sorting direction " + direction);
    }

    public AbstractQueryDto() {}

    protected abstract boolean isValidSortByValue(String paramString);

    protected abstract T createNewQuery(ProcessEngine paramProcessEngine);

    protected abstract void applyFilters(T paramT);

    protected abstract void applySortBy(T paramT, String paramString, Map<String, Object> paramMap, ProcessEngine paramProcessEngine);
}

