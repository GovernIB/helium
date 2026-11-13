package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a aturar la tramitació d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientAturarHandler {
	void setMotiu(Expression motiu);
	void setVarMotiu(Expression varMotiu);
}
