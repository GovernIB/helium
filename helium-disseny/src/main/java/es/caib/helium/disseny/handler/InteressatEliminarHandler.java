package es.caib.helium.disseny.handler;

import org.flowable.common.engine.api.delegate.Expression;

/**
 * Handler per a eliminar interessats d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface InteressatEliminarHandler {
	void setCodi(Expression codi);
	void setVarCodi(Expression varCodi);
}
