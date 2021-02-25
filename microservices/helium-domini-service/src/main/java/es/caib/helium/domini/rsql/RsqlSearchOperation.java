package es.caib.helium.domini.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Enumerat amb les operacions que es poden utilitzar en les expressions rsql
 * 
 * @author Limit Tecnologies
 */
@AllArgsConstructor
@Getter
public enum RsqlSearchOperation {

	EQUAL(RSQLOperators.EQUAL),
	NOT_EQUAL(RSQLOperators.NOT_EQUAL),
	GREATER_THAN(RSQLOperators.GREATER_THAN),
	GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),
	LESS_THAN(RSQLOperators.LESS_THAN),
	LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
	IN(RSQLOperators.IN),
	NOT_IN(RSQLOperators.NOT_IN),
	EQUAL_IGNORE_CASE(new ComparisonOperator("=ic=", false)),
	NOT_EQUAL_IGNORE_CASE(new ComparisonOperator("=nic=", false)),
	IS_NULL(new ComparisonOperator("=isnull=", false)),
	IS_NOT_NULL(new ComparisonOperator("=notnull=", false));

	final private ComparisonOperator operator;

	public static RsqlSearchOperation getSimpleOperator(final ComparisonOperator operator) {
		return Arrays.stream(values()).filter(op -> op.getOperator() == operator).findFirst().orElse(null);
	}

}
