package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.el.FixedValue;

/**
 * Handler per a tornar a obrir un expedient finalitzat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientDesfinalitzarHandler {
	void setReprendre(Expression reprendre);
	void setVarReprendre(Expression varReprendre);
}
