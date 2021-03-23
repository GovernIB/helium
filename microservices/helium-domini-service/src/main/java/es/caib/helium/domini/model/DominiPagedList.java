package es.caib.helium.domini.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public class DominiPagedList extends PagedList<DominiDto> {

    public DominiPagedList(
            List<DominiDto> content,
            int number,
            int size,
            Long totalElements,
            JsonNode pageable,
            boolean last,
            int totalPages,
            JsonNode sort,
            boolean first,
            int numberOfElements) {
        super(content, number, size, totalElements, pageable, last, totalPages, sort, first, numberOfElements);
    }
}
