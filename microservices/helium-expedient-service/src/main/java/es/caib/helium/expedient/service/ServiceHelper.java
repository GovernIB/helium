package es.caib.helium.expedient.service;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import es.caib.helium.ms.mapper.BaseMapper;
import es.caib.helium.ms.model.DefaultOrder;
import es.caib.helium.ms.model.DefaultSort;
import es.caib.helium.ms.model.PagedList;
import es.caib.helium.ms.repository.BaseRepository;
import es.caib.helium.ms.rsql.CustomRsqlVisitor;
import es.caib.helium.ms.rsql.RsqlSearchOperation;

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

    public static <E, PK> Page<E> getEntityPage(
            BaseRepository<E, PK> repository,
            Specification<E> spec,
            String filtreRsql,
            Pageable pageable,
            Sort sort) {

        spec = joinRsqlAndSpecification(spec, filtreRsql);

        if (spec != null) {
            if (pageable.isUnpaged()) {
                List<E> resultList = repository.findAll(spec, sort);
                return new PageImpl<>(resultList, pageable, resultList.size());
            } else {
                return repository.findAll(spec, pageable);
            }
        } else {
            if (pageable.isUnpaged()) {
                List<E> resultList = repository.findAll(sort);
                return new PageImpl<>(resultList, pageable, resultList.size());
            } else {
                return repository.findAll(pageable);
            }
        }
    }

    public static <E, PK> Page<E> getEntityPage(
            BaseRepository<E, PK> repository,
            Specification<E> spec,
            String filtreRsql,
            Pageable pageable,
            Sort sort,
            Class<?> classWithSort) {

        spec = joinRsqlAndSpecification(spec, filtreRsql);

        if (spec != null) {
            if (pageable.isUnpaged()) {
                sort = addDefaultSort(sort, classWithSort);
                List<E> resultList = repository.findAll(spec, sort);
                return new PageImpl<>(resultList, pageable, resultList.size());
            } else {
                return repository.findAll(spec, processPageable(pageable, classWithSort));
            }
        } else {
            if (pageable.isUnpaged()) {
                sort = addDefaultSort(sort, classWithSort);
                List<E> resultList = repository.findAll(sort);
                return new PageImpl<>(resultList, pageable, resultList.size());
            } else {
                return repository.findAll(processPageable(pageable, classWithSort));
            }
        }
    }

    public static <E, D, PK> PagedList<D> getDtoPage(
            BaseRepository<E, PK> repository,
            Specification<E> spec,
            String filtreRsql,
            Pageable pageable,
            Sort sort,
            Class<?> dtoClass,
            BaseMapper<E, D> mapper) {
        Page<E> page = getEntityPage(repository, spec, filtreRsql, pageable, sort, dtoClass);

        return new PagedList<>(
                page.getContent()
                        .stream()
                        .map(mapper::entityToDto)
                        .collect(Collectors.toList()),
                page.getPageable(),
                page.getTotalElements()
        );

    }

    public static <T>Specification<T> getRsqlSpecification(String rsqlFilter) {
        Set<ComparisonOperator> operators = RSQLOperators.defaultOperators();
        operators.add(RsqlSearchOperation.EQUAL_IGNORE_CASE.getOperator());
        operators.add(RsqlSearchOperation.NOT_EQUAL_IGNORE_CASE.getOperator());
        operators.add(RsqlSearchOperation.IS_NULL.getOperator());
        operators.add(RsqlSearchOperation.IS_NOT_NULL.getOperator());
        Node rootNode = new RSQLParser(operators).parse(rsqlFilter);
        return rootNode.accept(new CustomRsqlVisitor<>());
    }

    public static <E> Specification<E> joinRsqlAndSpecification(Specification<E> spec, String filtreRsql) {
        if (filtreRsql != null && !filtreRsql.isBlank()) {
            try {
                Specification<E> rsqlSpec = getRsqlSpecification(filtreRsql);
                if (spec != null)
                    spec = Specification.where(spec).and(rsqlSpec);
                else
                    spec = rsqlSpec;
            } catch (RSQLParserException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. Filtre RSQL incorrecte (" + filtreRsql + "). Error: " + ex.getMessage());
            }
        }
        return spec;
    }

    private static Pageable processPageable(Pageable pageable, Class<?> classWithSort) {
        if (pageable.isPaged()) {
            Sort sort = addDefaultSort(pageable.getSort(), classWithSort);
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    sort);
        } else {
            return pageable;
        }
    }

    private static Sort addDefaultSort(Sort sort, Class<?> classWithSort) {
        if (sort == null || sort.isEmpty()) {
            DefaultSort defaultOrderAnnotation = classWithSort.getAnnotation(DefaultSort.class);
            if (defaultOrderAnnotation != null && defaultOrderAnnotation.sortFields().length > 0) {
                List<Sort.Order> orders = new ArrayList<>();
                for (DefaultOrder defOrder: defaultOrderAnnotation.sortFields()) {
                    orders.add(new Sort.Order(defOrder.direction(), defOrder.field()));
                }
                return Sort.by(orders);
            }
        }
        return sort;
    }
}
