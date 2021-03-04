package es.caib.helium.domini.rsql;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 
 * 
 * @author Limit Tecnologies
 */
public class GenericRsqlSpecBuilder<T> {

	public Specification<T> createSpecification(final Node node) {
		if (node instanceof LogicalNode) {
			return createSpecification((LogicalNode)node);
		}
		if (node instanceof ComparisonNode) {
			return createSpecification((ComparisonNode)node);
		}
		return null;
	}

	private Specification<T> createSpecification(final LogicalNode logicalNode) {
		List<Specification<T>> specs = logicalNode.getChildren().stream().
				map(this::createSpecification).
				filter(Objects::nonNull).
				collect(Collectors.toList());
		Specification<T> result = specs.get(0);
		if (logicalNode.getOperator() == LogicalOperator.AND) {
			for (Specification<T> spec: specs) {
				result = Specification.where(result).and(spec);
			}
		} else if (logicalNode.getOperator() == LogicalOperator.OR) {
			for (Specification<T> spec: specs) {
				result = Specification.where(result).or(spec);
			}
		}
		return result;
	}

	private Specification<T> createSpecification(final ComparisonNode comparisonNode) {
		return Specification.where(
				new GenericRsqlSpecification<T>(
						comparisonNode.getSelector(),
						comparisonNode.getOperator(),
						comparisonNode.getArguments()));
	}

}
