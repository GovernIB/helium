package es.caib.helium.domini.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * 
 * @author Limit Tecnologies
 */
@Slf4j
@AllArgsConstructor
public class GenericRsqlSpecification<T> implements Specification<T> {

	private final String property;
	private final ComparisonOperator operator;
	private final List<String> arguments;

	@Override
	public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {

		List<Object> args = castArguments(root);
		final Object argument = args.get(0);

		switch (RsqlSearchOperation.getSimpleOperator(operator)) {
		case EQUAL:
			if (argument instanceof String) {
				return builder.like(root.get(property), argument.toString().replace('*', '%'));
			} else if (argument == null) {
				return builder.isNull(root.get(property));
			} else {
				return builder.equal(root.get(property), argument);
			}
		case NOT_EQUAL:
			if (argument instanceof String) {
				return builder.notLike(root.get(property), argument.toString().replace('*', '%'));
			} else if (argument == null) {
				return builder.isNotNull(root.get(property));
			} else {
				return builder.notEqual(root.get(property), argument);
			}
		case GREATER_THAN:
			return builder.greaterThan(root.get(property), argument.toString());
		case GREATER_THAN_OR_EQUAL:
			return builder.greaterThanOrEqualTo(root.get(property), argument.toString());
		case LESS_THAN:
			return builder.lessThan(root.get(property), argument.toString());
		case LESS_THAN_OR_EQUAL:
			return builder.lessThanOrEqualTo(root.get(property), argument.toString());
		case IN:
			return root.get(property).in(args);
		case NOT_IN:
			return builder.not(root.get(property).in(args));
		case EQUAL_IGNORE_CASE:
			if (argument instanceof String) {
				return builder.like(builder.lower(root.get(property)), argument.toString().toLowerCase().replace('*', '%'));
			} else if (argument == null) {
				return builder.isNull(root.get(property));
			} else {
				return builder.equal(root.get(property), argument);
			}
		case NOT_EQUAL_IGNORE_CASE:
			if (argument instanceof String) {
				return builder.notLike(builder.lower(root.get(property)), argument.toString().toLowerCase().replace('*', '%'));
			} else if (argument == null) {
				return builder.isNotNull(root.get(property));
			} else {
				return builder.notEqual(root.get(property), argument);
			}
		case IS_NULL:
			return builder.isNull(root.get(property));
		case IS_NOT_NULL:
			return builder.isNotNull(root.get(property));
		default:
			break;
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Object> castArguments(final Root<T> root) {

		Class<?> type = root.get(property).getJavaType();

		return arguments.stream().map(argument -> {
			if (type.equals(Integer.class) || type.equals(int.class)) {
				return Integer.parseInt(argument);
			} else if (type.equals(Long.class) || type.equals(long.class)) {
				return Long.parseLong(argument);
			} else if (type.equals(Date.class)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				try {
					return sdf.parse(argument);
				} catch (ParseException ex) {
					log.error("Error al parsejar la data amb el format yyyy-MM-dd'T'HH:mm:ss.SSSZ", ex);
					return argument;
				}
			} else if (type.equals(LocalDate.class)) {
				return LocalDate.parse(argument, DateTimeFormatter.ISO_LOCAL_DATE);
			} else if (type.equals(LocalTime.class)) {
				return LocalTime.parse(argument, DateTimeFormatter.ISO_LOCAL_TIME);
			} else if (type.equals(LocalDateTime.class)) {
				return LocalDateTime.parse(argument, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
				return Boolean.valueOf(argument);
			} else if (type.isEnum()) {
				return Enum.valueOf((Class<Enum>)type, argument);
			} else {
				return argument;
			}
		}).collect(Collectors.toList());
	}

}
