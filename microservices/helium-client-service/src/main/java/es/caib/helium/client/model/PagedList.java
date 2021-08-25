package es.caib.helium.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;


/**
 * PagedList
 */
public class PagedList<T> extends PageImpl<T> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PagedList(@JsonProperty("content") List<T> content,
                     @JsonProperty("number") int number,
                     @JsonProperty("size") int size,
                     @JsonProperty("totalElements") Long totalElements,
                     @JsonProperty("pageable") JsonNode pageable,
                     @JsonProperty("last") boolean last,
                     @JsonProperty("totalPages") int totalPages,
                     @JsonProperty("sort") JsonNode sort,
                     @JsonProperty("first") boolean first,
                     @JsonProperty("numberOfElements") int numberOfElements) {
        super(content, size == 0 ? Pageable.unpaged() : PageRequest.of(number, size), totalElements);
    }

    public PagedList(
            List<T> content,
            Pageable pageable,
            long total) {
        super(content, pageable, total);
    }

    public PagedList(List<T> content) {
        super(content);
    }
    
    public static <T>PagedList<T> of(List<T> contingut) {
        return new PagedList<T>(
                contingut,
                PageRequest.of(0, contingut.size()),
                contingut.size()
        );
    }

    public static <T>PagedList<T> emptyPage() {
        return new PagedList<T>(
                new ArrayList<T>(),
                PageRequest.of(0, 10),
                0L
        );
    }

    public static <T>PagedList<T> emptyPage(int size) {
        return new PagedList<T>(
                new ArrayList<T>(),
                PageRequest.of(0, size),
                0L
        );
    }

    public static <T>PagedList<T> emptyPage(int page, int size) {
        return new PagedList<T>(
                new ArrayList<T>(),
                PageRequest.of(page, size),
                0L
        );
    }

}
