package es.caib.helium.base.service;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import es.caib.helium.base.mapper._BaseMapper;
import es.caib.helium.base.model.DefaultOrder;
import es.caib.helium.base.model.DefaultSort;
import es.caib.helium.base.model.PagedList;
import es.caib.helium.base.repository._BaseRepository;
import es.caib.helium.base.rsql.CustomRsqlVisitor;
import es.caib.helium.base.rsql.RsqlSearchOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceHelper {

    public static <T> Page<T> getEntityPage(
            _BaseRepository repository,
            Specification spec,
            String filtreRsql,
            Pageable pageable,
            Sort sort,
            Class<?> dtoClass) {

        if (filtreRsql != null && !filtreRsql.isBlank()) {
            try {
                Specification<T> rsqlSpec = getRsqlSpecification(filtreRsql);
                if (spec != null)
                    spec = Specification.where(spec).and(rsqlSpec);
                else
                    spec = rsqlSpec;
            } catch (RSQLParserException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Filtre RSQL incorrecte (" + filtreRsql + "). Error: " + ex.getMessage());
            }
        }

        if (spec != null) {
            if (pageable.isUnpaged()) {
                sort = addDefaultSort(sort, dtoClass);
                List<T> resultList = repository.findAll(spec, sort);
                return new PageImpl(resultList, pageable, resultList.size());
            } else {
                return repository.findAll(spec, processPageable(pageable, dtoClass));
            }
        } else {
            if (pageable.isUnpaged()) {
                sort = addDefaultSort(sort, dtoClass);
                List<T> resultList = repository.findAll(sort);
                return new PageImpl(resultList, pageable, resultList.size());
            } else {
                return repository.findAll(processPageable(pageable, dtoClass));
            }
        }
    }

    public static <E, D> PagedList<D> getDtoPage(
            _BaseRepository repository,
            Specification spec,
            String filtreRsql,
            Pageable pageable,
            Sort sort,
            Class<?> dtoClass,
            _BaseMapper<E, D> mapper) {
        Page<E> page = getEntityPage(repository, spec, filtreRsql, pageable, sort, dtoClass);

        return  new PagedList<D>(
                page.getContent()
                        .stream()
                        .map(mapper::entityToDto)
                        .collect(Collectors.toList()),
//                PageRequest.of(
//                        dominiPage.getPageable().getPageNumber(),
//                        dominiPage.getPageable().getPageSize()),
                page.getPageable(),
                page.getTotalElements()
        );

    }

    private static Pageable processPageable(Pageable pageable, Class<?> dtoClass) {
        if (pageable.isPaged()) {
            Sort sort = addDefaultSort(pageable.getSort(), dtoClass);
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort);
        } else {
            return pageable;
        }
    }

    private static Sort addDefaultSort(Sort sort, Class<?> dtoClass) {
        if (sort == null || sort.isEmpty()) {
            DefaultSort defaultOrderAnnotation = dtoClass.getAnnotation(DefaultSort.class);
            if (defaultOrderAnnotation != null && defaultOrderAnnotation.sortFields().length > 0) {
                List<Sort.Order> orders = new ArrayList<Sort.Order>();
                for (DefaultOrder defOrder: defaultOrderAnnotation.sortFields()) {
                    orders.add(new Sort.Order(defOrder.direction(), defOrder.field()));
                }
                return Sort.by(orders);
            }
        }
        return sort;
    }

    private static <T>Specification<T> getRsqlSpecification(String rsqlFilter) {
        Set<ComparisonOperator> operators = RSQLOperators.defaultOperators();
        operators.add(RsqlSearchOperation.EQUAL_IGNORE_CASE.getOperator());
        operators.add(RsqlSearchOperation.NOT_EQUAL_IGNORE_CASE.getOperator());
        operators.add(RsqlSearchOperation.IS_NULL.getOperator());
        operators.add(RsqlSearchOperation.IS_NOT_NULL.getOperator());
        Node rootNode = new RSQLParser(operators).parse(rsqlFilter.toString());
        Specification<T> spec = rootNode.accept(new CustomRsqlVisitor<T>());
        return spec;
    }
}
