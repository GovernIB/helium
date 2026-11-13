package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a modificar el títol d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientTitolModificarHandler {
	void setTitol(Expression titol);
	void setVarTitol(Expression varTitol);
}
