package es.caib.helium.domini.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedSortedRequest implements Serializable {

    private Integer page;
    private Integer size;
    private List<String> sort;


    public Pageable getPageable() {
        if (isUnPaged()) {
            return Pageable.unpaged();
        } else {
            return PageRequest.of(this.page, this.size, getSort());
        }
    }

    public Sort getSort() {
        if (isUnSorted()) {
            return Sort.unsorted();
        } else {
            return Sort.by(
                    this.sort.stream()
                            .filter(o -> o != null && !o.isBlank())
                            .map(o -> {
                                String[] fields = o.split(":");

                                String propietat = fields[0].trim();
                                if (fields.length == 1) {
                                    return Sort.Order.by(fields[0]);
                                } else if (fields.length == 2) {
                                    String direccio = fields[1].trim();
                                    if ("ASC".equalsIgnoreCase(direccio)) {
                                        return Sort.Order.asc(propietat);
                                    } else if ("DESC".equalsIgnoreCase(direccio)) {
                                        return Sort.Order.desc(propietat);
                                    }
                                }
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Format d'ordre incorrecte. El format ha de ser: PROPIETAT[:[ASC|DESC]]");
                            })
                            .collect(Collectors.toList())
            );
        }
    }

    public boolean isUnPaged() {
        return this.page == null || this.size== null || this.size <= 0;
    }

    private boolean isUnSorted() {
        return sort == null || sort.isEmpty();
    }

}
